package zve.com.vn.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zve.com.vn.entity.InvalidateToken;

@Repository
public interface InvalidateRepository extends JpaRepository<InvalidateToken, String> {

  void deleteByExpireTimeBefore(Date expireTime);
}
