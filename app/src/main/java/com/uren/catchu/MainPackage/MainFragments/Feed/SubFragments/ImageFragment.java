package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MediaSerializable;
import com.uren.catchu.R;

public class ImageFragment extends Fragment {
    // Store instance variables
    private MediaSerializable media;

    // newInstance constructor for creating fragment with arguments
    public static ImageFragment newInstance(MediaSerializable mediaSerializable) {
        ImageFragment fragmentFirst = new ImageFragment();
        Bundle args = new Bundle();
        args.putSerializable("MediaSerializable", mediaSerializable);
        fragmentFirst.setArguments(args);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        media = (MediaSerializable) getArguments().getSerializable("MediaSerializable");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_image, container, false);


        ImageView imgFeedItem = (ImageView) view.findViewById(R.id.imgFeedItem);

        Glide.with(getContext())
                .load(media.getMedia().getUrl())
                .apply(RequestOptions.centerInsideTransform())
                .into(imgFeedItem);


        return view;
    }
}
