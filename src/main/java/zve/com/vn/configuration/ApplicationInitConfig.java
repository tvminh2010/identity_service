package zve.com.vn.configuration;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import zve.com.vn.entity.Role;
import zve.com.vn.entity.User;
import zve.com.vn.repository.UserRepository;

@Configuration
@Slf4j
@PropertySource("classpath:foo.properties")
public class ApplicationInitConfig {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${admin.default-password}")
	private String defaultAdminPassword;
	
	@Value("${java.specification.version}")
	private String javaVersion;
	
	@Value("${ho.ten}")
	private String testAuthoName;
	
	/* ------------------------------------------------------------------- */
	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository) {

		System.out.println("*******************************************");
			System.out.println("Java version là: " + javaVersion);
		System.out.println("*******************************************");
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {

				Role role = new Role();
				role.setName("ADMIN");

				Set<Role> roles = Set.of(role); // Sử dụng Set.of() thay vì HashSet
				
				User user = User.builder()
					.username("admin")
					.firstName("Administrator")
					.password(passwordEncoder.encode(defaultAdminPassword))
					.roles(roles)
					.build();
				
				userRepository.save(user);
				log.warn("Admin user have bean created with defaut password: ******* " + defaultAdminPassword);
			}
		};
	}
	/* ------------------------------------------------------------------- */
	
	public void authorName() {
		System.out.println("***************************************************");
		System.out.println(testAuthoName);
		System.out.println("***************************************************");
	}
	/* ------------------------------------------------------------------- */
}
