package ch.andre.financemanager.model;

    import java.math.BigDecimal;
    import java.util.Currency;
    import java.util.Objects;
    import java.util.UUID;

public class Account {

    private final UUID id;
    private String name;
    private AccountType type;
    private BigDecimal openingBalance;
    private Currency currency;

    public Account(String name, AccountType type, BigDecimal openingBalance, Currency currency) {
        this.id = UUID.randomUUID();
        setName(name);
        setType(type);
        setOpeningBalance(openingBalance);
        setCurrency(currency);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Der Kontoname darf nicht leer sein.");
        }

        this.name = name.trim();
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = Objects.requireNonNull(
                type,
                "Der Kontotyp darf nicht null sein."
        );
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = Objects.requireNonNull(
                openingBalance,
                "Der Anfangssaldo darf nicht null sein."
        );
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = Objects.requireNonNull(
                currency,
                "Die Währung darf nicht null sein."
        );
    }

    @Override
    public String toString() {
        return name + " (" + type + "): " + openingBalance + " " + currency.getCurrencyCode();
    }

}
