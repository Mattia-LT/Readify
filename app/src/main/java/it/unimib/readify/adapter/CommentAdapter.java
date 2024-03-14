package it.unimib.readify.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unimib.readify.R;
import it.unimib.readify.databinding.CommentItemBinding;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.User;

public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.CommentViewHolder> {

    public interface OnItemClickListener {
        void onCommentClick(Comment comment);
    }

    private final OnItemClickListener onItemClickListener;

    public CommentAdapter(OnItemClickListener onItemClickListener) {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.getCommentId().equals(newItem.getCommentId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CommentItemBinding binding = CommentItemBinding.inflate(inflater, parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = getItem(position);
        holder.bind(comment);
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
            if(comment != null){
                Locale locale = Locale.getDefault();
                if (locale.getLanguage().startsWith("it")) {
                    locale = new Locale("it", "IT");
                }

                Date commentDate = new Date(comment.getTimestamp());
                String pattern = "dd MMM yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
                String formattedDate = dateFormat.format(commentDate);

                binding.commentDate.setText(formattedDate);
                binding.commentText.setText(comment.getContent());

                User user = comment.getUser();
                int avatarId;
                if(user != null){
                    Log.d("USER OK", "USER OK");
                    binding.commentName.setText(comment.getUser().getUsername());
                    try {
                        avatarId = R.drawable.class.getDeclaredField(user.getAvatar().toLowerCase()).getInt(null);
                    } catch (Exception e) {
                        avatarId = R.drawable.ic_baseline_profile_24;
                    }
                    Glide.with(this.itemView.getContext())
                            .load(avatarId)
                            .dontAnimate()
                            .into(binding.commentImage);
                } else {
                    Log.d("USER NULL", "USER NULL");
                    //todo aggiungi errore nel caricamento dell'utente
                }
            }


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Comment comment = getItem(position);
                onItemClickListener.onCommentClick(comment);
            }
        }
    }
}
