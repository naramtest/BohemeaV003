package com.emargystudio.bohemeav0021.Model;

public class Table {

    private int table_number;
    private int chair_number;
    private String table_image;



    public Table(int table_number, int chair_number, String table_image) {
        this.table_number = table_number;
        this.chair_number = chair_number;
        this.table_image = table_image;
    }

    public int getTable_number() {
        return table_number;
    }

    public void setTable_number(int table_number) {
        this.table_number = table_number;
    }

    public int getChair_number() {
        return chair_number;
    }

    public void setChair_number(int chair_number) {
        this.chair_number = chair_number;
    }

    public String getTable_image() {
        return table_image;
    }

    public void setTable_image(String table_image) {
        this.table_image = table_image;
    }
}
