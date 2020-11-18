package com.sursulet.go4lunch.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.sursulet.go4lunch.R;

public class RestaurantAdapter extends ListAdapter<ListUiModel, RestaurantAdapter.RestaurantViewHolder> {

    protected RestaurantAdapter(@NonNull DiffUtil.ItemCallback<ListUiModel> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        ListUiModel listUiModel = getItem(position);
        holder.bind(listUiModel);
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView name;
        TextView txt;
        TextView opening;
        TextView distance;
        TextView nbWorkmates;
        RatingBar ratingBar;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.photo_restaurant);
            name = itemView.findViewById(R.id.name_restaurant);
            txt = itemView.findViewById(R.id.txt_restaurant);
            distance = itemView.findViewById(R.id.distance_restaurant);
            ratingBar = itemView.findViewById(R.id.rating_restaurant);
        }

        public void bind(ListUiModel listUiModel) {
            name.setText(listUiModel.name);
            txt.setText(listUiModel.getTxt());
            distance.setText(listUiModel.getDistance());
            ratingBar.setRating(Float.parseFloat(listUiModel.getRating()));
            //ratingBar.setNumStars(Integer.parseInt(listUiModel.getRating()));

            Glide.with(photo)
                    .load(listUiModel.photoUrl)
                    .transform(new CenterCrop())
                    .into(photo);
        }
    }
}
