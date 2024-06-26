package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.DESCRIPTION_TRIM_OPTIONS;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookSearchItemBinding;
import it.unimib.readify.model.OLWorkApiResponse;

public class BookSearchResultAdapter extends ListAdapter<OLWorkApiResponse, BookSearchResultAdapter.SearchResultViewHolder> {

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
        void onAddToCollectionButtonPressed(OLWorkApiResponse book);
    }

    private final OnItemClickListener onItemClickListener;


    public BookSearchResultAdapter(OnItemClickListener onItemClickListener) {
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
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BookSearchItemBinding binding = BookSearchItemBinding.inflate(inflater, parent, false);
        return new SearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        if(position != RecyclerView.NO_POSITION){
            OLWorkApiResponse book = getItem(position);
            holder.bind(book);
        }
    }

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
            String description = book.getDescription().getValue();
            for(String option : DESCRIPTION_TRIM_OPTIONS){
                if(description.contains(option)){
                    int position = description.indexOf(option);
                    description = description.substring(0, position);
                }
            }
            description = description.trim();
            if(description.isEmpty()){
                description = itemView.getContext().getString(R.string.description_not_available);
            }
            binding.textviewBookDescription.setText(description);
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
                            .placeholder(R.drawable.loading_spinner)
                            .error(R.drawable.image_not_available)
                            .transform(new CenterCrop());

                    Glide.with(itemView.getContext())
                            .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                            .apply(requestOptions)
                            .into(binding.imageviewBookCover);
                }
            } else {
                binding.imageviewBookCover.setImageResource(R.drawable.image_not_available);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                OLWorkApiResponse book = getItem(position);
                if (v.getId() == R.id.imagebutton_add_icon) {
                    onItemClickListener.onAddToCollectionButtonPressed(book);
                } else {
                    onItemClickListener.onBookItemClick(book);
                }
            }
        }
    }
}

