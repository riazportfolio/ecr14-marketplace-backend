package com.ecr14.marketplace.repository;

import com.ecr14.marketplace.entity.CustomerSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerSearchHistoryRepository extends JpaRepository<CustomerSearchHistory, String> {

    List<CustomerSearchHistory> findByUserIdOrderBySearchedAtDesc(String userId);

    @Query("SELECT csh.searchQuery FROM CustomerSearchHistory csh WHERE csh.userId = :userId AND csh.searchedAt >= :since GROUP BY csh.searchQuery ORDER BY MAX(csh.searchedAt) DESC")
    List<String> findDistinctRecentSearches(@Param("userId") String userId, @Param("since") LocalDateTime since);

    @Query("SELECT csh.searchQuery FROM CustomerSearchHistory csh WHERE csh.userId = :userId ORDER BY csh.searchedAt DESC")
    List<String> findTopSearchesByUserId(@Param("userId") String userId);
}
