package cn.atlantt1c.repository;

import cn.atlantt1c.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByAccount(String account);

    // 根据账户查询用户 ID
    @Query("SELECT u.id FROM User u WHERE u.account = ?1")
    Integer findIdByAccount(String account);
    // 批量查询用户
    @Query(value = "SELECT username FROM user WHERE id IN (:ids)", nativeQuery = true)
    List<String> findUsernamesByIds(List<Integer> ids);

    // 根据 ID 查询 username
    @Query("SELECT u.username FROM User u WHERE u.id = ?1")
    String findUsernameById(Integer id);
}
