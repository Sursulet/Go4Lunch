package com.sursulet.go4lunch.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.ui.OnItemClickListener;

public class RestaurantAdapter extends ListAdapter<ListUiModel, RestaurantAdapter.RestaurantViewHolder> {

    private final OnItemClickListener onItemClickListener;

    protected RestaurantAdapter(
            @NonNull DiffUtil.ItemCallback<ListUiModel> diffCallback,
            OnItemClickListener onItemClickListener
    ) {
        super(diffCallback);
        this.onItemClickListener = onItemClickListener;
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

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout item;

        ImageView photo;
        TextView name;
        TextView txt;
        TextView opening;
        TextView distance;
        TextView nbWorkmates;
        RatingBar ratingBar;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item_restaurant);
            photo = itemView.findViewById(R.id.restaurant_photo);
            name = itemView.findViewById(R.id.restaurant_name);
            txt = itemView.findViewById(R.id.restaurant_txt);
            opening = itemView.findViewById(R.id.restaurant_opening);
            distance = itemView.findViewById(R.id.restaurant_distance);
            nbWorkmates = itemView.findViewById(R.id.restaurant_nbWorkmates);
            ratingBar = itemView.findViewById(R.id.restaurant_rating_bar);
        }

        public void bind(ListUiModel listUiModel) {
            name.setText(listUiModel.name);
            txt.setText(listUiModel.getSentence());
            opening.setText(listUiModel.getOpening());
            distance.setText(listUiModel.getDistance());
            nbWorkmates.setText(listUiModel.getNbWorkmates());
            ratingBar.setRating(listUiModel.getRating());

            Glide.with(photo)
                    .load(listUiModel.photoUrl)
                    .transform(new CenterCrop())
                    .into(photo);

            item.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition()));
        }
    }
}
