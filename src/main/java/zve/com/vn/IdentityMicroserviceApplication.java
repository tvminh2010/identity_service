package zve.com.vn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentityMicroserviceApplication {

	public static void main(String[] args) {
		/*ApplicationContext context = */SpringApplication.run(IdentityMicroserviceApplication.class, args);
		/*
		ApplicationInitConfig applicationInitConfig = context.getBean(ApplicationInitConfig.class);
		applicationInitConfig.authorName();
		*/
		//UserUpdateRequest userUpdateRequest = context.getBean(UserUpdateRequest.class);
		//var testConfigure = context.getBean("testConfiguration");
		//System.out.println("testConfigure Bean là: " + testConfigure.toString());
		//UserUpdateRequest userUpdateRequest2 = context.getBean(UserUpdateRequest.class);
		//System.out.println(userUpdateRequest);
		//System.out.println("Equal là: " + userUpdateRequest.equals(userUpdateRequest2));
		//System.out.println("Hashcode là: " + userUpdateRequest.hashCode());
	}

}
