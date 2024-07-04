package com.example.myaudiobook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaudiobook.R;
import com.example.myaudiobook.classes.AudioBook;
import com.example.myaudiobook.listener.AudioBookListener;
import com.github.ybq.android.spinkit.SpinKitView;

public class BookAdapter extends ListAdapter<AudioBook,BookAdapter.ViewHolder> {

    private final Context mContext;
    private AudioBook curItem;
    private final AudioBookListener listener;
    private int playingPosition = -1;
    private boolean isPlaying = false;

    public BookAdapter(Context mContext, AudioBookListener listener) {
        super(diffCallback);
        this.mContext = mContext;
        this.listener = listener;
    }

    /**
     * checks if contents are the same
     */
    private final static DiffUtil.ItemCallback<AudioBook> diffCallback = new DiffUtil.ItemCallback<AudioBook>() {
        @Override
        public boolean areItemsTheSame(@NonNull AudioBook oldItem, @NonNull AudioBook newItem) {
            return oldItem.getKey().equals(newItem.getKey());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AudioBook oldItem, @NonNull AudioBook newItem) {
            return oldItem.isFullyEquals(newItem);
        }

    };


    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_book_item,parent,false);
        return new BookAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        curItem = getItem(position);
        holder.tvName.setText(curItem.getName());

//        if(position == playingPosition){
//            if(isPlaying) holder.skPlaying.setVisibility(View.VISIBLE);
//            else holder.skPlaying.setVisibility(View.GONE);
//
//            holder.clHolder.setBackgroundResource(R.drawable.item_selected);
//        }
//        else{
//            holder.skPlaying.setVisibility(View.GONE);
//            holder.clHolder.setBackgroundResource(R.drawable.item_ripple_background);
//        }
        if(curItem.isUrlValid()){
            holder.ivPlay.setImageResource(R.drawable.icon_play_2);
        }
        else{
            holder.ivPlay.setImageResource(R.drawable.coming_soon_new);
        }

        holder.ivPlay.setOnClickListener(v -> {
            int sPosition = holder.getBindingAdapterPosition();
            if(!getItem(sPosition).isUrlValid()){
                listener.onMessageFound("Not yet available");
                return;
            }
            playThisPosition(sPosition);
        });
    }

    public void setPlayingPosition(int position){
        playingPosition = position;
    }

    public void playThisPosition(int position){
//        if(playingPosition == position){
//            listener.onMessageFound("Already playing this book");
//            return;
//        }

        listener.onPlayRequest(getItem(position));

        int prev = playingPosition;
        playingPosition = position;

        notifyItemChanged(prev);
        notifyItemChanged(playingPosition);
    }

    public AudioBook getBookAt(int index){
        if(index < 0 || index >= getItemCount()){
            return null;
        }
        return getItem(index);
    }
    public int getPlayingPosition(){
        return playingPosition;
    }

    public void setPlaying(boolean isPlaying){
        this.isPlaying = isPlaying;
        if(playingPosition != -1) notifyItemChanged(playingPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName;
        private final SpinKitView skPlaying;
        private final ImageView ivPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            skPlaying = itemView.findViewById(R.id.skPlaying);
            tvName = itemView.findViewById(R.id.tvBookName);
            ivPlay = itemView.findViewById(R.id.ivPlay);
        }

    }

}
