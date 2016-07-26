package com.weotri.timemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SepLite on 7/25/16.
 */

class Adapter3Col extends BaseAdapter {

    Context context;
    ArrayList<Task> data;
    int[] textFields = {R.id.text1, R.id.text2, R.id.text3, R.id.text4};

    private static LayoutInflater inflater = null;

    public Adapter3Col(Context context, ArrayList<Task> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return data.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_textview, null);

        ((TextView) vi.findViewById(textFields[0])).setText(data.get(position).name);
        if(data.get(position).estimate == 0){
            ((TextView) vi.findViewById(textFields[1])).setText("");
        }else{
            ((TextView) vi.findViewById(textFields[1])).setText(data.get(position).estimate + "");
        }
        ((TextView) vi.findViewById(textFields[2])).setText(data.get(position).getTime());
        ((TextView) vi.findViewById(textFields[3])).setText(data.get(position).getStatusString(context));


        return vi;
    }
}