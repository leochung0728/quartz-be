package com.leochung0728.quartz.enums;

public enum SeasonMonth {
    ONE(1, 1),
    TWO(2, 1),
    THREE(3, 1),
    FOUR(4, 2),
    FIVE(5, 2),
    SIX(6, 2),
    SEVEN(7, 3),
    EIGHT(8, 3),
    NINE(9, 3),
    TEN(10, 4),
    ELEVEN(11, 4),
    TWELVE(12, 4);


    private int month;
    private int season;

    SeasonMonth(int month, int season) {
        this.month = month;
        this.season = season;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public static SeasonMonth getByMonth(int month) {
        for (SeasonMonth e : SeasonMonth.values()) {
            if (e.getMonth() == month) {
                return e;
            }
        }
        return null;
    }
}
