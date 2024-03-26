package pl.dicedev.excetpions;

import pl.dicedev.enums.FilterExceptionErrorMessages;

public class MissingAssetsFilterException extends RuntimeException {

    private String place;

    public MissingAssetsFilterException(String missingKey, String place) {
        super(FilterExceptionErrorMessages.MISSING_ASSETS_FILTER_KEY.getMessage() + " " + missingKey);
        this.place = place;
    }
}
