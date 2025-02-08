package zve.com.vn.configuration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration						//Annotation dùng để tạo bean cho các method của class này, Class này cũng là 1 bean						
@EnableWebSecurity					//Mặc định đã enable rồi, nên có thể có hoặc ko annotation này
@EnableMethodSecurity				//Kích hoạt Security trên method trong UserService class
public class SecurityConfig {

	@Value("${jwt.signerkey}")
	private String signerKey;
	
	@Autowired
	JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	/* --------------------------------------------------------------------- */
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(
				linhtinh -> linhtinh
				.requestMatchers(HttpMethod.POST, "/users").permitAll()
				.requestMatchers(HttpMethod.GET, "/users").permitAll()
				.requestMatchers(HttpMethod.POST, "/auth/log-in", "/auth/introspect").permitAll()
				.requestMatchers(HttpMethod.GET, "/home", "/", "/swagger-ui/**", "/actuator/**").permitAll()
				//.requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN") 		//.hasAnyAuthority("SCOPE_ADMIN", "SCOPE_USER")
				//.requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name()) 
				.anyRequest().authenticated());
		
		//Thực hiện decode token của request và tiến hành xác thực user
		httpSecurity.oauth2ResourceServer(linhtinh -> 
			linhtinh.jwt(linhtinh2 -> linhtinh2.decoder(jwtDecoder())
					.jwtAuthenticationConverter(jwtAuthenticationConverter()))	
					.authenticationEntryPoint(jwtAuthenticationEntryPoint)
					);
		
		//Hủy chế độ csrf của app sau khi đã enable security
		httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
		return httpSecurity.build();
    }
	/* --------------------------------------------------------------------- */
	//hàm đổi prefix của role từ SCOPE_ sang ROLE_
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	} 
	/* --------------------------------------------------------------------- */
	@Bean
	JwtDecoder jwtDecoder() {
		SecretKeySpec secretKeyspec = new SecretKeySpec(signerKey.getBytes(), "HS512");
		NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder
				.withSecretKey(secretKeyspec)
				.macAlgorithm(MacAlgorithm.HS512)
				.build();
		return nimbusJwtDecoder;
	}
	/* --------------------------------------------------------------------- */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	/* --------------------------------------------------------------------- */
	 
}
