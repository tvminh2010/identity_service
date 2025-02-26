package zve.com.vn.configuration;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import zve.com.vn.entity.Role;
import zve.com.vn.entity.User;
import zve.com.vn.repository.UserRepository;

@Configuration
@EnableScheduling
@Slf4j
@PropertySource("classpath:foo.properties")
public class ApplicationInitConfig {

  private final PasswordEncoder passwordEncoder;

  @Value("${admin.default-password}")
  private String defaultAdminPassword;

  @Value("${java.specification.version}")
  private String javaVersion;

  @Value("${ho.ten}")
  private String testAuthoName;

  @Autowired
  public ApplicationInitConfig(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  /* ------------------------------------------------------------------- */
  @Bean
  @ConditionalOnProperty(
      prefix = "spring",
      value = "datasource.driverClassName",
      havingValue = "com.microsoft.sqlserver.jdbc.SQLServerDriver")
  ApplicationRunner applicationRunner(UserRepository userRepository) {
    return args -> {
      if (userRepository.findByUsername("admin").isEmpty()) {

        Role role = new Role();
        role.setName("ADMIN");

        Set<Role> roles = Set.of(role); // Sử dụng Set.of() thay vì HashSet

        User user =
            User.builder()
                .username("admin")
                .firstName("Administrator")
                .password(passwordEncoder.encode(defaultAdminPassword))
                .roles(roles)
                .build();

        userRepository.save(user);
        log.warn(
            "Admin user have bean created with defaut password: ******* " + defaultAdminPassword);
      }
    };
  }

  /* ------------------------------------------------------------------- */
}
