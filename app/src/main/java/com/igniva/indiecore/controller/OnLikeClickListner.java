package com.igniva.indiecore.controller;

import android.widget.ImageView;
import android.widget.TextView;

import com.igniva.indiecore.ui.adapters.WallPostAdapter;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnLikeClickListner {
      void onLikeClicked(TextView like, int position, String postId, String type);
}
