package com.example.myaudiobook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myaudiobook.R;
import com.example.myaudiobook.classes.PlayList;
import com.example.myaudiobook.listener.PlaylistListener;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends ListAdapter<PlayList, PlayListAdapter.ViewHolder> {

    private final Context mContext;
    private PlayList curItem;
    private final PlaylistListener listener;
    int curColor = 0;

    public PlayListAdapter(Context mContext, PlaylistListener listener) {
        super(diffCallback);
        this.mContext = mContext;
        this.listener = listener;
    }

    /**
     * checks if contents are the same
     */
    private final static DiffUtil.ItemCallback<PlayList> diffCallback = new DiffUtil.ItemCallback<PlayList>() {
        @Override
        public boolean areItemsTheSame(@NonNull PlayList oldItem, @NonNull PlayList newItem) {
            return oldItem.getKey().equals(newItem.getKey());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PlayList oldItem, @NonNull PlayList newItem) {
            return oldItem.isFullyEquals(newItem);
        }

    };


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        curItem = getItem(position);

        holder.tvName.setText(curItem.getName());

        //holder.viewHorizontal.setBackgroundResource(getBackgroundColor());

        Glide.with(mContext)
                .load(curItem.getImageUrl())
                .placeholder(R.drawable.mujib_default_icon)
                .into(holder.ivThumbnail)
                .onLoadFailed(ResourcesCompat.getDrawable(mContext.getResources(),R.drawable.mujib_default_icon,null));


        holder.itemView.setOnClickListener(v -> listener.onItemClick(getItem(holder.getBindingAdapterPosition())));

        //holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(getBackgroundColor(),null));
    }

    /**
     * adds background color
     * @return color
     */
    private int getBackgroundColor() {
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.light_green);
        colorCode.add(R.color.pale_yellow);
        colorCode.add(R.color.light_magenta);
        colorCode.add(R.color.white);

        curColor = (curColor + 1) % colorCode.size();
        return colorCode.get(curColor);
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final ImageView ivThumbnail;
        private final View viewHorizontal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            viewHorizontal = itemView.findViewById(R.id.viewHorizontal);
        }

    }

}
