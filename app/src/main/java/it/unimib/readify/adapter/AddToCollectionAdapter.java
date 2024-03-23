package it.unimib.readify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.SelectCollectionItemBinding;
import it.unimib.readify.model.Collection;

public class AddToCollectionAdapter extends ListAdapter<Collection, AddToCollectionAdapter.AddToCollectionViewHolder> {
    public interface OnItemClickListener {
        void onCollectionAdd(Collection collection);
    }

    private final OnItemClickListener onItemClickListener;
    private final String bookId;
    public AddToCollectionAdapter(String bookId, OnItemClickListener onItemClickListener){
        super(new DiffUtil.ItemCallback<Collection>() {
            @Override
            public boolean areItemsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
                return oldItem.getName().equals(newItem.getName());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onItemClickListener = onItemClickListener;
        this.bookId = bookId;
    }

    @NonNull
    @Override
    public AddToCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SelectCollectionItemBinding binding = SelectCollectionItemBinding.inflate(inflater, parent, false);
        return new AddToCollectionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToCollectionViewHolder holder, int position) {
        Collection collection = getItem(position);
        holder.bind(collection);
    }

    public class AddToCollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final SelectCollectionItemBinding binding;
        public AddToCollectionViewHolder(@NonNull SelectCollectionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(Collection collection){
            if(collection != null){
                binding.textviewCollectionName.setText(collection.getName());
                String booksRead = String.valueOf(collection.getBooks().size())
                        .concat(" ")
                        .concat(this.itemView.getContext().getString(R.string.books));
                binding.textviewNumberOfBooks.setText(booksRead);
                String finalBookId = bookId.substring("/works/".length());
                for(Collection c : getCurrentList()){
                    List<String> books = c.getBooks();
                    binding.checkboxAddToCollection.setChecked(books.contains(finalBookId));
                }
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Collection collection = getItem(position);

            }
        }
    }
}
