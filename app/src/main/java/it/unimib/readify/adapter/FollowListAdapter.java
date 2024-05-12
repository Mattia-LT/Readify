package it.unimib.readify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Comparator;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FollowItemBinding;
import it.unimib.readify.model.ExternalUser;

public class FollowListAdapter extends ListAdapter<ExternalUser, FollowListAdapter.FollowViewHolder> {

    private List<ExternalUser> currentFollowing;

    public interface OnItemClickListener {
        void onProfileClick(ExternalUser user);
        void onFollowButtonClick(ExternalUser user);
        void onUnfollowButtonClick(ExternalUser user);
    }

    private final OnItemClickListener onItemClickListener;

    public FollowListAdapter(OnItemClickListener onItemClickListener) {
        super(new DiffUtil.ItemCallback<ExternalUser>() {
            @Override
            public boolean areItemsTheSame(@NonNull ExternalUser oldItem, @NonNull ExternalUser newItem) {
                return oldItem.getIdToken().equals(newItem.getIdToken());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ExternalUser oldItem, @NonNull ExternalUser newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FollowItemBinding binding = FollowItemBinding.inflate(inflater, parent, false);
        return new FollowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        ExternalUser user = getItem(position);
        holder.bind(user);
    }

    @Override
    public void submitList(@Nullable List<ExternalUser> list) {
        if (list != null) {
            list.sort(Comparator.comparing(ExternalUser::getTimestamp));
        }
        super.submitList(list);
    }

    public void submitFollowings(List<ExternalUser> currentFollowing){
        if(currentFollowing != null){
            this.currentFollowing = currentFollowing;
        }
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final FollowItemBinding binding;

        public FollowViewHolder(@NonNull FollowItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            binding.followImage.setOnClickListener(this);
            binding.followUsername.setOnClickListener(this);
            binding.followButton.setOnClickListener(this);
            binding.unfollowButton.setOnClickListener(this);
        }

        public void bind(ExternalUser externalUser) {
            if (externalUser != null) {
                binding.followUsername.setText(externalUser.getUser().getUsername());
                int avatarId;
                try {
                    avatarId = R.drawable.class.getDeclaredField(externalUser.getUser().getAvatar().toLowerCase()).getInt(null);
                } catch (Exception e) {
                    avatarId = R.drawable.ic_baseline_profile_24;
                }
                Glide.with(this.itemView.getContext())
                        .load(avatarId)
                        .dontAnimate()
                        .into(binding.followImage);

                if(isFollowed(externalUser)){
                    //load unfollow button
                    binding.unfollowButton.setVisibility(View.VISIBLE);
                    binding.followButton.setVisibility(View.GONE);
                } else {
                    //load follow button
                    binding.followButton.setVisibility(View.VISIBLE);
                    binding.unfollowButton.setVisibility(View.GONE);
                }
            }


        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ExternalUser user = getItem(position);
                if(v.getId() == binding.followUsername.getId() || v.getId() == binding.followImage.getId()){
                    onItemClickListener.onProfileClick(user);
                } else if(v.getId() == binding.followButton.getId()){
                    onItemClickListener.onFollowButtonClick(user);
                } else if(v.getId() == binding.unfollowButton.getId()){
                    onItemClickListener.onUnfollowButtonClick(user);
                }
            }
        }

        private boolean isFollowed(ExternalUser externalUser){
            if(currentFollowing != null){
                for(ExternalUser user : currentFollowing){
                    if(user.getIdToken().equals(externalUser.getIdToken())){
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }
}
