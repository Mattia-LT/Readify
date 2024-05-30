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
import it.unimib.readify.model.FollowUser;

public class FollowListAdapter extends ListAdapter<FollowUser, FollowListAdapter.FollowViewHolder> {

    private List<FollowUser> currentFollowing;
    private String loggedUserIdToken;

    public interface OnItemClickListener {
        void onProfileClick(FollowUser user);
        void onFollowButtonClick(FollowUser user);
        void onUnfollowButtonClick(FollowUser user);
    }

    private final OnItemClickListener onItemClickListener;

    public FollowListAdapter(OnItemClickListener onItemClickListener) {
        super(new DiffUtil.ItemCallback<FollowUser>() {
            @Override
            public boolean areItemsTheSame(@NonNull FollowUser oldItem, @NonNull FollowUser newItem) {
                return oldItem.getIdToken().equals(newItem.getIdToken());
            }

            @Override
            public boolean areContentsTheSame(@NonNull FollowUser oldItem, @NonNull FollowUser newItem) {
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
        FollowUser user = getItem(position);
        holder.bind(user);
    }

    @Override
    public void submitList(@Nullable List<FollowUser> list) {
        if (list != null) {
            list.sort(Comparator.comparing(FollowUser::getTimestamp));
        }
        super.submitList(list);
    }

    public void submitFollowings(List<FollowUser> currentFollowing, String loggedUserIdToken){
        if(currentFollowing != null){
            this.currentFollowing = currentFollowing;
            this.loggedUserIdToken = loggedUserIdToken;
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

        public void bind(FollowUser followUser) {
            if (followUser != null) {
                binding.followUsername.setText(followUser.getUser().getUsername());
                int avatarId;
                try {
                    avatarId = R.drawable.class.getDeclaredField(followUser.getUser().getAvatar().toLowerCase()).getInt(null);
                } catch (Exception e) {
                    avatarId = R.drawable.ic_baseline_profile_24;
                }
                Glide.with(this.itemView.getContext())
                        .load(avatarId)
                        .dontAnimate()
                        .into(binding.followImage);
                if(followUser.getIdToken().equalsIgnoreCase(loggedUserIdToken)){
                    //don't show follow button
                    binding.unfollowButton.setVisibility(View.GONE);
                    binding.followButton.setVisibility(View.GONE);
                } else if(isFollowed(followUser)){
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
                FollowUser user = getItem(position);
                if(v.getId() == binding.followUsername.getId() || v.getId() == binding.followImage.getId()){
                    onItemClickListener.onProfileClick(user);
                } else if(v.getId() == binding.followButton.getId()){
                    onItemClickListener.onFollowButtonClick(user);
                } else if(v.getId() == binding.unfollowButton.getId()){
                    onItemClickListener.onUnfollowButtonClick(user);
                }
            }
        }

        private boolean isFollowed(FollowUser followUser){
            if(currentFollowing != null){
                for(FollowUser user : currentFollowing){
                    if(user.getIdToken().equals(followUser.getIdToken())){
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }
}
