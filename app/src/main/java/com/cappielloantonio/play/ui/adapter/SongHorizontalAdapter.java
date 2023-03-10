package com.cappielloantonio.play.ui.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cappielloantonio.play.R;
import com.cappielloantonio.play.databinding.ItemHorizontalTrackBinding;
import com.cappielloantonio.play.glide.CustomGlideRequest;
import com.cappielloantonio.play.interfaces.ClickCallback;
import com.cappielloantonio.play.subsonic.models.Child;
import com.cappielloantonio.play.util.Constants;
import com.cappielloantonio.play.util.DownloadUtil;
import com.cappielloantonio.play.util.MappingUtil;
import com.cappielloantonio.play.util.MusicUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UnstableApi
public class SongHorizontalAdapter extends RecyclerView.Adapter<SongHorizontalAdapter.ViewHolder> {
    private final ClickCallback click;
    private final boolean isCoverVisible;

    private List<Child> songs;

    public SongHorizontalAdapter(ClickCallback click, boolean isCoverVisible) {
        this.click = click;
        this.isCoverVisible = isCoverVisible;
        this.songs = Collections.emptyList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHorizontalTrackBinding view = ItemHorizontalTrackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Child song = songs.get(position);

        holder.item.searchResultSongTitleTextView.setText(MusicUtil.getReadableString(song.getTitle()));
        holder.item.searchResultSongSubtitleTextView.setText(holder.itemView.getContext().getString(R.string.song_subtitle_formatter, MusicUtil.getReadableString(song.getArtist()), MusicUtil.getReadableDurationString(song.getDuration(), false)));
        holder.item.trackNumberTextView.setText(String.valueOf(song.getTrack()));

        if (DownloadUtil.getDownloadTracker(holder.itemView.getContext()).isDownloaded(MappingUtil.mapMediaItem(song, false))) {
            holder.item.searchResultDowanloadIndicatorImageView.setVisibility(View.VISIBLE);
        } else {
            holder.item.searchResultDowanloadIndicatorImageView.setVisibility(View.GONE);
        }

        if (isCoverVisible) CustomGlideRequest.Builder
                .from(holder.itemView.getContext(), song.getCoverArtId(), CustomGlideRequest.SONG_PIC, null)
                .build()
                .transform(new CenterCrop(), new RoundedCorners(CustomGlideRequest.CORNER_RADIUS))
                .into(holder.item.songCoverImageView);

        if (isCoverVisible) holder.item.trackNumberTextView.setVisibility(View.INVISIBLE);

        if (!isCoverVisible) holder.item.songCoverImageView.setVisibility(View.INVISIBLE);

        if (!isCoverVisible && (position > 0 && songs.get(position - 1) != null && songs.get(position - 1).getDiscNumber() < songs.get(position).getDiscNumber())) {
            holder.item.differentDiskDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setItems(List<Child> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public Child getItem(int id) {
        return songs.get(id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHorizontalTrackBinding item;

        ViewHolder(ItemHorizontalTrackBinding item) {
            super(item.getRoot());

            this.item = item;

            item.searchResultSongTitleTextView.setSelected(true);
            item.searchResultSongSubtitleTextView.setSelected(true);

            itemView.setOnClickListener(v -> onClick());
            itemView.setOnLongClickListener(v -> onLongClick());

            item.searchResultSongMoreButton.setOnClickListener(v -> onLongClick());
        }

        public void onClick() {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.TRACKS_OBJECT, new ArrayList<>(songs));
            bundle.putInt(Constants.ITEM_POSITION, getBindingAdapterPosition());

            click.onMediaClick(bundle);
        }

        private boolean onLongClick() {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.TRACK_OBJECT, songs.get(getBindingAdapterPosition()));

            click.onMediaLongClick(bundle);

            return false;
        }
    }
}