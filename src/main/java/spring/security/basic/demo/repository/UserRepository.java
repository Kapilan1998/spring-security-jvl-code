package spring.security.basic.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.basic.demo.entity.UserEntity;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
