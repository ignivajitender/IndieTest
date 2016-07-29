package com.igniva.indiecore.controller;
import com.igniva.indiecore.ui.adapters.TestAdapter;
import com.igniva.indiecore.ui.adapters.WallPostAdapter;

/**
 *
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnCommentListItemClickListnerTest {
      void onCommentListItemClicked(TestAdapter.RecyclerViewHolders view, int position, String postId);
}
