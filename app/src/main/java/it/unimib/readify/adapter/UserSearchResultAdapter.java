package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.ALREADY_READ;

import android.app.Application;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import it.unimib.readify.R;
import it.unimib.readify.databinding.UserSearchItemBinding;
import it.unimib.readify.model.Collection;
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
            if(collections != null){
                for(Collection collection : collections){
                    if(collection != null && collection.getName().equals(ALREADY_READ)){
                        numberOfBooks = collection.getBooks().size();
                    }
                }
            }
            booksRead = booksRead.concat(String.valueOf(numberOfBooks));
            binding.textviewBooksRead.setText(booksRead);
            //todo mettere colori diversi alle foto profilo
            Drawable coloredIcon = ContextCompat.getDrawable(application, R.drawable.ic_baseline_profile_24);
            Random random = new Random();
            int randomNumber = random.nextInt(4);
            int newColor = 0;

            switch (randomNumber){
                case 0:
                    newColor = application.getResources().getColor(R.color.orange, null);
                    break;
                case 1:
                    newColor = application.getResources().getColor(R.color.light_purple, null);
                    break;
                case 2:
                    newColor = application.getResources().getColor(R.color.login_blue, null);
                    break;
                case 3:
                    newColor = application.getResources().getColor(R.color.red_undo, null);
                    break;
            }
            if (coloredIcon != null) {
                coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
            }
            binding.imageviewUser.setImageDrawable(coloredIcon);
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

