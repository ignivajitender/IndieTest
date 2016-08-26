package com.igniva.indiecore.controller;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnReplyDeleteClickListner {
      void onReplyDeleteClicked(ImageView delete, int position, String replyId, String type);
}
