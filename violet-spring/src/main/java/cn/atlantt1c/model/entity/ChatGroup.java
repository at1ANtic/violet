package cn.atlantt1c.model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "chat_group")
public class ChatGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 自动递增的ID

    @Column(nullable = false, unique = true)
    private String uid;  // 生成的唯一UID

    @Column(nullable = false)
    private String groupName;  // 组名

    @ElementCollection
    @CollectionTable(name = "chat_group_members", joinColumns = @JoinColumn(name = "chat_group_id"))
    @Column(name = "member_id")
    private List<String> members;  // 成员列表，以JSON格式存储

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
