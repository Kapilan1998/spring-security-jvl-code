package spring.security.basic.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.basic.demo.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
