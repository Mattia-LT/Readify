package it.unimib.readify.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.unimib.readify.R;

public class ProfileImageSelectorAdapter extends RecyclerView.Adapter<ProfileImageSelectorAdapter.MyViewHolder> {
    int[] arr;
    private OnImageClickListener onImageClickListener;
    private final Application application;

    public ProfileImageSelectorAdapter(int[] arr, OnImageClickListener onImageClickListener, Application application) {
        this.arr = arr;
        this.onImageClickListener = onImageClickListener;
        this.application = application;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.imageView.setImageResource(arr[position]);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (onImageClickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    onImageClickListener.onImageClick(clickedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_img_item);
        }
    }

    // Interfaccia per gestire gli eventi di clic sulle immagini
    public interface OnImageClickListener {
        void onImageClick(int position);
    }
}
