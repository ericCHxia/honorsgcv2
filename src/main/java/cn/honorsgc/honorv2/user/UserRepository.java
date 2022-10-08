package cn.honorsgc.honorv2.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User getByUserId(String userId);
    Optional<User> findUserByUserId(String userId);

    @Query("SELECT DISTINCT u.college FROM User u")
    Set<String> getCollegeNames();

    @Query("SELECT DISTINCT u.subject FROM User u")
    Set<String> getSubjectNames();

    @Query("SELECT DISTINCT u.classId FROM User u")
    Set<String> getClassIds();
}