package cn.atlantt1c.repository;

import cn.atlantt1c.model.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {

    // 查找包含指定成员ID的聊天组
    @Query("SELECT cg FROM ChatGroup cg WHERE cg.members LIKE %?1%")
    List<ChatGroup> findByMemberId(String memberId);
}
