package it.unimib.readify.adapter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.unimib.readify.R;
import it.unimib.readify.model.Collection;

public class CollectionAdapter extends
        RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    /*
    si è optato per una interfaccia perchè 1) ci potranno essere più eventi diversi da gestire
    e 2) così l'adapter rimane generico
     */
    public interface OnItemClickListener {
        void onCollectionItemClick(Collection collection);
    }

    private final ArrayList<Collection> collectionsArray;
    private final OnItemClickListener onItemClickListener;

    public CollectionAdapter(ArrayList<Collection> collectionsArray, OnItemClickListener onItemClickListener) {
        this.collectionsArray = collectionsArray;
        this.onItemClickListener = onItemClickListener;
    }

    //passaggio del layout
    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.collection_item, parent, false);
        return new CollectionViewHolder(view);
    }

    //associazione tra i dati effettivi ed il layout
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
            //set dinamico di prova
            thumbnail.setBackgroundColor(itemView.getResources().getColor(R.color.orange));
            name.setText("fantasy");
            visibilityIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.baseline_visibility_24));

            //gestisce il margine del layout
            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) container.getLayoutParams();
                //converte 5dp in px a seconda del dispositivo dell'utente
            float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, Resources.getSystem().getDisplayMetrics());
            if(position % 2 == 0)
                layoutParams.rightMargin = (int) margin;
            else
                layoutParams.leftMargin = (int) margin;


            //set dinamico dei dati
            /*
            if(collection.getBooks().size() == 0)
                thumbnail.setImageDrawable(itemView.getResources().getDrawable(R.drawable.baseline_add_24));
            else {
                Drawable firstBookThumbnail = collection.getBooks().get(0).getThumbnail();
                thumbnail.setImageDrawable(firstBookThumbnail);
            }
            name.setText(collection.getName());
            if(collection.isVisible())
                visibilityIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.baseline_visibility_24));
            else
                visibilityIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.baseline_lock_outline_24));
             */
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onCollectionItemClick(collectionsArray.get(getAdapterPosition()));
        }
    }
}
