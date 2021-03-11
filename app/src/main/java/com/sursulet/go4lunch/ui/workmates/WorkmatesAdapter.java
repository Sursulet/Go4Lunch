package com.sursulet.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.ui.OnItemClickListener;

public class WorkmatesAdapter extends ListAdapter<WorkmatesUiModel, WorkmatesAdapter.WorkmatesViewHolder> {

    private final OnItemClickListener onItemClickListener;

    public WorkmatesAdapter(@NonNull DiffUtil.ItemCallback<WorkmatesUiModel> diffCallback, OnItemClickListener onItemClickListener) {
        super(diffCallback);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.workmates_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        WorkmatesUiModel workmatesUiModel = getItem(position);
        holder.bind(workmatesUiModel);
    }

    class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        final ImageView photo;
        final TextView txt;
        final TextView name;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.workmate_photo);
            txt = itemView.findViewById(R.id.workmate_txt);
            name = itemView.findViewById(R.id.workmate_name);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition()));
        }

        public void bind(WorkmatesUiModel workmatesUiModel) {
            txt.setText(workmatesUiModel.getSentence());
            if(workmatesUiModel.getMap() != null){
                String string = "(" + workmatesUiModel.getMap().get("name") + ")";
                name.setText(string);
                name.setOnClickListener(v -> onItemClickListener.onItemName(workmatesUiModel.getMap()));
            } else {
                name.setText("");
            }

            txt.setTypeface(null, workmatesUiModel.getTxtStyle());
            Glide.with(photo)
                    .load(workmatesUiModel.photo)
                    .circleCrop()
                    .into(photo);
        }
    }
}
