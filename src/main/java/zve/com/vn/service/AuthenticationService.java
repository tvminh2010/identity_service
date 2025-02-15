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

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import zve.com.vn.dto.request.AuthenticationRequest;
import zve.com.vn.dto.request.IntrospectRequest;
import zve.com.vn.dto.request.LogoutRequest;
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
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	InvalidateRepository invalidateRepository;
	
	@NonFinal	//Ko cho phép inject vào constructor
	@Value("${jwt.signerkey}")
	protected /* static final*/String SIGNER_KEY; //= "YvAD7YK1Xx+EitDRWO6lwao4SMtLuFaQamhBNXniy7c3PAbgpXo0BgVDBakWnABZ";

	/* -------------------------------------------------------- */
	public AuthenticationRestponse authenticateJwt(AuthenticationRequest request) {
		var user =  userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new CustomAppException (ErrorCode.USER_NOT_EXISTED));
		boolean authenticated = BCrypt.checkpw(request.getPassword(), user.getPassword());
		
		
		if(!authenticated) {
			throw new CustomAppException(ErrorCode.UN_AUTHENTICATED);
		}
		return new AuthenticationRestponse(authenticated, generateToken(user));
	}
	/* -------------------------------------------------------- */
	public IntrospectRestponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
	    try {
	        verifyToken(request.getToken());
	        return IntrospectRestponse.builder()
	                .valid(true).build();
	    } catch (CustomAppException e) {
	        return IntrospectRestponse.builder()
	                .valid(false).build();
	    } catch (Exception e) {
	        return IntrospectRestponse.builder()
	                .valid(false).build();
	    }
	}
	/* -------------------------------------------------------- */
	public IntrospectRestponse introspect2(IntrospectRequest request) throws JOSEException, ParseException {
	    verifyToken(request.getToken());
	    return IntrospectRestponse.builder().valid(true).build();
	}
	/* -------------------------------------------------------- */
	public void logout (LogoutRequest request) throws JOSEException, ParseException {
		var signedToken = verifyToken(request.getToken());
		String jit 			= signedToken.getJWTClaimsSet().getJWTID();
		Date expiredDate	= signedToken.getJWTClaimsSet().getExpirationTime();
		
		 if (isTokenBlacklisted(jit)) {
		        throw new CustomAppException(ErrorCode.TOKEN_LOGGED_OUT);
		 }
		
		 invalidateRepository.save(new InvalidateToken(jit, expiredDate));
	     SecurityContextHolder.clearContext();
	}
	/* -------------------------------------------------------- */
	public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		boolean verified = signedJWT.verify(verifier);
		
		 if (!verified) {
		        throw new CustomAppException(ErrorCode.INVALID_TOKEN);
	    }
		Date expirationTime  =  signedJWT.getJWTClaimsSet().getExpirationTime();
		
		
	    if (expirationTime == null || expirationTime.before(new Date())) {
	        throw new CustomAppException(ErrorCode.TOKEN_EXPIRE);
	    }   
	    
	    if(isTokenBlacklisted(signedJWT.getJWTClaimsSet().getJWTID())) {
	    	 throw new CustomAppException(ErrorCode.TOKEN_LOGGED_OUT);
	    }
	    
		return signedJWT;
	}
	/* -------------------------------------------------------- */
	 public boolean isTokenBlacklisted(String jwtId) {
		 return invalidateRepository.existsById(jwtId);
	 }
	/* -------------------------------------------------------- */
	private String generateToken(User user) {
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);									//Tạo đối tượng JWSHeader
		JWTClaimsSet jwtClaimSet = new JWTClaimsSet.Builder()
				.subject(user.getUsername())														//Xác định tiêu đề của token là username đăng nhập
				.issuer("trinhvanminh.net")															//Người cấp token, thường là domain của mình
				.issueTime(new Date())																//Thời gian cấp, lấy thời gian hiện tại
				.expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))	//Thời gian hết hạn tocken sau 1h
				.jwtID(UUID.randomUUID().toString())												//Đặt ID cho token											
				.claim("scope", getUserRolesAndPermissions(user))
				.build();		
		Payload payload = new Payload(jwtClaimSet.toJSONObject());
		
		JWSObject jwsObject = new JWSObject(jwsHeader, payload);									//Tạo xong object token
		
		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));									//Ký Object token với thuật toán
			return jwsObject.serialize();
		} catch (JOSEException e) {
			log.error("Cannot generate token", e);
			throw new RuntimeException(e);
		}										
	}
	/* -------------------------------------------------------- */
	private String getUserRolesAndPermissions(User user) {
	    return Optional.ofNullable(user.getRoles())		//Tránh lỗi NullPointerException 
	            .orElse(Collections.emptySet())			//nếu user.getRoles() là null trả về tập rỗng
	            .stream()  								//Duyệt qua từng role và lấy tên
	            .flatMap(
	               role -> Stream.concat(Stream.of("ROLE_" + role.getName()),		// Tạo một Stream chỉ chứa tên Role
                   Optional.ofNullable(role.getPermissions()).orElse(Collections.emptySet()).stream().map(Permission::getName)))
	            .collect(Collectors.joining(" ")); 
	}
	/* -------------------------------------------------------- */
}
