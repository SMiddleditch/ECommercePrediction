package com.example.ecommerceprediction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceprediction.R;
import com.example.ecommerceprediction.activity.address.CrudAddressActivity;
import com.example.ecommerceprediction.holders.Account;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    // Adapter properties
    private Context applicationContext;
    private List<Account> aAccountList;
    private CrudAddressActivity crudAddressActivity;
    private RadioButton lastChecked = null;
    private int lastCheckedPos = 0;

    // Constructor for cases where CrudAddressActivity isn't provided
    public AccountAdapter(Context applicationContext, List<Account> aAccountList) {
        this.applicationContext = applicationContext;
        this.aAccountList = aAccountList;
        this.crudAddressActivity = null;
    }

    // Constructor for cases where CrudAddressActivity is provided
    public AccountAdapter(Context applicationContext, List<Account> aAccountList, CrudAddressActivity crudAddressActivity) {
        this.applicationContext = applicationContext;
        this.aAccountList = aAccountList;
        this.crudAddressActivity = crudAddressActivity;
    }

    // onCreateViewHolder inflates individual list items
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (crudAddressActivity != null) {
            view = LayoutInflater.from(applicationContext).inflate(R.layout.individual_crud_deats, parent, false);
        } else {
            view = LayoutInflater.from(applicationContext).inflate(R.layout.individual_account_deats, parent, false);
        }
        return new ViewHolder(view);
    }

    // onBindViewHolder populates data into each item of the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the account for the current position
        Account account = aAccountList.get(position);

        // Set the data on the ViewHolder's elements
        holder.name.setText(account.getName());
        holder.address.setText(account.getAddress());
        holder.town.setText(account.getTown());
        holder.post.setText(account.getPost());
        holder.number.setText(String.valueOf(account.getNumber()));

        // RadioButton logic for selection of a single item
        if (holder.aRadio != null) {
            holder.aRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton checked_rb = (RadioButton) v;
                    if (lastChecked != null) {
                        lastChecked.setChecked(false);
                        aAccountList.get(lastCheckedPos).setSelected(false); // deselect last item
                    }
                    lastChecked = checked_rb;
                    lastCheckedPos = position;

                    aAccountList.get(position).setSelected(true); // select current item
                }
            });

            // Maintain state of radio button
            if (aAccountList.get(position).isSelected()) {
                holder.aRadio.setChecked(true);
                lastChecked = holder.aRadio;
                lastCheckedPos = position;
            } else {
                holder.aRadio.setChecked(false);
            }
        }

        // Add edit and delete buttons if in CrudAddressActivity context
        if (crudAddressActivity != null) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crudAddressActivity.editAccount(account.getId());
                }
            });

            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crudAddressActivity.deleteAccount(account.getId());
                }
            });
        }
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return aAccountList.size();
    }

    // Check if any item is selected
    public boolean isAnyItemSelected() {
        for (Account account : aAccountList) {
            if (account.isSelected()) {
                return true;
            }
        }
        return false;
    }

    // ViewHolder class to hold the UI components for each item
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView address;
        private TextView town;
        private TextView post;
        private TextView number;
        private RadioButton aRadio;
        private ImageButton editButton;
        private ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the UI components
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            town = itemView.findViewById(R.id.town);
            post = itemView.findViewById(R.id.post);
            number = itemView.findViewById(R.id.number);
            aRadio = itemView.findViewById(R.id.address_selected); // Replace with the actual ID

            // Initialize edit and delete buttons if in CrudAddressActivity context
            if (crudAddressActivity != null) {
                editButton = itemView.findViewById(R.id.edit_button);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }
    }
}
