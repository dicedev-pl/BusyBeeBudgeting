package pl.dicedev.filters;

import org.springframework.stereotype.Component;
import pl.dicedev.excetpions.MissingAssetsFilterException;

@Component
public class AssetsFilterParametersValidator extends FilterParametersValidator {

    @Override
    public void throwException(String missingKey, String placeId) {
        throw new MissingAssetsFilterException(missingKey, placeId);
    }
}
