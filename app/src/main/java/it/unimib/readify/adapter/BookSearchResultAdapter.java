package it.unimib.readify.adapter;

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

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookSearchItemBinding;
import it.unimib.readify.databinding.BookSearchLoadingItemBinding;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookSearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BOOK_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
        void onAddToCollectionButtonPressed(int position);
    }

    private final List<OLWorkApiResponse> bookList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;


    public BookSearchResultAdapter(List<OLWorkApiResponse> bookList, Application application, OnItemClickListener onItemClickListener) {
        this.bookList = bookList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (bookList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return BOOK_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == BOOK_VIEW_TYPE) {
            BookSearchItemBinding binding = BookSearchItemBinding.inflate(inflater, parent, false);
            return new SearchResultViewHolder(binding);
        } else {
            BookSearchLoadingItemBinding binding = BookSearchLoadingItemBinding.inflate(inflater, parent, false);
            return new LoadingSearchResultViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SearchResultViewHolder){
            ((SearchResultViewHolder) holder).bind(bookList.get(position));
        } else if(holder instanceof LoadingSearchResultViewHolder){
            ((LoadingSearchResultViewHolder) holder).activate();
        }
    }

    @Override
    public int getItemCount() {
        if(bookList != null){
            return bookList.size();
        }
        return 0;
    }

    // custom viewholder to bind data to recyclerview items (search result items)
    public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final BookSearchItemBinding binding;

        public SearchResultViewHolder(@NonNull BookSearchItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.imagebuttonAddIcon.setOnClickListener(this);
        }

        public void bind(OLWorkApiResponse book) {
            binding.textviewBookTitle.setText(book.getTitle());
            binding.textviewBookDescription.setText(book.getDescription().getValue());
            // todo gestire cambio icona quando aggiungo libro
            // setImageViewFavoriteNews(newsList.get(getAdapterPosition()).isFavorite());
            if(book.getCovers() != null){
                int cover = -1;
                int pos = 0;
                while (cover == -1 && pos < book.getCovers().size()) {
                    cover = book.getCovers().get(pos);
                }
                if (cover == -1) {
                    binding.imageviewBookCover.setImageResource(R.drawable.image_not_available);
                } else {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.loading_image_gif)
                            .error(R.drawable.image_not_available)
                            .transform(new CenterCrop());

                    Glide.with(application)
                            .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                            .apply(requestOptions)
                            .into(binding.imageviewBookCover);
                }
            } else {
                Glide.with(application)
                        .load(R.drawable.image_not_available)
                        .into(binding.imageviewBookCover);
            }
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
                onItemClickListener.onBookItemClick(bookList.get(getAdapterPosition()));
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

    public static class LoadingSearchResultViewHolder extends RecyclerView.ViewHolder {
        //todo devo ancora implementarlo
        private final BookSearchLoadingItemBinding binding;
        public LoadingSearchResultViewHolder(@NonNull BookSearchLoadingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void activate(){
            binding.progressbarLoadingBook.setIndeterminate(true);
        }
    }
}

