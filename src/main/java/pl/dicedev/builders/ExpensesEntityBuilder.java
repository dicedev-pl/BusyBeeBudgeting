package pl.dicedev.builders;

import pl.dicedev.enums.ExpensesCategory;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ExpensesEntityBuilder {

    private UUID id;
    private BigDecimal amount;
    private Instant purchaseDate;
    private ExpensesCategory category;
    private UserEntity user;

    public ExpensesEntity build() {
        return new ExpensesEntity(id, amount, purchaseDate, user, category);
    }

    public ExpensesEntityBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ExpensesEntityBuilder withUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public ExpensesEntityBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public ExpensesEntityBuilder withPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
        return this;
    }

    public ExpensesEntityBuilder withCategory(ExpensesCategory category) {
        this.category = category;
        return this;
    }
}
