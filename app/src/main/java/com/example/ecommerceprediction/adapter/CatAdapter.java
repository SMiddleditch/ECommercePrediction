package com.example.ecommerceprediction.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.AllProductActivity;
import com.example.ecommerceprediction.holders.Categories;

import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {
    // Class variables for context and list of categories
    private Context context;
    private List<Categories> tCategoryList;

    // Constructor to initialize context and category list
    public CatAdapter(Context context, List<Categories> tCategoryList) {
        this.context = context;
        this.tCategoryList = tCategoryList;
    }

    // ViewHolder method to inflate individual category views
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.individual_cat_logo,parent,false);
        return new ViewHolder(view);
    }

    // onBindViewHolder method to bind data to each category view
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Use Glide library to load image from URL into ImageView
        Glide.with(context).load(tCategoryList.get(position).getB_image_url()).into(holder.brandImg);

        // Set onClickListener for each category image
        holder.brandImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                // Check if position is valid
                if (pos != RecyclerView.NO_POSITION) {
                    Categories clickedDataItem = tCategoryList.get(pos);
                    if (clickedDataItem.getBrand() != null) {
                        try {
                            // Start AllProductActivity for the clicked brand
                            Intent intent = new Intent(context, AllProductActivity.class);
                            intent.putExtra("brand", clickedDataItem.getBrand());
                            context.startActivity(intent);
                        } catch (Exception e) {
                            // Log any error occurred during starting the activity
                            Log.e("CatAdapter", "Error starting activity", e);
                        }
                    } else {
                        Log.d("CatAdapter", "Brand name is null for position " + pos);
                    }
                }
            }
        });
    }

    // Method to get count of category items
    @Override
    public int getItemCount() {
        return tCategoryList.size();
    }

    // ViewHolder class to hold view components of individual category item
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView brandImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize ImageView component
            brandImg= itemView.findViewById(R.id.cat_img);

            // Set click listener for entire item view, logs the clicked position
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Log.d("CatAdapter", "ItemView clicked at position " + pos);
                }
            });
        }
    }
}
