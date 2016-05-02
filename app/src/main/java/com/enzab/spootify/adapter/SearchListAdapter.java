package com.enzab.spootify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enzab.spootify.R;
import com.enzab.spootify.model.ISearchItem;

import java.util.List;

/**
 * Created by linard_f on 4/7/16.
 */
public class SearchListAdapter extends BaseAdapter {

    public interface IProcessItemOptionSelection {
        void onItemOptionSelection(ISearchItem item, String option);
    }

    private static class ViewHolder {
        TextView title;
        TextView description;
        ImageButton options;
    }

    private final IProcessItemOptionSelection callBack;
    private Context context;
    private LayoutInflater inflater;
    private List<ISearchItem> items;

    public SearchListAdapter(Context context, List<ISearchItem> items, IProcessItemOptionSelection callback) {
        this.context = context;
        this.items = items;
        this.callBack = callback;
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
            viewHolder.options = (ImageButton) convertView.findViewById(R.id.options);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ISearchItem item = items.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        viewHolder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .autoDismiss(true)
                        .adapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1,
                                        context.getResources().getStringArray(R.array.song_options)),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        callBack.onItemOptionSelection(item, text.toString());
                                        dialog.dismiss();
                                    }
                                })
                        .show();
            }
        });
        return convertView;
    }

    public void addItem(ISearchItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

}
