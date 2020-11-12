package com.sursulet.go4lunch.ui.workmates;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.sursulet.go4lunch.model.User;

public class WorkmatesUiModel {

    String uid;
    String txt;
    String photo;

    public WorkmatesUiModel(String id, String txt, String photo) {
        this.uid = id;
        this.txt = txt;
        this.photo = photo;
    }

    public String getUid() { return uid; }

    public String getTxt() { return txt; }

    public String getPhoto() {
        return photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesUiModel workmatesUiModel = (WorkmatesUiModel) o;
        return uid.equals(workmatesUiModel.uid) &&
                txt.equals(workmatesUiModel.txt) &&
                photo.equals(workmatesUiModel.photo);
    }

    public static final DiffUtil.ItemCallback<WorkmatesUiModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WorkmatesUiModel>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull WorkmatesUiModel oldWorkmatesUiModel, @NonNull WorkmatesUiModel newWorkmatesUiModel) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldWorkmatesUiModel.getUid().equals(newWorkmatesUiModel.getUid());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull WorkmatesUiModel oldWorkmatesUiModel, @NonNull WorkmatesUiModel newWorkmatesUiModel) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldWorkmatesUiModel.equals(newWorkmatesUiModel);
                }
            };
}
