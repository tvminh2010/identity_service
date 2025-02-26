package zve.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zve.com.vn.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
