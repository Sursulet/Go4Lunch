package com.sursulet.go4lunch.ui.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class ListUiModel {

    final String id;
    final String name;
    final String photoUrl;
    final String txt;
    final String opening;
    final String distance;
    final float rating;
    final String nbWorkmates;

    public ListUiModel(
            String id, String name, String photoUrl,
            String txt, String opening, String distance,
            float rating, String nbWorkmates
    ) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.txt = txt;
        this.opening = opening;
        this.distance = distance;
        this.rating = rating;
        this.nbWorkmates = nbWorkmates;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getTxt() {
        return txt;
    }

    public String getOpening() {
        return opening;
    }

    public String getDistance() {
        return distance;
    }

    public float getRating() {
        return rating;
    }

    public String getNbWorkmates() {
        return nbWorkmates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListUiModel listUiModel = (ListUiModel) o;
        return id.equals(listUiModel.id) &&
                name.equals(listUiModel.name) &&
                photoUrl.equals(listUiModel.photoUrl) &&
                txt.equals(listUiModel.txt) &&
                opening.equals(listUiModel.opening) &&
                distance.equals(listUiModel.distance) &&
                rating == listUiModel.rating &&
                nbWorkmates.equals(listUiModel.nbWorkmates);
    }

    public static final DiffUtil.ItemCallback<ListUiModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ListUiModel>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ListUiModel oldListUiModel, @NonNull ListUiModel newListUiModel) {
                    return oldListUiModel.getId().equals(newListUiModel.getId());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull ListUiModel oldListUiModel, @NonNull ListUiModel newListUiModel) {
                    return oldListUiModel.equals(newListUiModel);
                }
            };
}
