package com.igniva.indiecore.controller;

import android.widget.TextView;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnCommentDislikeClickListner {
      void onCommentDislikeClicked(TextView dislike, int position, String commentId, String type);
}
