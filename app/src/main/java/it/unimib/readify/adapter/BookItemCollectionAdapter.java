package it.unimib.readify.adapter;

import android.app.Application;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.unimib.readify.model.OLWorkApiResponse;

public class BookItemCollectionAdapter extends
        RecyclerView.Adapter<BookItemCollectionAdapter.ViewHolder>{

    private final OnItemClickListener onItemClickListener;
    private final Application application;

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
    }

    public BookItemCollectionAdapter(OnItemClickListener onItemClickListener, Application application) {
        this.onItemClickListener = onItemClickListener;
        this.application = application;
    }

    @NonNull
    @Override
    public BookItemCollectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BookItemCollectionAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
