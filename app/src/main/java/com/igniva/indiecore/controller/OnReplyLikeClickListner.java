package com.igniva.indiecore.controller;

import android.widget.TextView;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnReplyLikeClickListner {
      void onReplyLikeClicked(TextView like, int position, String replyId, String type);
}
