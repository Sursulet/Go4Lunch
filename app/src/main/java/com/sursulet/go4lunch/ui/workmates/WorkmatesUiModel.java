package com.sursulet.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class WorkmatesUiModel {

    String uid;
    String txt;
    String photo;
    int txtStyle;

    public WorkmatesUiModel(String id, String txt, String photo, int txtStyle) {
        this.uid = id;
        this.txt = txt;
        this.photo = photo;
        this.txtStyle = txtStyle;
    }

    public String getUid() { return uid; }
    public String getTxt() { return txt; }
    public String getPhoto() {
        return photo;
    }
    public int getTxtStyle() { return txtStyle; }

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
                        @NonNull WorkmatesUiModel oldWorkmatesUiModel,
                        @NonNull WorkmatesUiModel newWorkmatesUiModel
                ) {
                    return oldWorkmatesUiModel.getUid().equals(newWorkmatesUiModel.getUid());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull WorkmatesUiModel oldWorkmatesUiModel,
                        @NonNull WorkmatesUiModel newWorkmatesUiModel
                ) {
                    return oldWorkmatesUiModel.equals(newWorkmatesUiModel);
                }
            };
}
