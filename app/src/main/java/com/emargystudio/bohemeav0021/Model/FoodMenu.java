package com.emargystudio.bohemeav0021.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "foodMenu")
public class FoodMenu implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int food_id;
    private int category_id;
    private String name;
    private String image_url;
    private String description;
    private int price;
    private int discount;

    public FoodMenu(int id, int food_id, int category_id, String name, String image_url, String description, int price, int discount) {
        this.id = id;
        this.food_id = food_id;
        this.category_id = category_id;
        this.name = name;
        this.image_url = image_url;
        this.description = description;
        this.price = price;
        this.discount = discount;
    }

    @Ignore
    public FoodMenu(int food_id, int category_id, String name, String image_url, String description, int price, int discount) {
        this.food_id = food_id;
        this.category_id = category_id;
        this.name = name;
        this.image_url = image_url;
        this.description = description;
        this.price = price;
        this.discount = discount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    @Ignore
    protected FoodMenu(Parcel in) {
        id = in.readInt();
        food_id = in.readInt();
        category_id = in.readInt();
        name = in.readString();
        image_url = in.readString();
        description = in.readString();
        price = in.readInt();
        discount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(food_id);
        dest.writeInt(category_id);
        dest.writeString(name);
        dest.writeString(image_url);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeInt(discount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FoodMenu> CREATOR = new Creator<FoodMenu>() {
        @Override
        public FoodMenu createFromParcel(Parcel in) {
            return new FoodMenu(in);
        }

        @Override
        public FoodMenu[] newArray(int size) {
            return new FoodMenu[size];
        }
    };
}
