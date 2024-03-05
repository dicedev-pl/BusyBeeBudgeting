package pl.dicedev.services.dtos;

import pl.dicedev.enums.ExpensesCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ExpensesDto {

    private UUID id;
    private BigDecimal amount;
    private Instant purchaseDate;
    private ExpensesCategory category;

    public ExpensesDto() {
    }

    public ExpensesDto(UUID id, BigDecimal amount, Instant purchaseDate, ExpensesCategory category) {
        this.id = id;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public ExpensesCategory getCategory() {
        return category;
    }

    public void setCategory(ExpensesCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpensesDto that = (ExpensesDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ExpensesDto{" +
                "id=" + id +
                ", amount=" + amount +
                ", purchaseData=" + purchaseDate +
                ", category=" + category +
                '}';
    }
}
