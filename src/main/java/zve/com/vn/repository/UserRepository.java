package zve.com.vn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zve.com.vn.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);
}
