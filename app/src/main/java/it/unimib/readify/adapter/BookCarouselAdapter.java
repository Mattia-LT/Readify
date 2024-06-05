package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookHomeItemBinding;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookCarouselAdapter extends ListAdapter<OLWorkApiResponse, BookCarouselAdapter.BookCarouselViewHolder> {

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
    }

    private final OnItemClickListener onBookItemClickListener;

    public BookCarouselAdapter(OnItemClickListener onBookItemClickListener) {
        super(new DiffUtil.ItemCallback<OLWorkApiResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull OLWorkApiResponse oldItem, @NonNull OLWorkApiResponse newItem) {
                return oldItem.getKey().equals(newItem.getKey());
            }

            @Override
            public boolean areContentsTheSame(@NonNull OLWorkApiResponse oldItem, @NonNull OLWorkApiResponse newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onBookItemClickListener = onBookItemClickListener;
    }

    @NonNull
    @Override
    public BookCarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BookHomeItemBinding binding = BookHomeItemBinding.inflate(inflater, parent, false);
        return new BookCarouselViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookCarouselViewHolder holder, int position) {
        if (position != RecyclerView.NO_POSITION) {
            OLWorkApiResponse book = getItem(position);
            holder.bind(book);
        }
    }

    public class BookCarouselViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final BookHomeItemBinding binding;

        public BookCarouselViewHolder(@NonNull BookHomeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.bookCover.setOnClickListener(this);
        }

        public void bind(OLWorkApiResponse book) {
            //Set book's title
            binding.bookTitle.setText(book.getTitle());

            //Set book's cover
            if (book.getCovers() != null) {
                int cover = -1;
                int pos = 0;
                while (cover == -1 && pos < book.getCovers().size()) {
                    cover = book.getCovers().get(pos);
                    pos++;
                }
                if (cover == -1) {
                    binding.bookCover.setImageResource(R.drawable.image_not_available);
                } else {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.loading_spinner)
                            .error(R.drawable.image_not_available);

                    Glide.with(itemView.getContext())
                            .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                            .apply(requestOptions)
                            .into(binding.bookCover);
                }
            } else {
                binding.bookCover.setImageResource(R.drawable.image_not_available);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                OLWorkApiResponse book = getItem(position);
                onBookItemClickListener.onBookItemClick(book);
            }
        }
    }
}
