package com.example.codesandbox.snippet.repository;

import com.example.codesandbox.snippet.entity.CodeSnippet;
import com.example.codesandbox.snippet.entity.CodeVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeVersionRepository extends JpaRepository<CodeVersion, Long> {

    /**
     * 获取某个代码片段的所有版本（按版本号倒序）
     * 用途：显示版本历史列表
     */
    List<CodeVersion> findBySnippetOrderByVersionNumberDesc(CodeSnippet snippet);

    /**
     * 获取某个代码片段的特定版本
     * 用途：回滚到指定版本
     */
    Optional<CodeVersion> findBySnippetAndVersionNumber(CodeSnippet snippet, Integer versionNumber);

    /**
     * 获取某个代码片段的最新版本
     * 用途：显示当前代码内容
     */
    @Query("SELECT cv FROM CodeVersion cv WHERE cv.snippet = :snippet ORDER BY cv.versionNumber DESC LIMIT 1")
    Optional<CodeVersion> findLatestVersion(CodeSnippet snippet);

    /**
     * 统计某个代码片段的版本数量
     * 用途：显示"共有X个版本"
     */
    long countBySnippet(CodeSnippet snippet);

    /**
     * 获取某个代码片段最大的版本号
     * 用途：创建新版本时计算下一个版本号
     */
    @Query("SELECT MAX(cv.versionNumber) FROM CodeVersion cv WHERE cv.snippet = :snippet")
    Integer findMaxVersionNumber(CodeSnippet snippet);
}
