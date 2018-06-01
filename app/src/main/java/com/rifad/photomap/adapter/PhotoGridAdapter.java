package com.rifad.photomap.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.rifad.photomap.R;
import com.rifad.photomap.listener.OnItemClickListener;
import com.rifad.photomap.model.Photo;

import java.util.ArrayList;


public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.ViewHolder> {

    private RequestManager glide;
    private ArrayList<Photo> photos;
    private OnItemClickListener mListener;

    public PhotoGridAdapter(RequestManager glide, ArrayList<Photo> photos) {
        this.glide = glide;
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoGridAdapter.ViewHolder holder, int position) {
        glide.load(photos.get(position).getPath())
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return photos.isEmpty() ? 0 : photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPhoto = itemView.findViewById(R.id.ivPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
