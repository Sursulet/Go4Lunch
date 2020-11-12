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
import com.sursulet.go4lunch.model.User;

public class WorkmatesAdapter extends ListAdapter<WorkmatesUiModel, WorkmatesAdapter.WorkmatesViewHolder> {

    protected WorkmatesAdapter(@NonNull DiffUtil.ItemCallback<WorkmatesUiModel> diffCallback) {
        super(diffCallback);
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

    static class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView txt;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.workmate_photo);
            txt = itemView.findViewById(R.id.workmate_text);
        }

        public void bind(WorkmatesUiModel workmatesUiModel) {
            String b = workmatesUiModel.getTxt();
            txt.setText(b);
            Glide.with(photo)
                    .load(workmatesUiModel.photo)
                    .circleCrop()
                    .into(photo);
        }
    }
}
