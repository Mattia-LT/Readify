package it.unimib.readify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.unimib.readify.R;
import it.unimib.readify.model.Collection;

public class CollectionAdapter extends ArrayAdapter<Collection> {

        private final int layout;
        private final Collection[] collectionsArray;

    public CollectionAdapter(@NonNull Context context, int layout,
                             @NonNull Collection[] collectionsArray) {
        super(context, layout, collectionsArray);
        this.layout = layout;
        this.collectionsArray = collectionsArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //quando può essere null e quando no?
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(layout, parent, false);
        }
        ImageView thumbnail = convertView.findViewById(R.id.listview_collections);
        TextView name = convertView.findViewById(R.id.listview_collections);
        ImageView visibilityIcon = convertView.findViewById(R.id.listview_collections);

        return super.getView(position, convertView, parent);
    }
}
