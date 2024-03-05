package pl.dicedev.builders;

import pl.dicedev.enums.ExpensesCategory;
import pl.dicedev.services.dtos.ExpensesDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ExpensesDtoBuilder {

    private UUID id;
    private BigDecimal amount;
    private Instant purchaseDate;
    private ExpensesCategory category;

    public ExpensesDto build() {
        return new ExpensesDto(id, amount, purchaseDate, category);
    }

    public ExpensesDtoBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ExpensesDtoBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public ExpensesDtoBuilder withPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
        return this;
    }

    public ExpensesDtoBuilder withCategory(ExpensesCategory category) {
        this.category = category;
        return this;
    }
}
