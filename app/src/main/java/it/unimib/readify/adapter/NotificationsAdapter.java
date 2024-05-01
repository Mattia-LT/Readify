package it.unimib.readify.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import it.unimib.readify.databinding.BookCollectionItemBinding;
import it.unimib.readify.databinding.NotificationItemBinding;
import it.unimib.readify.model.Collection;
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
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
                return false;
            }
        });
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

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {

        private final NotificationItemBinding binding;

        public NotificationsViewHolder(@NonNull NotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notification notification) {
            binding.notificationDate.setText(String.format("%tD", notification.getTimestamp()));
        }
    }
}