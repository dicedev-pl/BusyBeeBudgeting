package pl.dicedev.excetpions;

import pl.dicedev.enums.FilterExceptionErrorMessages;

public class MissingExpensesFilterException extends RuntimeException {

    private String place;

    public MissingExpensesFilterException(String missingKey, String place) {
        super(FilterExceptionErrorMessages.MISSING_EXPENSES_FILTER_KEY.getMessage() + " " + missingKey);
        this.place = place;
    }
}
