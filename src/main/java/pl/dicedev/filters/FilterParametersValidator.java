package pl.dicedev.filters;

import pl.dicedev.enums.FilterExpensesParametersEnum;
import pl.dicedev.excetpions.MissingExpensesFilterException;

import java.util.Map;

public abstract class FilterParametersValidator {

    public void assertFilter(Map<String, String> filters) {
        if (containsYearAndMonth(filters)) return;
        if (containsDateFormAndTo(filters)) return;
        if (containsYearAndNotMonth(filters))
            throwException("month", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
        if (containsMonthAndNotYear(filters))
            throwException("year", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
        if (containsDateToAndNotFrom(filters))
            throwException("date from", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
        if (containsDateFromAndNotTo(filters))
            throwException("date to", "a3ea21c3-4a40-43c1-889b-c9e9c5c3bbb3");
    }

    private static boolean containsYearAndMonth(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.MONTH.getKey()) && filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey());
    }

    private static boolean containsDateFormAndTo(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.DATE_FORM.getKey()) && filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey());
    }


    private static boolean containsYearAndNotMonth(Map<String, String> filters) {
        return !filters.containsKey(FilterExpensesParametersEnum.MONTH.getKey()) && filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey());
    }

    private static boolean containsMonthAndNotYear(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.MONTH.getKey()) && !filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey());
    }

    private static boolean containsDateFromAndNotTo(Map<String, String> filters) {
        return filters.containsKey(FilterExpensesParametersEnum.DATE_FORM.getKey()) && !filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey());
    }

    private static boolean containsDateToAndNotFrom(Map<String, String> filters) {
        return !filters.containsKey(FilterExpensesParametersEnum.DATE_FORM.getKey()) && filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey());
    }


    public abstract void throwException(String missingKey, String placeId);

}
