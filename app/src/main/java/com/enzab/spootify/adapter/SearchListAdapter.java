package com.enzab.spootify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.enzab.spootify.R;
import com.enzab.spootify.model.SearchItem;
import com.enzab.spootify.model.Song;

import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class SearchListAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView title;
        TextView artist;
    }

    private Context context;
    private LayoutInflater inflater;
    private List<Song> items;

    public SearchListAdapter(Context context, List<Song> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Song item = items.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.artist.setText(item.getArtist());
        return convertView;
    }
}
