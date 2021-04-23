package com.example.sanskart.Model;

public class FoodItem {
    String base_price;
    String name;
    String imageUrl;



    String shopName;

    public FoodItem(){

    }

//    public FoodItem(String price, String name) {
//        this.base_price = price;
//        this.name = name;
//    }

    public FoodItem(String price, String name, String url, String shopName) {
        this.base_price = price;
        this.name = name;
        this.imageUrl = url;
        this.shopName = shopName;
    }

    public String getBase_price() {
        return base_price;
    }

    public void setBase_price(String base_price) {
        this.base_price = base_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
