package com.igniva.indiecore.controller;
import com.igniva.indiecore.ui.adapters.CommentReplyAdapter;
import com.igniva.indiecore.ui.adapters.PostCommentAdapter;

/**
 *
 * Created by igniva-andriod-05 on 30/6/16.
 */
public interface OnReplyListItemClickListner {
      void onReplyListItemClick(CommentReplyAdapter.RecyclerViewHolders view, int position, String replyId);
}
