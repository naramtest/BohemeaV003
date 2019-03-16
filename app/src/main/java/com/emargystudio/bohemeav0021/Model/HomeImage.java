package com.emargystudio.bohemeav0021.Model;

public class HomeImage {


    private int id;
    private String image_url;
    private String description;
    private int position;

    public HomeImage(String image_url, String description, int position) {
        this.image_url = image_url;
        this.description = description;
        this.position = position;
    }

    public HomeImage(int id, String image_url, String description, int position) {
        this.id = id;
        this.image_url = image_url;
        this.description = description;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
