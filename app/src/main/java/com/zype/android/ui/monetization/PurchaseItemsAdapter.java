package com.zype.android.ui.monetization;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zype.android.Billing.PurchaseItem;
import com.zype.android.R;

import java.util.ArrayList;
import java.util.List;

public class PurchaseItemsAdapter extends RecyclerView.Adapter<PurchaseItemsAdapter.ViewHolder> {
    private List<PurchaseItem> items;
    private IPurchaseItemsListener listener;

    public interface IPurchaseItemsListener {
        void onPurchaseItemSelected(PurchaseItem item);
    }

    public PurchaseItemsAdapter(@NonNull IPurchaseItemsListener listener) {
        this.listener = listener;
        items = new ArrayList<>();
    }

    public void setData(List<PurchaseItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchase_item_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = items.get(position);
        if (holder.item.product != null) {
            holder.textTitle.setText(holder.item.product.getTitle());
            holder.textPrice.setText(String.valueOf(holder.item.product.getPrice()));
            holder.textDescription.setText(holder.item.product.getDescription());
            holder.buttonContinue.setText(String.format(
                    holder.view.getContext().getString(R.string.subscription_item_button_continue),
                    holder.item.product.getTitle()));
            holder.buttonContinue.setOnClickListener(v -> {
                listener.onPurchaseItemSelected(holder.item);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public PurchaseItem item;
        public TextView textTitle;
        public TextView textPrice;
        public TextView textDescription;
        public Button buttonContinue;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textTitle = view.findViewById(R.id.textTitle);
            textPrice = view.findViewById(R.id.textPrice);
            textDescription = view.findViewById(R.id.textDescription);
            buttonContinue = view.findViewById(R.id.buttonContinue);
        }
    }
}
