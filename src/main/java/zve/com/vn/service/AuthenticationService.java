package zve.com.vn.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
import zve.com.vn.dto.response.AuthenticationRestponse;
import zve.com.vn.dto.response.IntrospectRestponse;
import zve.com.vn.entity.User;
import zve.com.vn.enums.ErrorCode;
import zve.com.vn.exception.CustomAppException;
import zve.com.vn.repository.UserRepository;

@Service
@Slf4j
public class AuthenticationService {
	
	@Autowired
	UserRepository userRepository;
	
	@NonFinal	//Ko cho phép inject vào constructor
	@Value("${jwt.signerkey}")
	protected /* static final*/String SIGNER_KEY; //= "YvAD7YK1Xx+EitDRWO6lwao4SMtLuFaQamhBNXniy7c3PAbgpXo0BgVDBakWnABZ";

	/* -------------------------------------------------------- */
	public boolean authenticate(AuthenticationRequest request)  {
		var user =  userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new CustomAppException (ErrorCode.USER_NOT_EXISTED));
		return BCrypt.checkpw(request.getPassword(), user.getPassword());
	}
	/* -------------------------------------------------------- */
	
	public AuthenticationRestponse authenticateJwt(AuthenticationRequest request) {
		var user =  userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new CustomAppException (ErrorCode.UN_AUTHENTICATED));
		boolean authenticated = BCrypt.checkpw(request.getPassword(), user.getPassword());
		
		AuthenticationRestponse authenticationResponse = new AuthenticationRestponse();
		authenticationResponse.setAuthenticated(authenticated);
		if(!authenticated) {
			throw new CustomAppException(ErrorCode.UN_AUTHENTICATED);
		}
		var token = generateToken(user);
		authenticationResponse.setToken(token);
		
		return authenticationResponse;
	}
	/* -------------------------------------------------------- */
	private String generateToken(User user) {
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);									//Tạo đối tượng JWSHeader
		
		//Tạo đối tượng JWTClaimsSet, là payload của token
		JWTClaimsSet jwtClaimSet = new JWTClaimsSet.Builder()
				.subject(user.getUsername())														//Xác định tiêu đề của token là username đăng nhập
				.issuer("trinhvanminh.net")															//Người cấp token, thường là domain của mình
				.issueTime(new Date())																//Thời gian cấp, lấy thời gian hiện tại
				.expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))	//Thời gian hết hạn tocken sau 1h
				.claim("scope", buidScope(user))
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
	public IntrospectRestponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		boolean /*var*/ verified = signedJWT.verify(verifier);
		
		Date expityTime =  signedJWT.getJWTClaimsSet().getExpirationTime();
		
		return IntrospectRestponse.builder()
			.valid(verified && expityTime.after(new Date()))
			.build();
	}
	/* -------------------------------------------------------- */
	private String buidScope(User user) {
		StringJoiner stringJoiner = new StringJoiner("");
		
		//if (!CollectionUtils.isEmpty(user.getRoles())) {
			//user.getRoles().forEach(s -> stringJoiner.add(s));}
		
		return stringJoiner.toString();
	}
	/* -------------------------------------------------------- */
}
