package it.unimib.readify.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookCollectionItemBinding;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookItemCollectionAdapter extends
        RecyclerView.Adapter<BookItemCollectionAdapter.ViewHolder>{

    private List<OLWorkApiResponse> books;
    private final OnItemClickListener onItemClickListener;
    private final Application application;
    private BookCollectionItemBinding bookCollectionItemBinding;

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
    }

    public BookItemCollectionAdapter(OnItemClickListener onItemClickListener, Application application) {
        this.onItemClickListener = onItemClickListener;
        this.application = application;
        books = new ArrayList<>();
    }

    public void setBooks(List<OLWorkApiResponse> books) {
        this.books = books;
        notifyItemRangeChanged(0, this.books    .size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bookCollectionItemBinding = BookCollectionItemBinding.inflate(LayoutInflater.from(application.getApplicationContext()));
        return new ViewHolder(bookCollectionItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull BookItemCollectionAdapter.ViewHolder holder, int position) {
        holder.bind(books.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(books == null)
            return 0;
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(OLWorkApiResponse book, int position) {
            //Thumbnail
            if(book.getCovers() != null) {
                for (Integer cover: book.getCovers()) {
                    if(cover != -1) {
                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.loading_spinner)
                                .error(R.drawable.image_not_available);
                        Glide.with(application)
                                .load("https://covers.openlibrary.org/b/id/"
                                        + cover
                                        + "-L.jpg")
                                .apply(requestOptions)
                                .into(bookCollectionItemBinding.collectionThumbnailImageview);
                        break;
                    }
                }
            }
            //Title
            bookCollectionItemBinding.collectionNameTextview.setText(book.getTitle());
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onBookItemClick(books.get(getAdapterPosition()));
        }
    }
}