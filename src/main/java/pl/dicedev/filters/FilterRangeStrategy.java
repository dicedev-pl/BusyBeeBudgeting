package pl.dicedev.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.dicedev.enums.FilterSpecification;
import pl.dicedev.repositories.entities.UserEntity;

import java.util.List;
import java.util.Map;

@Component
public class FilterRangeStrategy<T> {

    private Map<String, FilterRange> filterRange;

    public FilterRangeStrategy(Map<String, FilterRange> filterRange) {
        this.filterRange = filterRange;
    }

    public List<T> getFilteredDataForSpecification(UserEntity user, Map<String, String> filters, FilterSpecification filterSpecification) {
        return filterRange.get(filterSpecification.getRange()).getAllByFilter(filters, user, filterSpecification);
    }

}
