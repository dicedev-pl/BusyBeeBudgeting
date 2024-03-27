package pl.dicedev.filters;

import org.springframework.stereotype.Component;
import pl.dicedev.enums.FilterSpecification;
import pl.dicedev.repositories.ExpensesRepository;
import pl.dicedev.repositories.entities.ExpensesEntity;
import pl.dicedev.repositories.entities.UserEntity;

import java.time.Instant;
import java.util.List;

@Component
public class ExpensesFilterRange extends FilterRange<ExpensesEntity> {

    private final ExpensesRepository expensesRepository;

    public ExpensesFilterRange(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }

    @Override
    public List<ExpensesEntity> getAllEntityBetweenDate(Instant fromDate, Instant toDate, UserEntity user) {
        return expensesRepository.findAllByDate(fromDate, toDate, user);
    }

    @Override
    protected String getFilterName() {
        return FilterSpecification.FOR_EXPENSES.getValidator();
    }
}
