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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookSearchItemBinding;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookSearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BookSearchItemBinding binding = BookSearchItemBinding.inflate(inflater, parent, false);
        return new SearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position != RecyclerView.NO_POSITION){
            if(holder instanceof SearchResultViewHolder){
                ((SearchResultViewHolder) holder).bind(bookList.get(position));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
                    pos++;
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
}

