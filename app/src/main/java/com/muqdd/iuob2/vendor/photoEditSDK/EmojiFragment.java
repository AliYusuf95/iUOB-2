package com.muqdd.iuob2.vendor.photoEditSDK;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.stories.StoriesActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ahmed Adel on 5/5/17.
 */

public class EmojiFragment extends Fragment implements EmojiAdapter.OnEmojiClickListener {

    private ArrayList<String> emojiIds;
    private StoriesActivity photoEditorActivity;
    private RecyclerView emojiRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoEditorActivity = (StoriesActivity) getActivity();

        String[] emojis = photoEditorActivity.getResources().getStringArray(R.array.photo_editor_emoji);

        emojiIds = new ArrayList<>();
        Collections.addAll(emojiIds, emojis);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_edit_list, container, false);

        emojiRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_photo_edit_rv);
        emojiRecyclerView.setHasFixedSize(true);
        emojiRecyclerView.setLayoutManager(new GridLayoutManager(photoEditorActivity, 4));
        EmojiAdapter adapter = new EmojiAdapter(photoEditorActivity, emojiIds);
        adapter.setOnEmojiClickListener(this);
        emojiRecyclerView.setAdapter(adapter);

        return rootView;
    }

    public RecyclerView getEmojiRecyclerView() {
        return emojiRecyclerView;
    }

    @Override
    public void onEmojiClickListener(String emojiName) {
        photoEditorActivity.addEmoji(emojiName);
    }
}
