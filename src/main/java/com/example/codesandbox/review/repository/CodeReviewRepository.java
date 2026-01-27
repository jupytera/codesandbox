package com.example.codesandbox.review.repository;

import com.example.codesandbox.review.entity.CodeReview;
import com.example.codesandbox.snippet.entity.CodeSnippet;
import com.example.codesandbox.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {

    /**
     * 查找某个代码片段的所有审核记录
     * 用途：查看代码的审核历史
     */
    List<CodeReview> findBySnippet(CodeSnippet snippet);

    /**
     * 查找某个审核者负责的所有审核任务
     * 用途：审核者查看"待我审核的任务"
     */
    List<CodeReview> findByReviewer(User reviewer);

    /**
     * 根据状态查找审核任务
     * 用途：查找所有待审核/已通过/被拒绝的审核
     */
    List<CodeReview> findByStatus(CodeReview.ReviewStatus status);

    /**
     * 查找某个审核者的待审核任务
     * 用途：显示"待处理"列表
     */
    List<CodeReview> findByReviewerAndStatus(User reviewer, CodeReview.ReviewStatus status);

    /**
     * 查找某个代码片段的最新审核记录
     * 用途：显示当前审核状态
     */
    @Query("SELECT cr FROM CodeReview cr WHERE cr.snippet = :snippet ORDER BY cr.createdAt DESC LIMIT 1")
    Optional<CodeReview> findLatestReview(CodeSnippet snippet);

    /**
     * 统计某个审核者已完成的审核数量
     * 用途：审核者工作量统计
     */
    long countByReviewerAndStatus(User reviewer, CodeReview.ReviewStatus status);
}
