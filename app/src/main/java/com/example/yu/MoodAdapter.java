package com.example.yu;

import android.widget.BaseAdapter;
import java.util.List;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MoodAdapter extends BaseAdapter {
    private List<MoodItem> moodItems;
    private LayoutInflater inflater;

    public MoodAdapter(Context context, List<MoodItem> moodItems) {
        this.moodItems = moodItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return moodItems.size();
    }

    @Override
    public Object getItem(int position) {
        return moodItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.diary_item, parent, false);
        }

        ImageView moodImageView = convertView.findViewById(R.id.spinnerItemImageView);
        TextView moodTextView = convertView.findViewById(R.id.spinnerItemTextView);

        MoodItem moodItem = moodItems.get(position);
        moodImageView.setImageResource(moodItem.getImageResource());
        moodTextView.setText(moodItem.getText());

        return convertView;
    }

}
