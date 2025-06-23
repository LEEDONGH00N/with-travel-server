package com.arom.with_travel.domain.accompanies.repository.accompany;

import com.arom.with_travel.domain.accompanies.model.AccompanyComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AccompanyCommentRepository extends JpaRepository<AccompanyComment, Long> {
    @Query("""
    SELECT c FROM AccompanyComment c
    JOIN FETCH c.member
    WHERE c.accompany.id = :accompanyId
    AND (
        :lastCreatedAt IS NULL OR
        (c.createdAt > :lastCreatedAt) OR
        (c.createdAt = :lastCreatedAt AND c.id > :lastId)
    )
    ORDER BY c.createdAt ASC, c.id ASC
""")
    Slice<AccompanyComment> findCommentsWithCursor(
            @Param("accompanyId") Long accompanyId,
            @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
