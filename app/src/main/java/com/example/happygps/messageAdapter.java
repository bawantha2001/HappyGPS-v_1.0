package com.example.happygps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class messageAdapter extends ArrayAdapter<keywords> {
    private Context mcontext;
    private int mResource;

    public messageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<keywords> objects) {
        super(context, resource, objects);
        this.mcontext=context;
        this.mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(mcontext);
        convertView=layoutInflater.inflate(mResource,parent,false);
        TextView keyWord =convertView.findViewById(R.id.msg);
        keyWord.setText(getItem(position).getKey());
        return convertView;
    }
}
