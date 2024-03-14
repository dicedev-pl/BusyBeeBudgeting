package pl.dicedev.excetpions;

import pl.dicedev.enums.ExpensesExceptionErrorMessages;

public class MissingExpensesFilterException extends RuntimeException {

    private String place;

    public MissingExpensesFilterException(String month, String place) {
        super(ExpensesExceptionErrorMessages.MISSING_FILTER_KEY.getMessage() + " " + month);
        this.place = place;
    }
}
