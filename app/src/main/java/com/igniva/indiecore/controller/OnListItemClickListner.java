package com.igniva.indiecore.controller;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;

/**
 *
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnListItemClickListner {
      void onListItemClicked(WallPostAdapter.RecyclerViewHolders view, int position,String postId);
}
