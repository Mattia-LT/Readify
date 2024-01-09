package it.unimib.readify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookSearchResultAdapter extends RecyclerView.Adapter<BookSearchResultAdapter.SearchResultViewHolder> {

    private Context context;
    private List<OLWorkApiResponse> bookList;

    public BookSearchResultAdapter(Context context, List<OLWorkApiResponse> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_search_item, parent, false);
        return new SearchResultViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        OLWorkApiResponse book = bookList.get(position);

        // Set the data to views
        holder.titleTextView.setText(book.getTitle());
        holder.descriptionTextView.setText(book.getDescription().getValue().toString());
        // TODO gestire immagine con glide
        // Glide.with(context).load(book.getImageUrl()).into(holder.coverImageView);

        // TODO gestire il bottone dei preferiti
        holder.addButton.setOnClickListener(v -> {
            // Handle the button click, e.g., add to bookshelf
            // You may want to pass a callback to the adapter to handle this in your fragment/activity
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }


    static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        ImageButton addButton;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.imageview_book_cover);
            titleTextView = itemView.findViewById(R.id.textview_book_title);
            descriptionTextView = itemView.findViewById(R.id.textview_book_description);
            addButton = itemView.findViewById(R.id.imagebutton_add_icon);
        }
    }






}
