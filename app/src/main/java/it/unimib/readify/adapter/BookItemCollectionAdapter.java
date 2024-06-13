package it.unimib.readify.adapter;

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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BookCollectionItemBinding;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.util.SubjectsUtil;

public class BookItemCollectionAdapter extends ListAdapter<OLWorkApiResponse, BookItemCollectionAdapter.BookItemCollectionViewHolder> {

    public interface OnItemClickListener {
        void onBookItemClick(OLWorkApiResponse book);
    }

    private final OnItemClickListener onItemClickListener;

    public BookItemCollectionAdapter(OnItemClickListener onItemClickListener) {
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
    public BookItemCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BookCollectionItemBinding binding = BookCollectionItemBinding.inflate(inflater, parent, false);
        return new BookItemCollectionAdapter.BookItemCollectionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookItemCollectionAdapter.BookItemCollectionViewHolder holder, int position) {
        OLWorkApiResponse book = getItem(position);
        holder.bind(book);
    }

    public class BookItemCollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final BookCollectionItemBinding binding;

        public BookItemCollectionViewHolder(@NonNull BookCollectionItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            binding.bookCollectionContainer.setOnClickListener(this);
        }

        public void bind(OLWorkApiResponse book) {
            loadCover(book);
            binding.textviewBookCollectionTitle.setText(book.getTitle());
            loadAuthors(book);
            loadSubjects(book);
            loadRating(book);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                OLWorkApiResponse book = getItem(position);
                onItemClickListener.onBookItemClick(book);
            }
        }



        private void loadCover(OLWorkApiResponse book){
            if(book.getCovers() != null){
                int pos = 0;
                int cover = -1;
                while (cover == -1 && pos < book.getCovers().size()) {
                    cover = book.getCovers().get(pos);
                    pos++;
                }
                if (cover == -1) {
                    binding.bookCollectionThumbnailImageview.setImageResource(R.drawable.image_not_available);
                } else {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.loading_spinner)
                            .error(R.drawable.image_not_available);

                    Glide.with(itemView.getContext())
                            .load(OL_COVERS_API_URL + OL_COVERS_API_ID_PARAMETER + cover + OL_COVERS_API_IMAGE_SIZE_L)
                            .apply(requestOptions)
                            .into(binding.bookCollectionThumbnailImageview);
                }
            } else {
                binding.bookCollectionThumbnailImageview.setImageResource(R.drawable.image_not_available);
            }
        }
        private void loadAuthors(OLWorkApiResponse book){
            StringBuilder authors = new StringBuilder();
            if(book.getAuthorList() != null){
                for(OLAuthorApiResponse author : book.getAuthorList()){
                    authors.append(author.getName()).append("\n");
                }
            } else {
                authors.append(itemView.getContext().getString(R.string.error_author_not_found));
            }
            binding.textviewBookCollectionAuthor.setText(authors.toString());
        }

        private void loadSubjects(OLWorkApiResponse book) {
            if(book.getSubjects() != null){
                binding.chipgroupBookCollectionGenres.removeAllViews();
                SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(itemView.getContext());
                String[] subjects = itemView.getResources().getStringArray(R.array.chip_genres);
                if(book.getSubjects() != null){
                    for(String subject : book.getSubjects()){
                        if(subjectsUtil.containSubject(subject)){
                            Chip chip = (Chip) LayoutInflater.from(itemView.getContext()).inflate(R.layout.single_subject_chip_layout_small, binding.chipgroupBookCollectionGenres, false);
                            int id = subjectsUtil.getChipId(subject);
                            chip.setText(subjects[id - 1]);
                            chip.setClickable(false);
                            binding.chipgroupBookCollectionGenres.addView(chip);
                        }
                    }
                }
            }
        }

        private void loadRating(OLWorkApiResponse book) {
            if(book.getRating() != null && book.getRating().getSummary().getAverage() != 0){
                float rating = (float) book.getRating().getSummary().getAverage();
                binding.ratingBarBookCollection.setRating(rating);
                String ratingString = String.format("%s", rating).substring(0,3);
                binding.ratingBarLabelBookCollection.setText(ratingString);
            } else {
                binding.ratingBarBookCollection.setRating(0);
                binding.ratingBarLabelBookCollection.setText(R.string.rating_not_available);
            }
        }

    }
}