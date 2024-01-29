package it.unimib.readify.adapter;

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

import it.unimib.readify.R;
import it.unimib.readify.model.Collection;

public class CollectionAdapter extends
        RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    /*
    using an interface because
        1) there may be more different events to manage
        2) the adapter remains generic
     */
    public interface OnItemClickListener {
        void onCollectionItemClick(Collection collection);
    }

    private final ArrayList<Collection> collectionsArray;
    private final OnItemClickListener onItemClickListener;
    private final Application application;

    public CollectionAdapter(ArrayList<Collection> collectionsArray, OnItemClickListener onItemClickListener, Application application) {
        this.collectionsArray = collectionsArray;
        this.onItemClickListener = onItemClickListener;
        this.application = application;
    }

    //managing layout
    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.collection_item, parent, false);
        return new CollectionViewHolder(view);
    }

    //association between the actual data and the layout
    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        holder.bind(collectionsArray.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(collectionsArray == null)
            return 0;
        return collectionsArray.size();
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView thumbnail;
        private TextView name;
        private ImageView visibilityIcon;
        private ConstraintLayout container;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.collection_thumbnail_imageview);
            name = itemView.findViewById(R.id.collection_name_textview);
            visibilityIcon = itemView.findViewById(R.id.collection_visibility_icon);
            container = itemView.findViewById(R.id.collection_container);
            itemView.setOnClickListener(this);
        }

        public void bind(Collection collection, int position) {
            //set collection thumbnail
            //todo verify correct behavior with multiple books in a collection
            if(collection.getBooks() == null || collection.getBooks().size() == 0)
                thumbnail.setImageResource(R.drawable.image_not_available);
            else {
                boolean isThumbnailAvailable = false;
                for (int i = 0; i < collection.getBooks().size(); i++) {
                        if(collection.getBook(i) != null && collection.getBook(i).getCovers() != null)
                            for (int j = 0; j < collection.getBook(i).getCovers().size(); j++) {
                                if(collection.getBook(i).getCovers().get(j) != -1) {
                                    isThumbnailAvailable = true;
                                    Log.d("url", "https://covers.openlibrary.org/b/id/"
                                            + collection.getBook(i).getCovers().get(j)
                                            + "-L.jpg");
                                    RequestOptions requestOptions = new RequestOptions()
                                            .placeholder(R.drawable.loading_image_gif)
                                            .error(R.drawable.image_not_available);
                                    Glide.with(application)
                                            .load("https://covers.openlibrary.org/b/id/"
                                                    + collection.getBook(i).getCovers().get(j)
                                                    + "-L.jpg")
                                            .apply(requestOptions)
                                            .into(thumbnail);
                                    break;
                                }
                            }
                }
                if(!isThumbnailAvailable)
                    thumbnail.setImageResource(R.drawable.image_not_available);
            }
            //set collection name and visibility
            name.setText(collection.getName());
            if(collection.isVisible())
                visibilityIcon.setImageResource(R.drawable.baseline_visibility_24);
            else
                visibilityIcon.setImageResource(R.drawable.baseline_lock_outline_24);

            //managing layout margin
            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) container.getLayoutParams();
                //convert 5dp in px depending on the user's device
            float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, Resources.getSystem().getDisplayMetrics());
            if(position % 2 == 0)
                layoutParams.rightMargin = (int) margin;
            else
                layoutParams.leftMargin = (int) margin;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onCollectionItemClick(collectionsArray.get(getAdapterPosition()));
        }
    }
}
