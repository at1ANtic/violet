package cn.atlantt1c.service;

import cn.atlantt1c.model.entity.Role;
import cn.atlantt1c.model.entity.User;
import cn.atlantt1c.repository.RoleRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleService {


    @Resource
    private RoleRepository roleRepository;

    public List<Role> findRolesByUser(User user) {
        return roleRepository.findRoleByUser(user.getAccount());
    }
}

