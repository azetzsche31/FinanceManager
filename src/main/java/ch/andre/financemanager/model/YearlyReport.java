package ch.andre.financemanager.model;

import java.math.BigDecimal;

public class YearlyReport {

    private final Account account;
    private final int year;

    private final BigDecimal totalIncome;
    private final BigDecimal totalExpenses;
    private final BigDecimal netResult;

    public YearlyReport(
            Account account,
            int year,
            BigDecimal totalIncome,
            BigDecimal totalExpenses,
            BigDecimal netResult
    ) {
        this.account = account;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netResult = netResult;
    }

    public Account getAccount() {
        return account;
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
                ===== Jahresbericht =====
                Konto: %s
                Jahr: %d
                
                Einnahmen: %s %s
                Ausgaben: %s %s
                Netto: %s %s
                """.formatted(
                        account.getName(),
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
