package dev.rogerbertan.cashflow.infra.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import org.junit.jupiter.api.Test;

class PeriodCalculatorTest {

    // getCurrentPeriod tests

    @Test
    void getCurrentPeriod_ShouldReturnCurrentWeek_WhenPeriodIsWeekly() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate expectedEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        DateRange result = PeriodCalculator.getCurrentPeriod("weekly");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
    }

    @Test
    void getCurrentPeriod_ShouldReturnCurrentWeek_WhenPeriodIsWeeklyUpperCase() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate expectedEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        DateRange result = PeriodCalculator.getCurrentPeriod("WEEKLY");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
    }

    @Test
    void getCurrentPeriod_ShouldReturnCurrentQuarter_WhenPeriodIsQuarterly() {
        LocalDate now = LocalDate.now();
        Month currentMonth = now.getMonth();
        Month startMonth = Month.of(((currentMonth.getValue() - 1) / 3) * 3 + 1);
        LocalDate expectedStart = LocalDate.of(now.getYear(), startMonth, 1);
        LocalDate expectedEnd =
                expectedStart.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

        DateRange result = PeriodCalculator.getCurrentPeriod("quarterly");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
        assertThat(result.start().getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void getCurrentPeriod_ShouldReturnCurrentYear_WhenPeriodIsYearly() {
        int year = LocalDate.now().getYear();

        DateRange result = PeriodCalculator.getCurrentPeriod("yearly");

        assertThat(result.start()).isEqualTo(LocalDate.of(year, 1, 1));
        assertThat(result.end()).isEqualTo(LocalDate.of(year, 12, 31));
    }

    @Test
    void getCurrentPeriod_ShouldReturnCurrentMonth_WhenPeriodIsMonthly() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate expectedEnd = now.with(TemporalAdjusters.lastDayOfMonth());

        DateRange result = PeriodCalculator.getCurrentPeriod("monthly");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
    }

    @Test
    void getCurrentPeriod_ShouldReturnCurrentMonth_WhenPeriodIsUnknown() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate expectedEnd = now.with(TemporalAdjusters.lastDayOfMonth());

        DateRange result = PeriodCalculator.getCurrentPeriod("daily");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
    }

    // getPreviousPeriod tests

    @Test
    void getPreviousPeriod_ShouldReturnPreviousWeek_WhenPeriodIsWeekly() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart =
                now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate expectedEnd = expectedStart.plusDays(6);

        DateRange result = PeriodCalculator.getPreviousPeriod("weekly");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
        assertThat(result.start().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    void getPreviousPeriod_ShouldReturnPreviousQuarter_WhenPeriodIsQuarterly() {
        LocalDate now = LocalDate.now();
        Month currentMonth = now.getMonth();
        Month startMonth = Month.of(((currentMonth.getValue() - 1) / 3) * 3 + 1);
        LocalDate expectedStart = LocalDate.of(now.getYear(), startMonth, 1).minusMonths(3);
        LocalDate expectedEnd =
                expectedStart.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

        DateRange result = PeriodCalculator.getPreviousPeriod("quarterly");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
        assertThat(result.start()).isBefore(LocalDate.of(now.getYear(), startMonth, 1));
    }

    @Test
    void getPreviousPeriod_ShouldReturnPreviousYear_WhenPeriodIsYearly() {
        int previousYear = LocalDate.now().getYear() - 1;

        DateRange result = PeriodCalculator.getPreviousPeriod("yearly");

        assertThat(result.start()).isEqualTo(LocalDate.of(previousYear, 1, 1));
        assertThat(result.end()).isEqualTo(LocalDate.of(previousYear, 12, 31));
    }

    @Test
    void getPreviousPeriod_ShouldReturnPreviousMonth_WhenPeriodIsMonthly() {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        LocalDate expectedStart = previousMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate expectedEnd = previousMonth.with(TemporalAdjusters.lastDayOfMonth());

        DateRange result = PeriodCalculator.getPreviousPeriod("monthly");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
    }

    @Test
    void getPreviousPeriod_ShouldReturnPreviousMonth_WhenPeriodIsUnknown() {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        LocalDate expectedStart = previousMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate expectedEnd = previousMonth.with(TemporalAdjusters.lastDayOfMonth());

        DateRange result = PeriodCalculator.getPreviousPeriod("daily");

        assertThat(result.start()).isEqualTo(expectedStart);
        assertThat(result.end()).isEqualTo(expectedEnd);
    }
}
