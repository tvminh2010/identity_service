package zve.com.vn.entity;

import java.util.Set;

import jakarta.persistence.Entity;
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
@Table(name="tbl_role")				//Bổ sung dòng này nếu CSDL là sql server
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
	
	@Id
	private String name;						
	private String description;
	
	@ManyToMany
	Set<Permission> permissions;
	
	@ManyToMany(mappedBy = "roles")
	Set<User> users;
}
