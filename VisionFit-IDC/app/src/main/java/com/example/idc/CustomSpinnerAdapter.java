package com.example.idc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {

    private final List<String> list;
    private final LayoutInflater inflater;
    private int selectedItemPosition = -1;

    public CustomSpinnerAdapter(Context context, List<String> list) {
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_outer_view, parent, false);
        }

        String text = list.get(position);
        TextView textView = convertView.findViewById(R.id.spinner_inner_text);
        textView.setText(text);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_inner_view, parent, false);
        }

        String text = list.get(position);
        TextView textView = convertView.findViewById(R.id.spinner_text);
        textView.setText(text);

        return convertView;
    }

    // 스피너에서 선택된 항목의 위치를 설정하는 메서드
    public void setSelectedItemPosition(int position) {
        selectedItemPosition = position;
    }

    // 스피너에서 선택된 항목을 반환하는 메서드
    public String getSelectedItem() {
        return selectedItemPosition != -1 ? list.get(selectedItemPosition) : null;
    }
}