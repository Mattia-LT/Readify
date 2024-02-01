package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.ALREADY_READ;
import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookSearchItemBinding;
import it.unimib.readify.databinding.BookSearchLoadingItemBinding;
import it.unimib.readify.databinding.UserSearchItemBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.User;

public class UserSearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onUserItemClick(User user);
        void onAddToCollectionButtonPressed(int position);
    }

    private final List<User> userList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;


    public UserSearchResultAdapter(List<User> userList, Application application, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserSearchItemBinding binding = UserSearchItemBinding.inflate(inflater, parent, false);
        return new UserSearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof UserSearchResultViewHolder){
            ((UserSearchResultViewHolder) holder).bind(userList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(userList != null){
            return userList.size();
        }
        return 0;
    }

    // custom viewholder to bind data to recyclerview items (search result items)
    public class UserSearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final UserSearchItemBinding binding;

        public UserSearchResultViewHolder(@NonNull UserSearchItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(User user) {
            binding.textviewUsername.setText(user.getUsername());
            String booksRead = application.getString(R.string.textview_books_read);
            int numberOfBooks = 0;
            List<Collection> collections = user.getCollections();
            for(Collection collection : collections){
                if(collection != null && collection.getName().equals(ALREADY_READ)){
                    numberOfBooks = collection.getBooks().size();
                }
            }
            booksRead = booksRead.concat(String.valueOf(numberOfBooks));
            binding.textviewBooksRead.setText(booksRead);
            // todo gestire foto profilo
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imagebutton_add_icon) {
                // hai premuto il bottone per aggiungere alla raccolta
                //todo gestire aggiunta alla raccolta
                //setImageViewFavoriteNews(!newsList.get(getAdapterPosition()).isFavorite());
                //onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                //hai premuto qualcos'altro
                onItemClickListener.onUserItemClick(userList.get(getAdapterPosition()));
            }
        }

        private void setImageViewAddedBook(boolean isFavorite) {
            /*
            if (isFavorite) {
                imageViewFavoriteNews.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
                imageViewFavoriteNews.setColorFilter(
                        ContextCompat.getColor(
                                imageViewFavoriteNews.getContext(),
                                R.color.red_500)
                );
            } else {
                imageViewFavoriteNews.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
                imageViewFavoriteNews.setColorFilter(
                        ContextCompat.getColor(
                                imageViewFavoriteNews.getContext(),
                                R.color.black)
                );
            }
            */
        }
    }
}

