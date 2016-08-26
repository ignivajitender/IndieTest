package com.igniva.indiecore.controller;

import android.widget.TextView;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnReplyDislikeClickListner {
      void onReplyDislikeClicked(TextView dislike, int position, String replyId, String type);
}
