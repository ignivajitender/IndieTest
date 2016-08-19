package com.igniva.indiecore.controller;

import android.widget.TextView;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnCommentClickListner {
      void onCommentClicked(TextView comment, int position, String postId, String type);
}
