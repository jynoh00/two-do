package twodo.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import twodo.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}