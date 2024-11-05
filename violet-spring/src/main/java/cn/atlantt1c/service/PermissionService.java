package cn.atlantt1c.service;

import cn.atlantt1c.model.entity.Permission;
import cn.atlantt1c.model.entity.Role;
import cn.atlantt1c.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService {

    @Resource
    private PermissionRepository permissionRepository;

    public List<Permission> findPermissionByRole(Role role) {
        return permissionRepository.findPermissionsByRoleName(role.getName());
    }
}