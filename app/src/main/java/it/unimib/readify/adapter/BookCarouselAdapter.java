package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookHomeItemBinding;
import it.unimib.readify.databinding.BookLoadingHomeItemBinding;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BOOK_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
        void onAddToCollectionButtonPressed(int position);
    }


    private final List<OLWorkApiResponse> bookList;
    private final Application application;
    private final OnItemClickListener onBookItemClickListener;

    @Override
    public int getItemViewType(int position) {
        if (bookList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return BOOK_VIEW_TYPE;
        }
    }

    public BookCarouselAdapter(List<OLWorkApiResponse> bookList, Application application, OnItemClickListener onBookItemClickListener) {
        this.bookList = bookList;
        this.application = application;
        this.onBookItemClickListener = onBookItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == BOOK_VIEW_TYPE) {
            BookHomeItemBinding binding = BookHomeItemBinding.inflate(inflater, parent, false);
            return new  BookHomeViewHolder(binding);
        } else {
            BookLoadingHomeItemBinding binding = BookLoadingHomeItemBinding.inflate(inflater, parent, false);
            return new BookLoadingHomeViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BookHomeViewHolder){
            ((BookHomeViewHolder) holder).bind(bookList.get(position));
        } else if(holder instanceof BookLoadingHomeViewHolder){
            ((BookLoadingHomeViewHolder) holder).activate();
        }

    }

    @Override
    public int getItemCount() {
        if (bookList != null){
            return  bookList.size();
        }
        return 0;
    }

    public void refreshList(List<OLWorkApiResponse> books){
        int size = bookList.size();
        bookList.clear();
        notifyItemRangeRemoved(0, size);
        bookList.addAll(books);
        notifyItemRangeInserted(0, books.size());
    }

    // Custom ViewHolder
    public class BookHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final BookHomeItemBinding binding;

        public BookHomeViewHolder(@NonNull BookHomeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.bookCover.setOnClickListener(this);
            //TODO aggiungi qua bottone per aggiungere il libro e il relativo listener, magari se tieni premuto?
        }


        //TODO SISTEMA
        //secondo me la variabile pos non cambia mai il suo valore ed il while va in loop
        public void bind(OLWorkApiResponse book) {
            binding.bookTitle.setText(book.getTitle());
            if(book.getCovers() != null){
                int pos = 0;
                int cover = -1;
                while (cover == -1 && pos < book.getCovers().size()) {
                    cover = book.getCovers().get(pos);
                    pos++;
                }
                if (cover == -1) {
                    binding.bookCover.setImageResource(R.drawable.image_not_available);
                } else {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.loading_image_gif)
                            .error(R.drawable.image_not_available);
                    Glide.with(application)
                            .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                            .apply(requestOptions)
                            .into(binding.bookCover);
                }
            } else {
                Glide.with(application)
                        .load(R.drawable.image_not_available)
                        .into(binding.bookCover);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imagebutton_add_icon) {
                //todo hai TENUTO premuto il bottone per aggiungere alla raccolta???
                //todo gestire aggiunta alla raccolta
                //setImageViewFavoriteNews(!newsList.get(getAdapterPosition()).isFavorite());
                //onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                //hai premuto qualcos'altro
                //controllo perchè a volte in circostanze strane potrebbe crashare, cosi si evita sicuramente
                int position = Math.max(getAdapterPosition(), 0);
                onBookItemClickListener.onBookItemClick(bookList.get(position));
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

    public static class BookLoadingHomeViewHolder extends RecyclerView.ViewHolder{
        //todo da implementare
        private final BookLoadingHomeItemBinding binding;
        public BookLoadingHomeViewHolder(BookLoadingHomeItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        public void activate(){
            binding.progressbarLoadingBook.setIndeterminate(true);
            binding.bookTitle.setText(R.string.textview_loading);
        }
    }
























    }

