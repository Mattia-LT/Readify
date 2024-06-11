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

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unimib.readify.R;
import it.unimib.readify.databinding.NotificationItemBinding;
import it.unimib.readify.model.FollowUser;
import it.unimib.readify.model.Notification;

public class NotificationsAdapter extends ListAdapter<Notification, NotificationsAdapter.NotificationsViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private List<FollowUser> currentFollowing;

    public interface OnItemClickListener {
        void onNotificationItemClick(Notification notification);
        void onFollowUser(String externalUserIdToken);
        void onUnfollowUser(String externalUserIdToken);
    }

    public NotificationsAdapter(OnItemClickListener onItemClickListener) {

        super(new DiffUtil.ItemCallback<Notification>() {
            @Override
            public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
                return oldItem.getTimestamp() == newItem.getTimestamp();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NotificationItemBinding binding = NotificationItemBinding.inflate(inflater, parent, false);
        return new NotificationsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notification notification = getItem(position);
        holder.bind(notification);
    }

    @Override
    public void submitList(@Nullable List<Notification> list) {
        if (list != null) {
            list.sort(Comparator.comparing(Notification::getTimestamp).reversed());
        }
        super.submitList(list);
    }

    public void submitFollowings(List<FollowUser> currentFollowing){
        if(currentFollowing != null){
            this.currentFollowing = currentFollowing;
        }
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final NotificationItemBinding binding;

        public NotificationsViewHolder(@NonNull NotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.followButton.setOnClickListener(this);
            binding.unfollowButton.setOnClickListener(this);
            binding.notificationTextContainer.setOnClickListener(this);
            binding.notificationIcon.setOnClickListener(this);
        }

        public void bind(Notification notification) {
            if(notification != null) {
                //Set username
                binding.notificationTitle.setText(notification.getUser().getUsername());

                //Set avatar icon
                int avatarId;
                try {
                    avatarId = R.drawable.class.getDeclaredField(notification.getUser().getAvatar().toLowerCase()).getInt(null);
                } catch (Exception e) {
                    avatarId = R.drawable.ic_baseline_profile_24;
                }

                Glide.with(itemView.getContext())
                        .load(avatarId)
                        .dontAnimate()
                        .into(binding.notificationIcon);

                //Set date
                Locale locale = Locale.getDefault();
                if (locale.getLanguage().startsWith("it")) {
                    locale = new Locale("it", "IT");
                }
                Date timestamp = new Date(notification.getTimestamp());
                String pattern = "dd/MM/yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
                String formattedDate = dateFormat.format(timestamp);
                binding.notificationDate.setText(formattedDate);

                if(isFollowed(notification.getIdToken())) {
                    binding.unfollowButton.setVisibility(View.VISIBLE);
                    binding.followButton.setVisibility(View.GONE);
                } else {
                    binding.followButton.setVisibility(View.VISIBLE);
                    binding.unfollowButton.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Notification notification = getItem(position);
                if(v.getId() == binding.notificationIcon.getId()
                        || v.getId() == binding.notificationTextContainer.getId()){
                    onItemClickListener.onNotificationItemClick(notification);
                } else if(v.getId() == binding.followButton.getId()){
                    onItemClickListener.onFollowUser(notification.getIdToken());
                } else if(v.getId() == binding.unfollowButton.getId()){
                    onItemClickListener.onUnfollowUser(notification.getIdToken());
                }
            }
        }

        private boolean isFollowed(String followerUserIdToken){
            if(currentFollowing != null){
                for(FollowUser user : currentFollowing){
                    if(user.getIdToken().equals(followerUserIdToken)){
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }
}