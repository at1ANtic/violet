package cn.atlantt1c.repository;

import cn.atlantt1c.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    @Query("SELECT p FROM Role r " +
            "LEFT JOIN RolePermission rp ON r.id = rp.roleId " +
            "LEFT JOIN Permission p ON p.id = rp.permissionId " +
            "WHERE r.name = :roleName")
    List<Permission> findPermissionsByRoleName(@Param("roleName") String roleName);
}