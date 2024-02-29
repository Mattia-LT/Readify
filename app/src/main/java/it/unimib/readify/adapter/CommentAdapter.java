package it.unimib.readify.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import it.unimib.readify.R;
import it.unimib.readify.databinding.CommentItemBinding;
import it.unimib.readify.model.Comment;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onCommentClick(Comment comment);
    }

    private final List<Comment> commentList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public CommentAdapter(List<Comment> commentList, Application application, OnItemClickListener onItemClickListener) {
        this.commentList = commentList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CommentItemBinding binding = CommentItemBinding.inflate(inflater, parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        ( (CommentViewHolder) holder).bind(comment);
    }

    @Override
    public int getItemCount() {
        if(commentList != null){
            return commentList.size();
        }
        return 0;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final CommentItemBinding binding;

        public CommentViewHolder(@NonNull CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            // todo mettere il listener sulla foto profilo? parliamone
        }

        public void bind(Comment comment) {

            Locale locale = Locale.getDefault();
            if (locale.getLanguage().equals("it")) {
                locale = new Locale("it", "IT");
            }

            String pattern = "dd MMM yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
            String formattedDate = dateFormat.format(comment.getDate());

            binding.commentDate.setText(formattedDate);
            binding.commentText.setText(comment.getComment());
            binding.commentName.setText(comment.getUserId());

            //TODO caricare la foto profilo vera
            Glide.with(application)
                    .load(R.drawable.ic_baseline_profile_24)
                    .into(binding.commentImage);
        }

        @Override
        public void onClick(View v) {
            //todo gestire azioni quando clicchi sulla foto, dovrebbe aprire il profilo
            onItemClickListener.onCommentClick(commentList.get(getAdapterPosition()));
        }
    }
}
