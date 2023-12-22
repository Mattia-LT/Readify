package it.unimib.readify.adapter;

// BookAdapter.java

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookRecyclerViewCarouselAdapter extends RecyclerView.Adapter<BookRecyclerViewCarouselAdapter.BookViewHolder> {

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
        void onSaveButtonPressed(int position);
    }


    private final List<OLWorkApiResponse> bookList;
    private final Application application;
    private final OnItemClickListener onBookItemClickListener;

    public BookRecyclerViewCarouselAdapter(List<OLWorkApiResponse> bookList, Application application, OnItemClickListener onBookItemClickListener) {
        this.bookList = bookList;
        this.application = application;
        this.onBookItemClickListener = onBookItemClickListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_home_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bind(bookList.get(position));

    }

    @Override
    public int getItemCount() {
        if (bookList != null){
            return  bookList.size();
        }
        return 0;
    }


    // Custom ViewHolder
    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView bookCoverImageView;
        private final TextView bookTitleTextView;
        //private Book currentBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.book_cover);
            bookTitleTextView = itemView.findViewById(R.id.book_title);
            itemView.setOnClickListener(this);
            bookCoverImageView.setOnClickListener(this);
            //TODO aggiungi qua bottone per aggiungere il libro e il relativo listener
        }


        //TODO SISTEMA
        public void bind(OLWorkApiResponse book) {
            bookTitleTextView.setText(book.getTitle());

        }

        @Override
        public void onClick(View v) {
            onBookItemClickListener.onBookItemClick(bookList.get(getAdapterPosition()));
        }

            //TODO gestisco
            /*
            if (v.getId() == R.id.imageview_favorite_news) {
                setImageViewFavoriteNews(!newsList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                onItemClickListener.onNewsItemClick(newsList.get(getAdapterPosition()));
            }

            */
        }


         /*
    private void setImageViewFavoriteNews(boolean isFavorite) {
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

