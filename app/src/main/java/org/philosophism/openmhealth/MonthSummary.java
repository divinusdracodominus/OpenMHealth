package org.philosophism.openmhealth;

public class MonthSummary {
    private String monthYear;
    private int totalRecords;

    public MonthSummary(String monthYear, int totalRecords) {
        this.monthYear = monthYear;
        this.totalRecords = totalRecords;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public int getTotalRecords() {
        return totalRecords;
    }
}

