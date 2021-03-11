package com.sursulet.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Map;

public class WorkmatesUiModel {

    final String uid;
    final String sentence;
    final Map<String, String> map;
    final String photo;
    final int sentenceStyle;

    public WorkmatesUiModel(String id, Map<String, String> map, String sentence, String photo, int sentenceStyle) {
        this.uid = id;
        this.map = map;
        this.sentence = sentence;
        this.photo = photo;
        this.sentenceStyle = sentenceStyle;
    }

    public String getUid() { return uid; }
    public Map<String, String> getMap() { return map; }
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
