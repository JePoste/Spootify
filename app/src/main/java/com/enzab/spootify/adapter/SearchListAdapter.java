package com.enzab.spootify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.enzab.spootify.R;
import com.enzab.spootify.model.SearchItem;

import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class SearchListAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView title;
        TextView description;
    }

    private Context context;
    private LayoutInflater inflater;
    private List<SearchItem> items;

    public SearchListAdapter(Context context, List<SearchItem> items) {
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
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SearchItem item = items.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        return convertView;
    }

    public void addItem(SearchItem item) {
        items.add(item);
        notifyDataSetChanged();
    }
}
