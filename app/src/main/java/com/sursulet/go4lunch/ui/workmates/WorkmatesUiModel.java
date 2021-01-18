package com.sursulet.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class WorkmatesUiModel {

    String uid;
    String sentence;
    String photo;
    int sentenceStyle;

    public WorkmatesUiModel(String id, String sentence, String photo, int sentenceStyle) {
        this.uid = id;
        this.sentence = sentence;
        this.photo = photo;
        this.sentenceStyle = sentenceStyle;
    }

    public String getUid() { return uid; }
    public String getSentence() { return sentence; }
    public String getPhoto() {
        return photo;
    }
    public int getTxtStyle() { return sentenceStyle; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesUiModel workmatesUiModel = (WorkmatesUiModel) o;
        return uid.equals(workmatesUiModel.uid) &&
                sentence.equals(workmatesUiModel.sentence) &&
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
