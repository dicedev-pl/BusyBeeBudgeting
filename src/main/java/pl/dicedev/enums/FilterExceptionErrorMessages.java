package pl.dicedev.enums;

public enum FilterExceptionErrorMessages {

    MISSING_EXPENSES_FILTER_KEY("missing filter key Expenses"),
    MISSING_ASSETS_FILTER_KEY("missing filter key Assets");

    private final String message;

    FilterExceptionErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
