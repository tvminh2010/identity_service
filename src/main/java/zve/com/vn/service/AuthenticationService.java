package zve.com.vn.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;
import zve.com.vn.dto.request.AuthenticationRequest;
import zve.com.vn.dto.request.IntrospectRequest;
import zve.com.vn.dto.request.LogoutRequest;
import zve.com.vn.dto.request.RefreshRequest;
import zve.com.vn.dto.response.AuthenticationRestponse;
import zve.com.vn.dto.response.IntrospectRestponse;
import zve.com.vn.entity.InvalidateToken;
import zve.com.vn.entity.Permission;
import zve.com.vn.entity.User;
import zve.com.vn.enums.ErrorCode;
import zve.com.vn.exception.CustomAppException;
import zve.com.vn.repository.InvalidateRepository;
import zve.com.vn.repository.UserRepository;

@Service
@Slf4j
public class AuthenticationService {

  @Autowired UserRepository userRepository;

  @Autowired InvalidateRepository invalidateRepository;

  @Value("${jwt.signer-key}")
  private String signerKey;

  @Value("${jwt.valid-duration}")
  private long validDuration;

  @Value("${jwt.refreshable-duration}")
  private long refreshableDuration;

  /* -------------------------------------------------------- */
  public AuthenticationRestponse authenticateJwt(AuthenticationRequest request) {
    var user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new CustomAppException(ErrorCode.USER_NOT_EXISTED));
    boolean authenticated = BCrypt.checkpw(request.getPassword(), user.getPassword());

