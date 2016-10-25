package com.igniva.indiecore.controller;

import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnImageDownloadClick {

      void onDownloadClick(ProgressBar view, int position, String mediaID);
}
