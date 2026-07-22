package ch.andre.financemanager.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private Category category;
    private Account account;

    public Transaction(LocalDate date, BigDecimal amount, String description, TransactionType type, Category category, Account account){

        this.id = UUID.randomUUID();
        setDate(date);
        setAmount(amount);
        setDescription(description);
        setType(type);
        setCategory(category);
        setAccount(account);
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = Objects.requireNonNull(date, "Das Buchungsdatum darf nicht leer sein.");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "Der Betrag darf nicht leer sein.");

        if(amount.signum() <= 0) {
            throw new IllegalArgumentException("Der Betrag muss grösser als null sein.");
        }

        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Die Beschreibung darf nicht leer sein.");
        }
        this.description = description.trim();
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = Objects.requireNonNull(type, "Die Buchungsart darf nicht null sein.");
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = Objects.requireNonNull(category, "Die Kategorie darf nicht null sein.");
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = Objects.requireNonNull(account, "Das Konto darf nicht null sein.");
    }

    public BigDecimal getSignedAmount() {
        return type == TransactionType.EXPENSE
                ? amount.negate()
                : amount;
    }

    @Override
    public String toString() {
        return date
                + " | "
                + description
                +" | "
                + getSignedAmount()
                +" "
                +account.getCurrency().getCurrencyCode();
    }
}
