package pl.dicedev.enums;

public enum FilterSpecification {
    FOR_ASSETS("forAssertValidator"),
    FOR_EXPENSES("forExpensesValidator");

    private final String validator;

    FilterSpecification(String validator) {
        this.validator = validator;
    }

    public String getValidator() {
        return validator;
    }
}
