package it.unimib.readify.adapter;

import android.app.Application;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.CollectionItemBinding;
import it.unimib.readify.model.Collection;

public class CollectionAdapter extends
        RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<Collection> collectionsList;
    private final OnItemClickListener onItemClickListener;
    private final Application application;
    private CollectionItemBinding collectionItemBinding;

    /*
        Using an interface because
            1) there may be more different events to manage
            2) the adapter remains generic
     */
    public interface OnItemClickListener {
        void onCollectionItemClick(Collection collection);
    }

    public CollectionAdapter(OnItemClickListener onItemClickListener, Application application) {
        this.onItemClickListener = onItemClickListener;
        this.application = application;
        collectionsList = new ArrayList<>();
    }

    public void setCollectionsList(List<Collection> collectionsList) {
        this.collectionsList = collectionsList;
        notifyItemRangeChanged(0, this.collectionsList.size());
    }

    //Managing layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        collectionItemBinding = CollectionItemBinding.inflate(LayoutInflater.from(application.getApplicationContext()));
        return new ViewHolder(collectionItemBinding.getRoot());
    }

    //Association between actual data and layout
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(collectionsList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(collectionsList == null)
            return 0;
        return collectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Collection collection, int position) {
            //Set collection thumbnail
            /*
                todo sometimes behavior is incorrect
                 - works contained in wrong collection
                 - thumbnail does not correspond to the first cover of the first work
                todo insert same size for each thumbnail (maybe?)
            */
            if(collection.getWorks() == null || collection.getWorks().isEmpty())
                collectionItemBinding.collectionThumbnailImageview
                        .setImageResource(R.drawable.image_not_available);
            else {
                boolean isThumbnailAvailable = false;
                for (int i = 0; i < collection.getWorks().size(); i++) {
                    if(collection.getWorks().get(i) != null && collection.getWorks().get(i).getCovers() != null) {
                        for (int j = 0; j < collection.getWorks().get(i).getCovers().size(); j++) {
                            if(collection.getWorks().get(i).getCovers().get(j) != -1) {
                                isThumbnailAvailable = true;
                                RequestOptions requestOptions = new RequestOptions()
                                        .placeholder(R.drawable.loading_image_gif)
                                        .error(R.drawable.image_not_available);
                                Glide.with(application)
                                        .load("https://covers.openlibrary.org/b/id/"
                                                + collection.getWorks().get(i).getCovers().get(j)
                                                + "-L.jpg")
                                        .apply(requestOptions)
                                        .into(collectionItemBinding.collectionThumbnailImageview);
                                break;
                            }
                        }
                    }
                    if(isThumbnailAvailable) {
                        break;
                    }
                }
                if(!isThumbnailAvailable)
                    collectionItemBinding.collectionThumbnailImageview
                            .setImageResource(R.drawable.image_not_available);
            }

            //Set collection name and visibility
            collectionItemBinding.collectionNameTextview.setText(collection.getName());
            if(collection.isVisible()) {
                collectionItemBinding.collectionVisibilityIcon
                        .setImageResource(R.drawable.baseline_visibility_24);
            } else {
                collectionItemBinding.collectionVisibilityIcon
                        .setImageResource(R.drawable.baseline_lock_outline_24);
            }

            //Managing layout margin
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                    (ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            //Convert 5dp in px depending on the user's device
            float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, Resources.getSystem().getDisplayMetrics());
            if(position % 2 == 0)
                layoutParams.rightMargin = (int) margin;
            else
                layoutParams.leftMargin = (int) margin;
            collectionItemBinding.collectionContainer.setLayoutParams(layoutParams);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onCollectionItemClick(collectionsList.get(getAdapterPosition()));
        }
    }
}