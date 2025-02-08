package zve.com.vn.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="tbl_user")				//Bổ sung dòng này nếu CSDL là sql server
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private String id;								//Đổi sang Integer nếu là sql server
	private String username;
	private String password;
	private String email;
	@Column(columnDefinition = "nvarchar(255)")		//Nếu dùng database là sqlserver
	private String firstName;
	@Column(columnDefinition = "nvarchar(255)")		//Nếu dùng database là sqlserver
	private String lastName;
	private LocalDate dob;
	
	@ManyToMany
	private Set<Role> roles;
}
