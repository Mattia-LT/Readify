package it.unimib.readify.adapter;

import android.view.LayoutInflater;
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
    public interface OnCheckboxStatusChanged {
        void onCollectionSelected(Collection collection);
        void onCollectionUnselected(Collection collection);
    }

    private final OnCheckboxStatusChanged onCheckboxStatusChanged;
    private final String bookId;
    public AddToCollectionAdapter(String bookId, OnCheckboxStatusChanged onCheckboxStatusChanged){
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
        this.onCheckboxStatusChanged = onCheckboxStatusChanged;
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

    public class AddToCollectionViewHolder extends RecyclerView.ViewHolder{

        private final SelectCollectionItemBinding binding;

        public AddToCollectionViewHolder(@NonNull SelectCollectionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.checkboxAddToCollection.addOnCheckedStateChangedListener((checkBox, state) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Collection selectedCollection = getItem(position);
                    if(checkBox.isChecked()){
                        onCheckboxStatusChanged.onCollectionSelected(selectedCollection);
                    } else{
                        onCheckboxStatusChanged.onCollectionUnselected(selectedCollection);
                    }
                }
            });
        }

        public void bind(Collection collection) {
            if (collection != null) {
                binding.textviewCollectionName.setText(collection.getName());
                String booksRead = this.itemView.getResources().getQuantityString(R.plurals.books, collection.getNumberOfBooks(), collection.getNumberOfBooks());
                binding.textviewNumberOfBooks.setText(booksRead);
                List<String> books = collection.getBooks();
                if (books != null) {
                    binding.checkboxAddToCollection.setChecked(books.contains(bookId));
                }
            }
        }

    }
}
