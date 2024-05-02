package it.unimib.readify.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unimib.readify.R;
import it.unimib.readify.databinding.NotificationItemBinding;
import it.unimib.readify.model.Notification;

public class NotificationsAdapter extends ListAdapter<Notification, NotificationsAdapter.NotificationsViewHolder> {

    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onNotificationItemClick(Notification notification);
        void onFollowUser(String externalUserIdToken);
        void onUnfollowUser(String externalUserIdToken);
    }

    public NotificationsAdapter(OnItemClickListener onItemClickListener) {
        /*
            makes sense to use DiffUtil.ItemCallback because, everytime the user opens NotificationPageFragment,
             he is going to modify interested notification instances; it will always return false,
             except when there isn't any new notification
         */

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
        //todo switch for layout in case of implementation of different types of notifications (from newFollowers)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NotificationItemBinding binding = NotificationItemBinding.inflate(inflater, parent, false);
        return new NotificationsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notification notification = getItem(position);
        holder.bind(notification);
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final NotificationItemBinding binding;

        public NotificationsViewHolder(@NonNull NotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Notification notification) {
            //username
            binding.notificationTitle.setText(notification.getUsername());
            //icon
            int avatarId;
            try {
                avatarId = R.drawable.class.getDeclaredField(notification.getAvatar().toLowerCase()).getInt(null);
            } catch (Exception e) {
                avatarId = R.drawable.ic_baseline_profile_24;
            }
            Glide.with(this.itemView.getContext())
                    .load(avatarId)
                    .dontAnimate()
                    .into(binding.notificationIcon);
            //date
            Locale locale = Locale.getDefault();
            if (locale.getLanguage().startsWith("it")) {
                locale = new Locale("it", "IT");
            }
            Date timestamp = new Date(notification.getTimestamp());
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
            String formattedDate = dateFormat.format(timestamp);
            binding.notificationDate.setText(formattedDate);
            //button
            /*
                todo right now, each time the user presses on the button, it refreshes the page,
                 closing the "show all section" (in case it was open)
             */
            if(notification.isFollowedByUser()) {
                binding.notificationButton.setText("Followed");
                binding.notificationButton.setOnClickListener(v -> {
                    onItemClickListener.onUnfollowUser(notification.getIdToken());
                    binding.notificationButton.setOnClickListener(null);
                });
            } else {
                binding.notificationButton.setText("Follow");
                binding.notificationButton.setOnClickListener(v -> {
                    onItemClickListener.onFollowUser(notification.getIdToken());
                    binding.notificationButton.setOnClickListener(null);
                });
            }
        }

        @Override
        public void onClick(View v) {
            Log.d("notification page", "aww");
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Notification notification = getItem(position);
                Log.d("notification page", notification.toString());
                onItemClickListener.onNotificationItemClick(notification);
            }
        }
    }
}