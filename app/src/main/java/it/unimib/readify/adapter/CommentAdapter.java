package it.unimib.readify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.model.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private TextView textViewComment;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO put name
            // textViewName = name of the user
            textViewComment = itemView.findViewById(R.id.edittext_comment);
        }

        void bind(Comment comment) {
            // TODO put name
            // textViewName.setText(comment.getName());
            textViewComment.setText(comment.getComment());
        }
    }
}
