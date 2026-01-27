package com.example.codesandbox.user.repository;

import com.example.codesandbox.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // JpaRepository 自动提供的方法：
    // - save(User user)           保存/更新
    // - findById(Long id)         根据ID查询
    // - findAll()                 查询所有
    // - deleteById(Long id)       根据ID删除
    // - count()                   统计数量
    // - existsById(Long id)       判断是否存在

    // 自定义查询方法（Spring Data JPA 自动实现）
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}