package com.example.codesandbox.review.repository;

import com.example.codesandbox.review.entity.CodeReview;
import com.example.codesandbox.review.entity.ReviewComment;
import com.example.codesandbox.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    /**
     * 查找某个审核的所有评论
     * 用途：显示审核详情页的评论列表
     */
    List<ReviewComment> findByReviewOrderByCreatedAtAsc(CodeReview review);

    /**
     * 查找某个审核在特定行号的评论
     * 用途：代码行内显示评论
     */
    List<ReviewComment> findByReviewAndLineNumber(CodeReview review, Integer lineNumber);

    /**
     * 查找某个用户发表的所有评论
     * 用途：查看用户的评论历史
     */
    List<ReviewComment> findByAuthor(User author);

    /**
     * 统计某个审核的评论数量
     * 用途：显示"共有X条评论"
     */
    long countByReview(CodeReview review);
}