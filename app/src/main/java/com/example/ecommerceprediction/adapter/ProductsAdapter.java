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
import com.example.ecommerceprediction.activity.ReviewsActivity;
import com.example.ecommerceprediction.holders.Products;
import com.example.ecommerceprediction.R;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    // Declare context and list of products
    private final Context context;
    private final List<Products> sProductsList;

    // Constructor to initialize context and products list
    public ProductsAdapter(Context context, List<Products> sProductsList) {
        this.context = context;
        this.sProductsList = sProductsList;
    }

    // Method to inflate individual product view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.individual_browse_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to each product view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set brand and model name of product
        holder.brandName.setText(sProductsList.get(position).getBrand());
        holder.modelName.setText((CharSequence) sProductsList.get(position).getModel());

        // Check gender type and set text accordingly
        if ("Men".equals(sProductsList.get(position).getGender())){
            holder.genderType.setText("Mens");
        } else {
            holder.genderType.setText("Womens");
        }

        // Set the product price
        holder.productsPrice.setText(String.format("£" + "%.2f", sProductsList.get(position).getPrice()));

        // Use Glide library to load image from URL into ImageView
        Glide.with(context).load(sProductsList.get(position).getP_image_url()).into(holder.productsImg);

        // Set onClickListener for the product image to start the DescriptActivity
        holder.productsImg.setOnClickListener(view -> {
            Intent intent= new Intent(context, DescriptActivity.class);
            intent.putExtra("descript", sProductsList.get(position));
            context.startActivity(intent);
        });

        // Set onClickListener for the reviewsButton to start the ReviewsActivity
        // Added null check to prevent NullPointerException
        if (holder.reviewsButton != null) {
            holder.reviewsButton.setOnClickListener(view -> {
                Intent intent = new Intent(context, ReviewsActivity.class);
                intent.putExtra("product_id", sProductsList.get(position).getProductID());
                context.startActivity(intent);
            });
        }
    }

    // Method to get count of product items
    @Override
    public int getItemCount() {
        return sProductsList.size();
    }

    // ViewHolder class to hold view components of individual product item
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productsImg;
        TextView brandName;
        TextView modelName;
        TextView genderType;
        TextView productsPrice;
        TextView reviewsButton;  // New field for reviewsButton

        // ViewHolder constructor to initialize the view components
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.b_name);
            productsImg = itemView.findViewById(R.id.browse_img);
            modelName = itemView.findViewById(R.id.b_model);
            genderType = itemView.findViewById(R.id.b_gender);
            productsPrice = itemView.findViewById(R.id.b_price);
            reviewsButton = itemView.findViewById(R.id.reviewsButton);  // Initialize reviewsButton
        }
    }

    // Method to update products data and refresh the adapter
    public void updateData(List<Products> newProductsList) {
        this.sProductsList.clear();
        this.sProductsList.addAll(newProductsList);
        notifyDataSetChanged();
    }
}
