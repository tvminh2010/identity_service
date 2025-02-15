package zve.com.vn.configuration;

import java.text.ParseException;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import zve.com.vn.service.AuthenticationService;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {

	@Autowired
	private AuthenticationService authenticationService;
	@Value("${jwt.signerkey}")
	private String signerKey;
	private NimbusJwtDecoder nimbusJwtDecoder;
	/* ------------------------------------------------------------- */
    @PostConstruct
    public void init() {
        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(signerKey.getBytes(), "HmacSHA512"))
                .macAlgorithm(MacAlgorithm.HS512).build();
    }
    /* ------------------------------------------------------------- */
	@Override
	public Jwt decode(String token) throws JwtException {
		try {
			authenticationService.verifyToken(token);
		} catch (JOSEException | ParseException e) {
			throw new JwtException("Failed to parse token", e);
		}
		 return nimbusJwtDecoder.decode(token);
	}
	/* ------------------------------------------------------------- */
}
