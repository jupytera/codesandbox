package com.example.codesandbox.snippet.repository;

import com.example.codesandbox.snippet.entity.CodeSnippet;
import com.example.codesandbox.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeSnippetRepository extends JpaRepository<CodeSnippet, Long> {

    /**
     * 查找某个用户的所有代码片段
     * 用途：用户查看自己的代码列表
     */
    Page<CodeSnippet> findByOwner(User owner, Pageable pageable);

    /**
     * 根据可见性查找代码片段
     * 用途：查找所有公开的代码
     */
    Page<CodeSnippet> findByVisibility(CodeSnippet.Visibility visibility, Pageable pageable);

    /**
     * 根据语言查找代码片段
     * 用途：筛选特定语言的代码（如只看Python代码）
     */
    Page<CodeSnippet> findByLanguage(String language, Pageable pageable);

    /**
     * 根据标题模糊搜索
     * 用途：基础搜索功能（Elasticsearch不可用时的降级方案）
     */
    @Query("SELECT cs FROM CodeSnippet cs WHERE cs.title LIKE %:keyword% OR cs.description LIKE %:keyword%")
    Page<CodeSnippet> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 查找某个用户的某种可见性的代码
     * 用途：查看自己的私有代码、公开代码等
     */
    Page<CodeSnippet> findByOwnerAndVisibility(User owner, CodeSnippet.Visibility visibility, Pageable pageable);

    /**
     * 查找热门代码（按浏览量排序）
     * 用途：首页推荐
     */
    @Query("SELECT cs FROM CodeSnippet cs WHERE cs.visibility = 'PUBLIC' ORDER BY cs.viewCount DESC")
    Page<CodeSnippet> findTopByViewCount(Pageable pageable);

    /**
     * 统计某个用户的代码数量
     */
    long countByOwner(User owner);
}
