package com.zype.android.ui.monetization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zype.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseFragment extends Fragment {
    public static final String TAG = PurchaseFragment.class.getSimpleName();

    private PaywallViewModel model;

    private PurchaseItemsAdapter adapter;

    public PurchaseFragment() {}

    public static PurchaseFragment getInstance() {
        PurchaseFragment fragment = new PurchaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_purchase, container, false);

        RecyclerView listPurchaseItems = rootView.findViewById(R.id.listPurchaseItems);
        adapter = new PurchaseItemsAdapter(item -> {
            model.makePurchase(getActivity(), item);
        });
        listPurchaseItems.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(PaywallViewModel.class);
        model.getPurchaseItems().observe(this, purchaseItems -> {
            adapter.setData(purchaseItems);
        });
    }
}
