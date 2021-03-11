package com.sursulet.go4lunch.ui.autocomplete;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.ui.OnItemClickListener;

public class PlaceAutocompleteAdapter extends ListAdapter<String, PlaceAutocompleteAdapter.AutocompleteViewHolder> {

    private final OnItemClickListener onItemClickListener;

    public PlaceAutocompleteAdapter(
            @NonNull DiffUtil.ItemCallback<String> diffCallback,
            OnItemClickListener onItemClickListener
    ) {
        super(diffCallback);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AutocompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AutocompleteViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.autocomplete_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AutocompleteViewHolder holder, int position) {
        String text = getItem(position);
        holder.bind(text);
    }

    public class AutocompleteViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout item;
        private final TextView textView;

        public AutocompleteViewHolder(@NonNull View itemView) {
            super(itemView);
            this.item = itemView.findViewById(R.id.autocomplete_item);
            this.textView = itemView.findViewById(R.id.autocomplete_text);
        }

        public void bind(String text) {
            textView.setText(text);
            item.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition()));
        }
    }

    public static final DiffUtil.ItemCallback<String> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equalsIgnoreCase(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equalsIgnoreCase(newItem);
                }
            };
}
