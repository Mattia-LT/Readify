package it.unimib.readify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import it.unimib.readify.databinding.ProfileImageItemBinding;

public class ProfileImageSelectorAdapter extends ListAdapter<Integer, ProfileImageSelectorAdapter.ProfileImageSelectorViewHolder> {

    public interface OnImageClickListener {
        void onImageClick(int resourceId);
    }

    private final OnImageClickListener onImageClickListener;

    public ProfileImageSelectorAdapter(OnImageClickListener onImageClickListener) {
        super(new DiffUtil.ItemCallback<Integer>() {
            @Override
            public boolean areItemsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
                return oldItem.intValue() == newItem.intValue();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
                return oldItem.intValue() == newItem.intValue();
            }
        });
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public ProfileImageSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ProfileImageItemBinding binding = ProfileImageItemBinding.inflate(inflater, parent, false);
        return new ProfileImageSelectorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileImageSelectorViewHolder holder, int position) {
        Integer resourceId = getItem(position);
        holder.bind(resourceId);
    }

    public class ProfileImageSelectorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ProfileImageItemBinding binding;

        public ProfileImageSelectorViewHolder(@NonNull ProfileImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.profileImgItem.setOnClickListener(this);
        }

        public void bind(int resourceId){
            binding.profileImgItem.setImageResource(resourceId);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                int resourceId = getItem(position);
                if(v.getId() == binding.profileImgItem.getId()){
                    onImageClickListener.onImageClick(resourceId);
                }
            }
        }
    }

}
