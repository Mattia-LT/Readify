package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.CollectionItemBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

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

        private final ImageView thumbnail;
        private final TextView name;
        private final ImageView visibilityIcon;
        private final ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.collection_thumbnail_imageview);
            name = itemView.findViewById(R.id.collection_name_textview);
            visibilityIcon = itemView.findViewById(R.id.collection_visibility_icon);
            container = itemView.findViewById(R.id.collection_container);
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
            boolean isThumbnailAvailable = false;
            if(collection.getWorks() == null || collection.getWorks().isEmpty()) {
                thumbnail.setImageResource(R.drawable.image_not_available);
            } else {
                for(OLWorkApiResponse work : collection.getWorks()) {
                    int pos = 0;
                    int cover = -1;
                    while (cover == -1 && pos < work.getCovers().size()) {
                        cover = work.getCovers().get(pos);
                        pos++;
                    }
                    if (cover != -1) {
                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.loading_spinner)
                                .error(R.drawable.image_not_available);
                        Glide.with(application)
                                .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                                .apply(requestOptions)
                                .into(thumbnail);
                        isThumbnailAvailable = true;
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