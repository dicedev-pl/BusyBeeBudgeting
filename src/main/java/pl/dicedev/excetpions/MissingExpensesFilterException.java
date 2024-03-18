package pl.dicedev.excetpions;

import pl.dicedev.enums.ExpensesExceptionErrorMessages;

public class MissingExpensesFilterException extends RuntimeException {

    private String place;

    public MissingExpensesFilterException(String missingKey, String place) {
        super(ExpensesExceptionErrorMessages.MISSING_FILTER_KEY.getMessage() + " " + missingKey);
        this.place = place;
    }
}
