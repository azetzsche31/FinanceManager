package ch.andre.financemanager.model;

import java.math.BigDecimal;
import java.time.Month;

public class MonthlyReport {

        private final Month month;
        private final int year;
        private Account account;

        private final BigDecimal totalIncome;
        private final BigDecimal totalExpenses;
        private final BigDecimal netResult;

    public MonthlyReport(
            Account account,
            Month month,
            int year,
            BigDecimal totalIncome,
            BigDecimal totalExpenses,
            BigDecimal netResult
            ) {
        this.account = account;
        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netResult = netResult;
    }

    public Account getAccount() {
        return account;
    }

    public Month getMonth(){
        return month;
    }

    public int getYear() {
        return year;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getNetResult() {
        return netResult;
    }

    @Override
    public String toString() {
        return """
                ===== Monatsbericht =====
                Konto: %s
                Zeitraum: %s %d
                
                Einnahmen: %s %s
                Ausgaben: %s %s
                Netto: %s %s
                """.formatted(
                account.getName(),
                month,
                year,
                totalIncome,
                account.getCurrency().getCurrencyCode(),
                totalExpenses,
                account.getCurrency().getCurrencyCode(),
                netResult,
                account.getCurrency().getCurrencyCode()
        );
    }

}
