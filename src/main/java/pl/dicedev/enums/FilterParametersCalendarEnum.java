package pl.dicedev.enums;

public enum FilterParametersCalendarEnum {
    MONTH("month"),
    YEAR("year"),
    DATE_FORM("from"),
    DATE_TO("to");

    private final String filterParameter;

    FilterParametersCalendarEnum(String filterParameter) {
        this.filterParameter = filterParameter;
    }

    public String getKey() {
        return filterParameter;
    }
}
