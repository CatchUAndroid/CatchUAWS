package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MediaSerializable;
import com.uren.catchu.R;

public class VideoFragment extends Fragment {
    // Store instance variables
    private MediaSerializable media;

    // newInstance constructor for creating fragment with arguments
    public static VideoFragment newInstance(MediaSerializable mediaSerializable) {
        VideoFragment fragmentFirst = new VideoFragment();
        Bundle args = new Bundle();
        args.putSerializable("MediaSerializable", mediaSerializable);
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
/*
        PlayerView playerView;
        SimpleExoPlayer player;

        playerView = view.findViewById(R.id.playerView);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());
        playerView.setPlayer(player);

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "feed-demo"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("https://s3.eu-west-2.amazonaws.com/catchuappbucket/video.mp4"));

        Log.i("videoUrl", media.getMedia().getUrl().toString());

        player.prepare(mediaSource);
        player.setPlayWhenReady(false);

*/
        return view;
    }
}
