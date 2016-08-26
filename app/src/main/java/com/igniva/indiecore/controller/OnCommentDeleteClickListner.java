package com.igniva.indiecore.controller;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnCommentDeleteClickListner {
      void onCommentDeleteClicked(ImageView delete, int position, String commentId, String type);
}
