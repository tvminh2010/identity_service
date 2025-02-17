package zve.com.vn.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration						//Annotation dùng để tạo bean cho các method của class này, Class này cũng là 1 bean						
@EnableWebSecurity					//Mặc định đã enable rồi, nên có thể có hoặc ko annotation này
@EnableMethodSecurity				//Kích hoạt Security trên method trong UserService class
public class SecurityConfig {

    private final String[] POST_PUBLIC_ENDPOINTS = {
            "/users", 
            "/auth/token", 
            "/auth/introspect", 
            "/auth/validatetoken",
            "/auth/log-in", 
            "/auth/logout", 
            "/auth/refresh",
            "/auth/logout"
    };
	
    private final String[] GET_PUBLIC_ENDPOINTS = {
            "/home", 
            "/", 
            "/swagger-ui/**", 
            "/actuator/**"
    };
    
	@Autowired
	JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
    @Autowired
    private CustomJwtDecoder customJwtDecoder;
	/* --------------------------------------------------------------------- */
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(
			linhtinh -> linhtinh
			.requestMatchers(HttpMethod.POST, POST_PUBLIC_ENDPOINTS).permitAll()
			.requestMatchers(HttpMethod.GET, GET_PUBLIC_ENDPOINTS).permitAll()
			//.requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN") 		
				//.hasAnyAuthority("SCOPE_ADMIN", "SCOPE_USER")
			//.requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name()) 
			.anyRequest().authenticated());
				
		
		//Thực hiện decode token của request và tiến hành xác thực user
		httpSecurity.oauth2ResourceServer(linhtinh -> 
			linhtinh.jwt(linhtinh2 -> linhtinh2.decoder(customJwtDecoder)
				.jwtAuthenticationConverter(jwtAuthenticationConverter()))	
				.authenticationEntryPoint(jwtAuthenticationEntryPoint));
		httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
		return httpSecurity.build();
    }
	/* --------------------------------------------------------------------- */
	//hàm đổi prefix của role từ SCOPE_ sang ROLE_
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	} 
	/* --------------------------------------------------------------------- */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	/* --------------------------------------------------------------------- */
	 
}
