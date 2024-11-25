package ru.tpu.hostel.administration.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.enums.DocumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findAllByType(DocumentType type);

    List<Document> findAllByUser(UUID userId);

    Optional<Document> findByUserAndType(UUID userId, DocumentType type);

    @Query("""
                SELECT DISTINCT d
                FROM Document d
                JOIN Document sub ON d.user = sub.user
                WHERE sub.type = :type
                  AND sub.endDate <= :endDate
            """)
    Page<Document> findAllByEndDateLessThanEqualAndType(
            @Param("endDate") LocalDate endDate,
            @Param("type") DocumentType type,
            Pageable pageable
    );

    @Query("""
    SELECT d
    FROM Document d
    WHERE d.user IN (
        SELECT sub1.user
        FROM Document sub1
        JOIN Document sub2 ON sub1.user = sub2.user
        WHERE sub1.type = :type
          AND sub1.endDate <= :endDate
          AND sub2.endDate > :endDate
        GROUP BY sub1.user
    )
    ORDER BY d.user ASC, d.type ASC
    """)
    Page<Document> findAllByEndDateLessThanEqualAndTypeForOne(
            @Param("endDate") LocalDate endDate,
            @Param("type") DocumentType type,
            Pageable pageable
    );

    @Query(value = """
    SELECT d.*
    FROM "administration"."document" d
    WHERE d."user" IN (
        SELECT d1."user"
        FROM "administration"."document" d1
        GROUP BY d1."user"
        HAVING COUNT(*) = COUNT(CASE WHEN d1."end_date" <= :endDate THEN 1 END)
    )
    ORDER BY d."user" ASC, d."type" ASC
    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM "administration"."document" d
                    WHERE d."user" IN (
                        SELECT d1."user"
                        FROM "administration"."document" d1
                        GROUP BY d1."user"
                        HAVING COUNT(*) = COUNT(CASE WHEN d1."end_date" <= :endDate THEN 1 END)
                    )
                    """,
            nativeQuery = true)
    Page<Document> findAllByEndDateLessThanEqual(
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
                SELECT DISTINCT d
                FROM Document d
                JOIN Document sub ON d.user = sub.user
                WHERE sub.type = :type
                  AND sub.endDate > :endDate
            """)
    Page<Document> findAllByEndDateAfterAndType(
            @Param("endDate") LocalDate endDate,
            @Param("type") DocumentType type,
            Pageable pageable
    );

    @Query("""
    SELECT d
    FROM Document d
    WHERE d.user IN (
        SELECT sub1.user
        FROM Document sub1
        JOIN Document sub2 ON sub1.user = sub2.user
        WHERE sub1.type = :type
          AND sub1.endDate > :endDate
          AND sub2.endDate <= :endDate
        GROUP BY sub1.user
    )
    ORDER BY d.user ASC, d.type ASC
    """)
    Page<Document> findAllByEndDateAfterAndTypeForOne(
            @Param("endDate") LocalDate endDate,
            @Param("type") DocumentType type,
            Pageable pageable
    );

    @Query(value = """
    SELECT d.*
    FROM "administration"."document" d
    WHERE d."user" IN (
        SELECT d1."user"
        FROM "administration"."document" d1
        GROUP BY d1."user"
        HAVING COUNT(*) = COUNT(CASE WHEN d1."end_date" > :endDate THEN 1 END)
    )
    ORDER BY d."user" ASC, d."type" ASC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM "administration"."document" d
    WHERE d."user" IN (
        SELECT d1."user"
        FROM "administration"."document" d1
        GROUP BY d1."user"
        HAVING COUNT(*) = COUNT(CASE WHEN d1."end_date" > :endDate THEN 1 END)
    )
    """,
            nativeQuery = true)
    Page<Document> findAllByEndDateAfter(
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );


    List<Document> findByUserInOrderByUserAscTypeAsc(List<UUID> userIds);
}
