package com.igniva.indiecore.controller;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnMediaPostClickListner {
      void onMediaPostClicked(ImageView media, int position, String postId, String type);
}
