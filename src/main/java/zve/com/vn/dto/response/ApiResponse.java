package zve.com.vn.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApiResponse <Thongtin> {
	private int code;
	private String message;
	private Thongtin result;
}
