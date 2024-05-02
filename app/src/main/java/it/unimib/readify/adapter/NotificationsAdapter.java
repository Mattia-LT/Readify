package it.unimib.readify.adapter;

import android.view.LayoutInflater;
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

    public NotificationsAdapter() {
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

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {

        private final NotificationItemBinding binding;

        public NotificationsViewHolder(@NonNull NotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
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
        }
    }
}