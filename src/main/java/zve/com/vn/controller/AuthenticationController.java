package zve.com.vn.controller;

import java.text.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.JOSEException;
import zve.com.vn.dto.request.AuthenticationRequest;
import zve.com.vn.dto.request.IntrospectRequest;
import zve.com.vn.dto.request.LogoutRequest;
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.dto.response.AuthenticationRestponse;
import zve.com.vn.dto.response.IntrospectRestponse;
import zve.com.vn.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	AuthenticationService authenticationService;
	
	/* ------------------------------------------------------------------------------- */
	@PostMapping("/log-in")
	ApiResponse<AuthenticationRestponse> authenticateJwt(@RequestBody AuthenticationRequest request) {
		ApiResponse<AuthenticationRestponse> apiResponse = new ApiResponse<AuthenticationRestponse>();
		AuthenticationRestponse result = authenticationService.authenticateJwt(request);
		apiResponse.setResult(result);
		return apiResponse;
	}
	/* ------------------------------------------------------------------------------- */
	@PostMapping("/logout")
	ApiResponse<String> logoutJwt (@RequestBody LogoutRequest request) throws JOSEException, ParseException {
		authenticationService.logout(request);
		ApiResponse<String> apiResponse = new ApiResponse<String>();
		apiResponse.setResult("Logout successfully!");
		return apiResponse;
	}
	/* ------------------------------------------------------------------------------- */
	@PostMapping("/introspect")
	ApiResponse<IntrospectRestponse> validate(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
		var result = authenticationService.validate(request);
		
		ApiResponse<IntrospectRestponse> apiResponse = new ApiResponse<IntrospectRestponse>();
		apiResponse.setResult(result);
		return apiResponse;
	}
	/* ------------------------------------------------------------------------------- */
	@PostMapping("/validatetoken")
	ApiResponse<IntrospectRestponse> validate2(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
		var result = authenticationService.validate2(request);
		ApiResponse<IntrospectRestponse> apiResponse = new ApiResponse<IntrospectRestponse>();
		apiResponse.setResult(result);
		return apiResponse;
	}
	/* ------------------------------------------------------------------------------- */
}
