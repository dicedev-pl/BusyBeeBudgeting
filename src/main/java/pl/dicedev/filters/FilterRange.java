package pl.dicedev.filters;

import pl.dicedev.enums.FilterExpensesParametersEnum;
import pl.dicedev.enums.MonthsEnum;
import pl.dicedev.repositories.entities.UserEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class FilterRange<T> {

    public List<T> getAllByFilter(Map<String, String> filters, UserEntity user) {
        if (filters.containsKey(FilterExpensesParametersEnum.DATE_TO.getKey())) {
            String dateFrom = filters.get(FilterExpensesParametersEnum.DATE_FORM.getKey());
            String dateTo = filters.get(FilterExpensesParametersEnum.DATE_TO.getKey());

            return getAllExpensesBetweenDate(dateFrom, dateTo, user);
        } else if (filters.containsKey(FilterExpensesParametersEnum.YEAR.getKey())) {
            String year = filters.get(FilterExpensesParametersEnum.YEAR.getKey());
            String month = filters.get(FilterExpensesParametersEnum.MONTH.getKey());

            return getAllExpensesForMonthInYear(year, month, user);
        }
        return Collections.emptyList();
    }

    private List<T> getAllExpensesForMonthInYear(String year, String month, UserEntity user) {
        String dateFrom = MonthsEnum.valueOf(month.toUpperCase()).getFirstDayForYear(year);
        String dateTo = MonthsEnum.valueOf(month.toUpperCase()).getLastDayForYear(year);
        return getAllExpensesBetweenDate(dateFrom, dateTo, user);
    }

    private List<T> getAllExpensesBetweenDate(String dateFrom, String dateTo, UserEntity user) {
        String instantSuffix = "T00:00:00.001Z";

        return getAllEntityBetweenDate(
                Instant.parse(dateFrom + instantSuffix),
                Instant.parse(dateTo + instantSuffix),
                user
        );
    }

    public abstract List<T> getAllEntityBetweenDate(Instant fromDate, Instant toDate, UserEntity user);

}
