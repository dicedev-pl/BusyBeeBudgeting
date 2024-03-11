package pl.dicedev.enums;

public enum FilterExpensesParametersEnum {
    MONTH("month"),
    YEAR("year"),
    DATE_FORM("from"),
    DATE_TO("to");

    private final String filterParameter;

    FilterExpensesParametersEnum(String filterParameter) {
        this.filterParameter = filterParameter;
    }

    public String getKey() {
        return filterParameter;
    }
}
