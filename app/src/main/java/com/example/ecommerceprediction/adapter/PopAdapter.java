package com.example.ecommerceprediction.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceprediction.activity.DescriptActivity;
import com.example.ecommerceprediction.holders.Products;
import com.example.ecommerceprediction.R;

import java.util.List;

public class PopAdapter extends RecyclerView.Adapter<PopAdapter.ViewHolder> {
    // Declare a context and a list of products to be used
    private final Context context;
    private final List<Products> productsList;

    // Constructor to initialize context and products list
    public PopAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    // ViewHolder method to inflate individual product views
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.individual_browse_item, parent, false);
        return new ViewHolder(view);
    }

    // onBindViewHolder method to bind data to each product view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set brand and model name of product
        holder.brandName.setText(productsList.get(position).getBrand());
        holder.modelName.setText((CharSequence) productsList.get(position).getModel());

        // Check gender type and set text accordingly
        if ("Men".equals(productsList.get(position).getGender())){
            holder.genderType.setText("Mens");
        } else {
            holder.genderType.setText("Womens");
        }

        // Set the product price
        holder.productsPrice.setText(String.format("£" + "%.2f", productsList.get(position).getPrice()));

        // Use Glide library to load image from URL into ImageView
        Glide.with(context).load(productsList.get(position).getP_image_url()).into(holder.productsImg);

        // Set onClickListener for the product image to start the DescriptActivity
        holder.productsImg.setOnClickListener(view -> {
            Intent intent= new Intent(context, DescriptActivity.class);
            intent.putExtra("descript", productsList.get(position));
            context.startActivity(intent);
        });
    }

    // Method to get count of product items
    @Override
    public int getItemCount() {
        return productsList.size();
    }

    // ViewHolder class to hold view components of individual product item
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productsImg;
        TextView modelName;
        TextView brandName;
        TextView genderType;  // New field for gender
        TextView productsPrice;

        // ViewHolder constructor to initialize the view components
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.b_name);
            modelName = itemView.findViewById(R.id.b_model);
            genderType = itemView.findViewById(R.id.b_gender);  // Initialize gender field
            productsImg = itemView.findViewById(R.id.browse_img);
            productsPrice = itemView.findViewById(R.id.b_price);
        }
    }
}
