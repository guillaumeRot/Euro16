package com.euro16.Utils.Enums;

/**
 * Created by Guillaume on 02/05/2016.
 */
public enum EDateFormat {

    DATETIME_BASE("yyyy-MM-dd HH:mm:ss"),
    DATETIME_GET_MATCH("dd-MM-yyyy HH:mm:ss"),
    DATE_SIMPLE("dd/MM/yyyy"),
    DATETIME_PRONOSTIC("EEEE dd MMMM - HH:mm"),
    DATETIME_PARSE_ACTU("EEE, dd MMM yyyy HH:mm:ss z");

    private final String formatDate;

    EDateFormat(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getFormatDate() {
        return this.formatDate;
    }
}
