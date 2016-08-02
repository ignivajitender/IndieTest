package com.igniva.indiecore.controller;
import com.igniva.indiecore.ui.adapters.PostCommentAdapter;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;

/**
 *
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnCommentListItemClickListner {
      void onCommentListItemClicked(PostCommentAdapter.RecyclerViewHolders view, int position, String commentId);
}

