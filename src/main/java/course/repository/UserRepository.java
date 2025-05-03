package course.repository;

import course.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT user FROM UserEntity user WHERE " +
            "user.username  = :username")
    UserEntity findByUsername(@Param("username") String username);
}