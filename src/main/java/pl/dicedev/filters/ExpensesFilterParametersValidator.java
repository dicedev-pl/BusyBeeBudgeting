package pl.dicedev.filters;

import org.springframework.stereotype.Component;
import pl.dicedev.excetpions.MissingExpensesFilterException;

@Component
public class ExpensesFilterParametersValidator extends FilterParametersValidator {

    @Override
    public void throwException(String missingKey, String placeId) {
        throw new MissingExpensesFilterException(missingKey, placeId);
    }
}