    if (!authenticated) {
      throw new CustomAppException(ErrorCode.UN_AUTHENTICATED);
    }
    return new AuthenticationRestponse(authenticated, generateToken(user));
  }

  /* -------------------------------------------------------- */
  public IntrospectRestponse validate(IntrospectRequest request)
      throws JOSEException, ParseException {
    try {
      verifyToken(request.getToken(), false);
      return IntrospectRestponse.builder().valid(true).build();
    } catch (CustomAppException e) {
      return IntrospectRestponse.builder().valid(false).build();
    } catch (Exception e) {
      return IntrospectRestponse.builder().valid(false).build();
    }
  }

  /* -------------------------------------------------------- */
  public IntrospectRestponse validate2(IntrospectRequest request)
      throws JOSEException, ParseException {
    verifyToken(request.getToken(), false);
    return IntrospectRestponse.builder().valid(true).build();
  }

  /* -------------------------------------------------------- */
  public void logout(LogoutRequest request) throws JOSEException, ParseException {
    var signedToken =
        verifyToken(
            request.getToken(),
            true); // Kiểm tra token nhập vào, và lấy giá trị của token đã ký signedToken
    String jit = signedToken.getJWTClaimsSet().getJWTID();
    Date expiredDate = signedToken.getJWTClaimsSet().getExpirationTime();

    if (isTokenBlacklisted(jit)) {
      throw new CustomAppException(ErrorCode.TOKEN_LOGGED_OUT);
    }

    invalidateRepository.save(new InvalidateToken(jit, expiredDate));
    SecurityContextHolder.clearContext();
  }

  /* -------------------------------------------------------- */
  public AuthenticationRestponse refreshToken(RefreshRequest request)
      throws JOSEException, ParseException {
    SignedJWT signedJWT = verifyToken(request.getToken(), true);
    String jit = signedJWT.getJWTClaimsSet().getJWTID(); // Lấy jit của token muốn refresh

    Date expiredDate =
        signedJWT.getJWTClaimsSet().getExpirationTime(); // Lấy expiredDate của token muốn refresh
    invalidateRepository.save(new InvalidateToken(jit, expiredDate)); // Lưu xuống blacklist db

    var username = signedJWT.getJWTClaimsSet().getSubject();
    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new CustomAppException(ErrorCode.USER_NOT_EXISTED));

    return AuthenticationRestponse.builder() // Tạo token mới và trả về AuthenticationRestponse
        .token(generateToken(user))
        .authenticated(true)
        .build();
  }

  /* -------------------------------------------------------- */
  public SignedJWT verifyToken(String token, boolean isRefresh)
      throws JOSEException, ParseException {
    JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
    SignedJWT signedJWT = SignedJWT.parse(token);
    boolean verified = signedJWT.verify(verifier);

    if (!verified) {
      throw new CustomAppException(ErrorCode.INVALID_TOKEN);
    }

    Date expirationTime =
        isRefresh
            ? new Date(
                signedJWT
                    .getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(refreshableDuration, ChronoUnit.SECONDS)
                    .toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime();

    if (expirationTime == null || expirationTime.before(new Date())) {
      throw new CustomAppException(ErrorCode.TOKEN_EXPIRE);
    }

    if (isTokenBlacklisted(signedJWT.getJWTClaimsSet().getJWTID())) {
      throw new CustomAppException(ErrorCode.TOKEN_LOGGED_OUT);
    }

    return signedJWT;
  }

  /* -------------------------------------------------------- */
  public boolean isTokenValid(String token) throws JOSEException, ParseException {
    JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
    SignedJWT signedJWT = SignedJWT.parse(token);
    return signedJWT.verify(verifier);
  }

  /* -------------------------------------------------------- */
  // Kiểm tra xem token có được lưu vào CSDL sau khi logout ko?
  public boolean isTokenBlacklisted(String jwtId) {
    return invalidateRepository.existsById(jwtId);
  }

  /* -------------------------------------------------------- */
  private String generateToken(User user) {
    JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512); // Tạo đối tượng JWSHeader
    JWTClaimsSet jwtClaimSet =
        new JWTClaimsSet.Builder()
            .subject(user.getUsername()) // Xác định tiêu đề của token là username đăng nhập
            .issuer("trinhvanminh.net") // Người cấp token, thường là domain của mình
            .issueTime(new Date()) // Thời gian cấp, lấy thời gian hiện tại
            .expirationTime(
                new Date(
                    Instant.now()
                        .plus(validDuration, ChronoUnit.SECONDS)
                        .toEpochMilli())) // Thời gian hết hạn tocken sau 1h
            .jwtID(UUID.randomUUID().toString()) // Đặt ID cho token
            .claim("scope", getUserRolesAndPermissions(user))
            .build();
    Payload payload = new Payload(jwtClaimSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(jwsHeader, payload); // Tạo xong object token

    try {
      jwsObject.sign(new MACSigner(signerKey.getBytes())); // Ký Object token với thuật toán
      return jwsObject.serialize();
    } catch (JOSEException e) {
      log.error("Cannot generate token", e);
      throw new CustomAppException(ErrorCode.TOKEN_COULDNOT_GENERATE);
    }
  }

  /* -------------------------------------------------------- */
  private String getUserRolesAndPermissions(User user) {
    return Optional.ofNullable(user.getRoles()) // Tránh lỗi NullPointerException
        .orElse(Collections.emptySet()) // nếu user.getRoles() là null trả về tập rỗng
        .stream() // Duyệt qua từng role và lấy tên
        .flatMap(
            role ->
                Stream.concat(
                    Stream.of("ROLE_" + role.getName()), // Tạo một Stream chỉ chứa tên Role
                    Optional.ofNullable(role.getPermissions())
                        .orElse(Collections.emptySet())
                        .stream()
                        .map(Permission::getName)))
        .collect(Collectors.joining(" "));
  }

  /* -------------------------------------------------------- */
  // @Scheduled(cron = "0 0 0 * * ?") 				// Chạy vào 0h mỗi ngày
  // @Scheduled(cron = "0 0 * * * ?") 				// Chạy mỗi giờ
  @Scheduled(fixedRate = 60000) // Chạy mỗi 60 giây
  @Transactional
  public void cleanExpiredTokens() {
    invalidateRepository.deleteByExpireTimeBefore(new Date());
  }
  /* -------------------------------------------------------- */
}
