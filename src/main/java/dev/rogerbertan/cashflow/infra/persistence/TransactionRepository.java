package dev.rogerbertan.cashflow.infra.persistence;

import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " + "WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") Type type);

    @Query(
            "SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t "
                    + "WHERE t.type = :type "
                    + "AND YEAR(t.transactionDate) = :year "
                    + "AND MONTH(t.transactionDate) = :month")
    BigDecimal sumAmountByMonthAndYearAndType(
            @Param("month") int month, @Param("year") int year, @Param("type") Type type);

    @Query(
            "SELECT new dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary("
                    + "c.name, "
                    + "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount "
                    + "ELSE CAST(0 AS BigDecimal) END), CAST(0 AS BigDecimal)), "
                    + "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount "
                    + "ELSE CAST(0 AS BigDecimal) END), CAST(0 AS BigDecimal))) "
                    + "FROM TransactionEntity t "
                    + "JOIN t.category c "
                    + "WHERE YEAR(t.transactionDate) = :year "
                    + "AND MONTH(t.transactionDate) = :month "
                    + "GROUP BY c.id, c.name")
    List<CategorySummary> findCategorySummariesByMonthAndYear(
            @Param("month") int month, @Param("year") int year);

    @Query(
            "SELECT t FROM TransactionEntity t "
                    + "WHERE t.transactionDate BETWEEN :startDate AND :endDate "
                    + "ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findByDateRange(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
