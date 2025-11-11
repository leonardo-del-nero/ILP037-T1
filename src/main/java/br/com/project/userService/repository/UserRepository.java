package br.com.project.userService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.project.userService.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    @Transactional(readOnly = true)
    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
