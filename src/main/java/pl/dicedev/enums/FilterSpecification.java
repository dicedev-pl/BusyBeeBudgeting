package pl.dicedev.enums;

public enum FilterSpecification {
    FOR_ASSETS("forAssertValidator", "forAssertRange"),
    FOR_EXPENSES("forExpensesValidator", "forExpensesRange"),
    ;

    private final String validator;
    private final String range;

    FilterSpecification(String validator, String range) {
        this.validator = validator;
        this.range = range;
    }

    public String getValidator() {
        return validator;
    }

    public String getRange() {
        return range;
    }
}
