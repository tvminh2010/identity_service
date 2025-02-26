package zve.com.vn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zve.com.vn.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
