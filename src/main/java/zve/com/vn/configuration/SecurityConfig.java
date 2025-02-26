package zve.com.vn.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration // Annotation dùng để tạo bean cho các method của class này, Class này cũng là 1 bean
@EnableWebSecurity // Mặc định đã enable rồi, nên có thể có hoặc ko annotation này
@EnableMethodSecurity // Kích hoạt Security trên method trong UserService class
public class SecurityConfig {

  private static final String[] POST_PUBLIC_ENDPOINTS = {
    "/users",
    "/auth/token",
    "/auth/introspect",
    "/auth/validatetoken",
    "/auth/log-in",
    "/auth/logout",
    "/auth/refresh",
    "/auth/logout"
  };

  private static final String[] GET_PUBLIC_ENDPOINTS = {
    "/home", "/", "/swagger-ui/**", "/actuator/**"
  };

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final CustomJwtDecoder customJwtDecoder;

  public SecurityConfig(
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, CustomJwtDecoder customJwtDecoder) {
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.customJwtDecoder = customJwtDecoder;
  }

  /* --------------------------------------------------------------------- */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(
        linhtinh ->
            linhtinh
                .requestMatchers(HttpMethod.POST, POST_PUBLIC_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, GET_PUBLIC_ENDPOINTS)
                .permitAll()
                // .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
                // .hasAnyAuthority("SCOPE_ADMIN", "SCOPE_USER")
                // .requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name())
                .anyRequest()
                .authenticated());

    // Thực hiện decode token của request và tiến hành xác thực user
    httpSecurity.oauth2ResourceServer(
        linhtinh ->
            linhtinh
                .jwt(
                    linhtinh2 ->
                        linhtinh2
                            .decoder(customJwtDecoder)
                            .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(jwtAuthenticationEntryPoint));
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    return httpSecurity.build();
  }

  /* --------------------------------------------------------------------- */
  // hàm đổi prefix của role từ SCOPE_ sang ROLE_
  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
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
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of("http://localhost:3000", "https://localhost:4200")); // Chỉ định origin
    configuration.setAllowedMethods(
        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Phương thức HTTP cho phép
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Header cho phép
    configuration.setAllowCredentials(true); // Cho phép gửi cookie hoặc thông tin xác thực

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả endpoint
    return source;
  }
  /* --------------------------------------------------------------------- */
}
