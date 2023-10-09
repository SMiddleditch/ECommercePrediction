package com.example.ecommerceprediction.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.holders.Products;

import java.util.List;

// This class represents the Adapter for the Cart ListView
public class CartAdapter extends ArrayAdapter<Products> {

    // Constructor for CartAdapter. It takes a context and a list of Products
    public CartAdapter(Context context, List<Products> products) {
        super(context, 0, products);
    }

    // This method gets called for each item in the list to create a view for that item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("CartAdapter", "getView called for position: " + position);

        // If the convertView is null, inflate a new view for the item.
        // Else, reuse the existing convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.individual_cart_product, parent, false);
        }

        // Get the Product at the current position
        Products product = getItem(position);

        // Find views in the layout for different parts of the product
        ImageView c_img = convertView.findViewById(R.id.c_img);
        TextView cBrand = convertView.findViewById(R.id.cBrand);
        TextView cPrice = convertView.findViewById(R.id.cPrice);
        TextView cModel = convertView.findViewById(R.id.cModel);
        TextView cGender = convertView.findViewById(R.id.cGender);
        TextView cSize = convertView.findViewById(R.id.cSize);

        // Set the views in the layout with data from the product
        if (product != null) {
            Glide.with(getContext()).load(product.getP_image_url()).into(c_img); // Load product image with Glide
            cBrand.setText(product.getBrand()); // Set brand
            cPrice.setText(String.valueOf(product.getPrice())); // Set price
            cModel.setText(String.valueOf(product.getModel())); // Set model
            cSize.setText(String.valueOf(product.getSize())); // Set size
            cGender.setText(product.getGender()); // Set gender
            Log.d("CartAdapter", "Processed item: " + product.getBrand() + ", " + product.getPrice());
        }

        // Return the fully populated and ready to display view
        return convertView;
    }
}
