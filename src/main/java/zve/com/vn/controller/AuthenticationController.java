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
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.dto.response.AuthenticationRestponse;
import zve.com.vn.dto.response.IntrospectRestponse;
import zve.com.vn.enums.ErrorCode;
import zve.com.vn.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	AuthenticationService authenticationService;
	
	/* ------------------------------------------------------------------------------- */
	@PostMapping("/log-in2")
	ApiResponse<AuthenticationRestponse> authenticate(@RequestBody AuthenticationRequest request) {
		ApiResponse<AuthenticationRestponse> apiResponse = new ApiResponse<AuthenticationRestponse>();
		boolean result = authenticationService.authenticate(request);
		
		AuthenticationRestponse authenticationResponse = new AuthenticationRestponse();
		authenticationResponse.setAuthenticated(result);
		authenticationResponse.setToken("Token String demo here");
		
		if(result) {
			apiResponse.setCode(ErrorCode.USER_AUTHENTICATED.getCode());
			apiResponse.setResult(authenticationResponse);
		} else {
			apiResponse.setCode(ErrorCode.UN_AUTHENTICATED.getCode());
			apiResponse.setResult(authenticationResponse);
		}
		return apiResponse;		
	}
	/* ------------------------------------------------------------------------------- */
	@PostMapping("/log-in")
	ApiResponse<AuthenticationRestponse> authenticateJwt(@RequestBody AuthenticationRequest request) {
		ApiResponse<AuthenticationRestponse> apiResponse = new ApiResponse<AuthenticationRestponse>();
		AuthenticationRestponse result = authenticationService.authenticateJwt(request);
		apiResponse.setResult(result);
		return apiResponse;
	}
	/* ------------------------------------------------------------------------------- */
	@PostMapping("/introspect")
	ApiResponse<IntrospectRestponse> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
		var result = authenticationService.introspect(request);
		
		ApiResponse<IntrospectRestponse> apiResponse = new ApiResponse<IntrospectRestponse>();
		apiResponse.setResult(result);
		return apiResponse;
	}
	/* ------------------------------------------------------------------------------- */
}
