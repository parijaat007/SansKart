package com.example.dubstep.Model;

public class CartInfo {
    String CartItemsTotal;
    String CartTotal;
    String Discount;

    public CartInfo() {
    }

    public CartInfo(String cartItemsTotal, String cartTotal, String discount) {
        CartItemsTotal = cartItemsTotal;
        CartTotal = cartTotal;
        Discount = discount;
    }

    public String getCartItemsTotal() {
        return CartItemsTotal;
    }

    public void setCartItemsTotal(String cartItemsTotal) {
        CartItemsTotal = cartItemsTotal;
    }

    public String getCartTotal() {
        return CartTotal;
    }

    public void setCartTotal(String cartTotal) {
        CartTotal = cartTotal;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
