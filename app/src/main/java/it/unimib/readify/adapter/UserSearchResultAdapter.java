package it.unimib.readify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import it.unimib.readify.R;
import it.unimib.readify.databinding.UserSearchItemBinding;
import it.unimib.readify.model.User;

public class UserSearchResultAdapter extends ListAdapter<User, UserSearchResultAdapter.UserSearchResultViewHolder> {

    public interface OnItemClickListener {
        void onUserItemClick(User user);
    }

    private final OnItemClickListener onItemClickListener;


    public UserSearchResultAdapter(OnItemClickListener onItemClickListener) {
        super(new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.getIdToken().equals(newItem.getIdToken());
            }

            @Override
            public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserSearchItemBinding binding = UserSearchItemBinding.inflate(inflater, parent, false);
        return new UserSearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSearchResultViewHolder holder, int position) {
        if(position != RecyclerView.NO_POSITION){
            User currentUser = getItem(position);
            holder.bind(currentUser);
        }
    }

    public class UserSearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final UserSearchItemBinding binding;

        public UserSearchResultViewHolder(@NonNull UserSearchItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(User user) {
            binding.textviewUsername.setText(user.getUsername());
            int numberOfBooks = 0;
            //TODO implementare logica per vedere il numero di libri
//            List<Collection> collections = user.getFetchedCollections();
//            if(collections != null){
//                for(Collection collection : collections){
//                    if(collection != null && collection.getName().equals(ALREADY_READ)){
//                        numberOfBooks = collection.getBooks().size();
//                    }
//                }
//            }
            String booksRead = itemView.getResources().getString(R.string.textview_books_read);
            booksRead = booksRead.concat(String.valueOf(numberOfBooks));
            binding.textviewBooksRead.setText(booksRead);
            int avatarId;
            try {
                avatarId = R.drawable.class.getDeclaredField(user.getAvatar().toLowerCase()).getInt(null);
            } catch (Exception e) {
                avatarId = R.drawable.ic_baseline_profile_24;
            }
            Glide.with(itemView.getContext())
                    .load(avatarId)
                    .dontAnimate()
                    .into(binding.imageviewUser);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                User user = getItem(position);
                onItemClickListener.onUserItemClick(user);
            }
        }
    }

}

