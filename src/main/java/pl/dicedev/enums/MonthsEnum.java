package pl.dicedev.enums;

public enum MonthsEnum {
    JANUARY("01", 31),
    FEBRUARY("02", 28),
    MARCH("03", 31),
    APRIL("04", 30),
    MAY("05", 31),
    JUNE("06", 30),
    JULY("07", 31),
    AUGUST("08", 31),
    SEPTEMBER("09", 30),
    OCTOBER("10", 31),
    NOVEMBER("11", 30),
    DECEMBER("12", 31);

    private final String monthsIdx;
    private final int maxDays;

    MonthsEnum(String monthsIdx, int maxDays) {
        this.monthsIdx = monthsIdx;
        this.maxDays = maxDays;
    }

    public String getFirstDayForYear(String year) {
        return year + "-" + this.monthsIdx + "-01";
    }

    public String getLastDayForYear(String year) {
        return year + "-" + this.monthsIdx + "-" + this.maxDays;
    }
}
