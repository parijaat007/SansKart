package com.example.sanskart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanskart.R;
import com.google.android.material.button.MaterialButton;

public class FoodItemViewHolder extends RecyclerView.ViewHolder {

    public TextView mFoodItemName;
    public ImageView mFoodImage;
    public TextView mFoodItemPrice;
    public MaterialButton mAddToCart;
    public TextView mShopProvider;

    public FoodItemViewHolder(@NonNull View itemView) {
        super(itemView);

        mFoodItemName = (TextView) itemView.findViewById(R.id.food_name);
        mFoodItemPrice = (TextView) itemView.findViewById(R.id.food_price);
        mFoodImage = (ImageView) itemView.findViewById(R.id.food_image);
        mAddToCart = (MaterialButton) itemView.findViewById(R.id.addtocart_btn);
        mShopProvider = (TextView) itemView.findViewById(R.id.shop_provider);
    }

}
