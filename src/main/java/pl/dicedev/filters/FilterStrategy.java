package pl.dicedev.filters;

import org.springframework.stereotype.Component;
import pl.dicedev.enums.FilterSpecification;

import java.util.Map;

@Component
public class FilterStrategy {

    public Map<String, FilterParametersValidator> filterParametersValidator;

    public FilterStrategy(Map<String, FilterParametersValidator> filterParametersValidator) {
        this.filterParametersValidator = filterParametersValidator;
    }

    public void checkFilterForSpecification(Map<String, String> filter, FilterSpecification filterSpecification) {
        FilterParametersValidator chosenValidator = filterParametersValidator.get(filterSpecification.getValidator());
        chosenValidator.assertFilter(filter);
    }

}
