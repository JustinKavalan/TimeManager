package com.weotri.timemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public final String listName = "taskList";
    ListView list;
    ArrayList<String[]> listContent;
    Adapter3Col listAdapter;
    LayoutInflater inflater;
    View dialogView;
    SharedPreferences sharedPref;
    AlertDialog.Builder newTaskBuilder;
    AlertDialog.Builder removeTaskBuilder;
    String jsonList;
    SharedPreferences.Editor sharedPrefEdit;
    int[] textInputFields = {R.id.text_input1, R.id.text_input2, R.id.text_input3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        sharedPref = this.getPreferences(this.MODE_PRIVATE);
        sharedPrefEdit = sharedPref.edit();
        jsonList = sharedPref.getString(listName, "");
        final Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String[]>>() {}.getType();
        if(jsonList.equals("")){
            listContent = new ArrayList<String[]>();
        }else {
            listContent = gson.fromJson(jsonList, type);
        }*/

        listContent = new ArrayList<String[]>();
        listAdapter = new Adapter3Col(this, listContent);

        list = (ListView) findViewById(R.id.list);
//        listAdapter = new Adapter3Col(this,);
        list.setAdapter(listAdapter);

        inflater = this.getLayoutInflater();
        newTaskBuilder = new AlertDialog.Builder(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = inflater.inflate(R.layout.dialog, null);
                newTaskBuilder.setTitle(R.string.new_event)
                        .setView(dialogView)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                EditText editText = (EditText) dialogView.findViewById(R.id.text_input1);
//                                String text = editText.getText().toString();
                                String[] array = new String[textInputFields.length];
//                                   /* jsonList = gson.toJson(listContent);
//                                    sharedPrefEdit.putString(listName, jsonList);
//                                    sharedPrefEdit.commit();
//                                    listAdapter.notifyDataSetChanged();)*/
                                for(int j = 0; j < textInputFields.length; j++){
                                    array[j] = ((TextView) dialogView.findViewById(textInputFields[j])).getText().toString();
                                }
                                listContent.add(array);
                                listAdapter.notifyDataSetChanged();
                            }
                        }).show();
            }
        });

        removeTaskBuilder = new AlertDialog.Builder(this);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int positionToRemove = i;
                removeTaskBuilder.setTitle(R.string.delete_question)
                        .setMessage(R.string.delete_prompt)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               listContent.remove(positionToRemove);
                          /*     jsonList = gson.toJson(listContent);
                               sharedPrefEdit.remove(listName);
                               sharedPrefEdit.putString(listName, jsonList);
                               sharedPrefEdit.commit();
                               listAdapter.notifyDataSetChanged();*/
                           }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();

        TextView date = (TextView) findViewById(R.id.date);
        date.setText(DateFormat.getDateInstance(1).format(new Date()));
    }


}
/*
class TwoDArrayAdapter extends ArrayAdapter<>{
    public ArrayList<String[]>(){

    }
}

*/

class Adapter3Col extends BaseAdapter {

    Context context;
    ArrayList<String[]> data;
    int[] textFields = {R.id.text1, R.id.text2, R.id.text3};

    private static LayoutInflater inflater = null;

    public Adapter3Col(Context context, ArrayList<String[]> data) {
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
        return data.get(0)[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_textview, null);

        for(int i = 0; i < data.get(position).length; i++){
            TextView text = null;

            text = (TextView) vi.findViewById(textFields[i]);

            text.setText(data.get(position)[i]);
        }

        return vi;
    }
}