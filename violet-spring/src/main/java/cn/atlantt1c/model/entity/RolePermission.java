package cn.atlantt1c.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * RolePermission
 */

@Entity
@Table(name = "role_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission implements Serializable {

    private static final long serialVersionUID = -8564770707000796503L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 角色id
     */
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * 权限id
     */
    @Column(name = "permission_id")
    private Integer permissionId;
}