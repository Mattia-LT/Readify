package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import it.unimib.readify.R;
import it.unimib.readify.databinding.CollectionItemBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public class CollectionAdapter extends ListAdapter<Collection, CollectionAdapter.CollectionViewHolder> {
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onCollectionItemClick(Collection collection);
    }

    public CollectionAdapter(OnItemClickListener onItemClickListener) {
        super(new DiffUtil.ItemCallback<Collection>() {
            @Override
            public boolean areItemsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
                return oldItem.getCollectionId().equals(newItem.getCollectionId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CollectionItemBinding binding = CollectionItemBinding.inflate(inflater, parent, false);
        return new CollectionAdapter.CollectionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = getItem(position);
        holder.bind(collection);
    }


    public class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CollectionItemBinding binding;

        public CollectionViewHolder(@NonNull CollectionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Collection collection) {
            if(collection != null && collection.getWorks() != null){
                //Set collection name and visibility
                binding.collectionNameTextview.setText(collection.getName());
                if (collection.isVisible()) {
                    binding.collectionVisibilityIcon.setImageResource(R.drawable.baseline_visibility_24);
                } else {
                    binding.collectionVisibilityIcon.setImageResource(R.drawable.baseline_lock_outline_24);
                }

                //Set collection cover
                boolean isThumbnailAvailable = false;
                if(collection.getWorks().isEmpty() && collection.getNumberOfBooks() == 0) {
                    binding.collectionThumbnailImageview.setImageResource(R.drawable.image_not_available);
                } else if(collection.getWorks().isEmpty() && collection.getNumberOfBooks() > 0){
                    binding.collectionThumbnailImageview.setImageResource(R.drawable.loading_spinner);
                } else {
                    for(OLWorkApiResponse work : collection.getWorks()) {
                        if(work.getCovers() != null){
                            int pos = 0;
                            int cover = -1;
                            while (cover == -1 && pos < work.getCovers().size()) {
                                cover = work.getCovers().get(pos);
                                pos++;
                            }
                            if (cover != -1) {
                                Glide.with(itemView.getContext())
                                        .asBitmap()
                                        .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                                        .placeholder(R.drawable.loading_spinner)
                                        .error(R.drawable.image_not_available)
                                        .centerCrop()
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                binding.collectionThumbnailImageview.setImageBitmap(resource);
                                                Palette.from(resource).generate(palette -> {
                                                    if (palette != null) {
                                                        int bookCoverMainColor = palette.getDominantColor(Color.BLACK); // Default color
                                                        //Get brightness of the cover image and set visibility icon color accordingly
                                                        int brightness = (int) (0.2126 * Color.red(bookCoverMainColor) + 0.7152 * Color.green(bookCoverMainColor) + 0.0722 * Color.blue(bookCoverMainColor));
                                                        if (brightness < 128) {
                                                            //Background is dark, use a white icon
                                                            binding.collectionVisibilityIcon.setColorFilter(Color.WHITE);
                                                        } else {
                                                            //Background is light, use a black icon
                                                            binding.collectionVisibilityIcon.setColorFilter(Color.BLACK);
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                                // Not needed
                                            }
                                        });
                                isThumbnailAvailable = true;
                                break;
                            }
                        }
                    }
                    if(!isThumbnailAvailable){
                        binding.collectionThumbnailImageview.setImageResource(R.drawable.image_not_available);
                    }
                }

                //Set layout margins
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) binding.collectionContainer.getLayoutParams();
                float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, Resources.getSystem().getDisplayMetrics());
                if (getBindingAdapterPosition() % 2 == 0) {
                    layoutParams.rightMargin = (int) margin;
                } else {
                    layoutParams.leftMargin = (int) margin;
                }
            }
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Collection collection = getItem(position);
                onItemClickListener.onCollectionItemClick(collection);
            }
        }
    }
}
