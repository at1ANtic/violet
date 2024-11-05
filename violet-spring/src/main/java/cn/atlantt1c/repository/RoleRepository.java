package cn.atlantt1c.repository;

import cn.atlantt1c.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT new cn.atlantt1c.model.entity.Role(r.id, r.name) " +
            "FROM User u " +
            "LEFT JOIN UserRole ur ON u.id = ur.userId " +
            "LEFT JOIN Role r ON r.id = ur.roleId " +
            "WHERE u.account = :account")
    List<Role> findRoleByUser(@Param("account") String account);
}