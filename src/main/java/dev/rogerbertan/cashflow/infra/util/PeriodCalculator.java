package dev.rogerbertan.cashflow.infra.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

public final class PeriodCalculator {

    private PeriodCalculator() {}

    public static DateRange getCurrentPeriod(String period) {
        LocalDate now = LocalDate.now();

        return switch (period.toLowerCase()) {
            case "weekly" -> {
                LocalDate startOfWeek =
                        now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                yield new DateRange(startOfWeek, endOfWeek);
            }
            case "quarterly" -> {
                Month currentMonth = now.getMonth();
                Month startMonth = Month.of(((currentMonth.getValue() - 1) / 3) * 3 + 1);
                LocalDate startOfQuarter = LocalDate.of(now.getYear(), startMonth, 1);
                LocalDate endOfQuarter =
                        startOfQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                yield new DateRange(startOfQuarter, endOfQuarter);
            }
            case "yearly" -> {
                LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);
                LocalDate endOfYear = LocalDate.of(now.getYear(), 12, 31);
                yield new DateRange(startOfYear, endOfYear);
            }
            default -> {
                // monthly (default)
                LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
                yield new DateRange(startOfMonth, endOfMonth);
            }
        };
    }

    public static DateRange getPreviousPeriod(String period) {
        LocalDate now = LocalDate.now();

        return switch (period.toLowerCase()) {
            case "weekly" -> {
                LocalDate previousWeekStart =
                        now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate previousWeekEnd = previousWeekStart.plusDays(6);
                yield new DateRange(previousWeekStart, previousWeekEnd);
            }
            case "quarterly" -> {
                Month currentMonth = now.getMonth();
                Month startMonth = Month.of(((currentMonth.getValue() - 1) / 3) * 3 + 1);
                LocalDate startOfPreviousQuarter =
                        LocalDate.of(now.getYear(), startMonth, 1).minusMonths(3);
                LocalDate endOfPreviousQuarter =
                        startOfPreviousQuarter
                                .plusMonths(2)
                                .with(TemporalAdjusters.lastDayOfMonth());
                yield new DateRange(startOfPreviousQuarter, endOfPreviousQuarter);
            }
            case "yearly" -> {
                LocalDate startOfPreviousYear = LocalDate.of(now.getYear() - 1, 1, 1);
                LocalDate endOfPreviousYear = LocalDate.of(now.getYear() - 1, 12, 31);
                yield new DateRange(startOfPreviousYear, endOfPreviousYear);
            }
            default -> {
                // monthly (default)
                LocalDate previousMonth = now.minusMonths(1);
                LocalDate startOfPreviousMonth =
                        previousMonth.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate endOfPreviousMonth =
                        previousMonth.with(TemporalAdjusters.lastDayOfMonth());
                yield new DateRange(startOfPreviousMonth, endOfPreviousMonth);
            }
        };
    }
}
