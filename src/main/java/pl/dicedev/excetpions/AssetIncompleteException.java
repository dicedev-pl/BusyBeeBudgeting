package pl.dicedev.excetpions;

public class AssetIncompleteException extends RuntimeException {

    private String errorCode;

    public AssetIncompleteException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
