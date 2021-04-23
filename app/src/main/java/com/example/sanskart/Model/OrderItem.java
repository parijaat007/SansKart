package com.example.sanskart.Model;

public class OrderItem
{
    String Customer_name;
    String Phone_Number;
    String Latitude;
    String Longitude;
    String CartTotalAmount;
    String Status;
    String Customer_UID;
    String Distance;

    public OrderItem()
    {

    }

    public OrderItem(String phone_number, String latitude, String longitude, String cartTotal,String status, String customer_UID, String distance, String name)
    {
        Phone_Number = phone_number;
        Latitude = latitude;
        Longitude = longitude;
        CartTotalAmount = cartTotal;
        Status = status;
        Customer_UID = customer_UID;
        Distance = distance;
        Customer_name = name;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_number) {
        Phone_Number = phone_number;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getCartTotalAmount() {
        return CartTotalAmount;
    }

    public void setCartTotal(String cartTotal) {
        this.CartTotalAmount = cartTotal;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCustomer_UID() {
        return Customer_UID;
    }

    public void setCustomer_UID(String customer_uid) {
        Customer_UID = customer_uid;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public void setCustomer_name(String customer_name) {
        Customer_name = customer_name;
    }

    public void setCartTotalAmount(String cartTotalAmount) {
        CartTotalAmount = cartTotalAmount;
    }

    public String getCustomer_name() {
        return Customer_name;
    }
}
