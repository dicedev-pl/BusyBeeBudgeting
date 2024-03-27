package pl.dicedev.filters;

import org.springframework.beans.factory.annotation.Autowired;
import pl.dicedev.enums.FilterParametersCalendarEnum;
import pl.dicedev.enums.FilterSpecification;
import pl.dicedev.enums.MonthsEnum;
import pl.dicedev.repositories.entities.UserEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class FilterRange<T> {

    @Autowired
    private FilterStrategy filterStrategy;

    public List<T> getAllByFilter(Map<String, String> filters, UserEntity user) {
        if (FilterSpecification.FOR_EXPENSES.getValidator().equals(getFilterName()))
            filterStrategy.checkFilterForSpecification(filters, FilterSpecification.FOR_EXPENSES);
        if (FilterSpecification.FOR_ASSETS.getValidator().equals(getFilterName()))
            filterStrategy.checkFilterForSpecification(filters, FilterSpecification.FOR_ASSETS);

        if (filters.containsKey(FilterParametersCalendarEnum.DATE_TO.getKey())) {
            String dateFrom = filters.get(FilterParametersCalendarEnum.DATE_FORM.getKey());
            String dateTo = filters.get(FilterParametersCalendarEnum.DATE_TO.getKey());

            return getAllExpensesBetweenDate(dateFrom, dateTo, user);
        } else if (filters.containsKey(FilterParametersCalendarEnum.YEAR.getKey())) {
            String year = filters.get(FilterParametersCalendarEnum.YEAR.getKey());
            String month = filters.get(FilterParametersCalendarEnum.MONTH.getKey());

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

    protected abstract String getFilterName();

}
