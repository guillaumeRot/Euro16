package com.euro16.Utils.RowsChoix;

/**
 * Created by Guillaume on 20/05/2016.
 */
public class RowActualite {

    private String date;
    private String title;
    private String description;

    public RowActualite(String date, String title, String description) {
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RowActualite{" +
                "date = '" + date + '\'' +
                ", title = '" + title + '\'' +
                ", description = '" + description + '\'' +
                '}';
    }
}
