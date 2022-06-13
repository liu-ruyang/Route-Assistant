package com.example.perfectnavigationassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.perfectnavigationassistant.R;
import com.example.perfectnavigationassistant.bean.Filter;

import java.util.List;

public class FilterAdapter extends ArrayAdapter<Filter> {
    private int resourceId;

    public FilterAdapter(Context context, int resource, List<Filter> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Filter filter = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView filterItem = view.findViewById(R.id.filter);
        filterItem.setText(filter.getRecommendContext());
        return view;
    }

}
