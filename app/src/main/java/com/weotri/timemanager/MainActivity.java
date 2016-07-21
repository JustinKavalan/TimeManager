package com.weotri.timemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import java.util.Calendar;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import com.weotri.timemanager.Task;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public final String listName = "taskList";
    ListView list;
    ArrayList<Task> listContent;
    Adapter3Col listAdapter;
    LayoutInflater inflater;
    View dialogView;
    SharedPreferences sharedPref;
    Gson gson;
    Handler timer;
    AlertDialog.Builder newTaskBuilder;
    String jsonList;
    SharedPreferences.Editor sharedPrefEdit;
    int[] textInputFields = {R.id.text_input1, R.id.text_input2};
    Comparator<ArrayList<Long>> customComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customComparator =  new Comparator<ArrayList<Long>>() {
            @Override
            public int compare(ArrayList<Long> list1, ArrayList<Long> list2) {
                long index1 = list1.get(0);
                long index2 = list2.get(0);
                if(index1 > index2){
                    return 1;
                }else if(index1 < index2){
                    return -1;
                }else {
                    return 0;
                }
            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = this.getPreferences(this.MODE_PRIVATE);
        sharedPrefEdit = sharedPref.edit();
        jsonList = sharedPref.getString(listName, "");
        gson = new Gson();
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        if(jsonList.equals("")){
            listContent = new ArrayList<Task>();
        }else {
            listContent = gson.fromJson(jsonList, type);
        }

        listAdapter = new Adapter3Col(this, listContent);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(listAdapter);
        this.registerForContextMenu(list);


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
                                String[] array = new String[textInputFields.length];
                                for(int j = 0; j < textInputFields.length; j++){
                                    array[j] = ((TextView) dialogView.findViewById(textInputFields[j])).getText().toString();
                                }
                                String name = ((TextView) dialogView.findViewById(textInputFields[0])).getText().toString();
                                String estimate = ((TextView) dialogView.findViewById(textInputFields[1])).getText().toString();
                                Task task = new Task(name, Integer.parseInt(estimate));
                                listContent.add(task);
                                jsonList = gson.toJson(listContent);
                                sharedPrefEdit.putString(listName, jsonList);
                                sharedPrefEdit.commit();
                                listAdapter.notifyDataSetChanged();
                            }
                        }).show();
            }
        });

        timer = new Handler();
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("info", "Timers updated");
                listAdapter.notifyDataSetChanged();
                jsonList = gson.toJson(listContent);
                sharedPrefEdit.putString(listName, jsonList);
                sharedPrefEdit.apply();
                timer.postDelayed(this, 10000);
            }
        }, 10000);

    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int status = listContent.get(info.position).getStatus();
        switch (status) {
            case Task.ACTIVE:
                menu.findItem(R.id.action_resume).setVisible(false);
                menu.findItem(R.id.action_start).setVisible(false);
                break;
            case Task.ENDED:
                menu.findItem(R.id.action_pause).setVisible(false);
                menu.findItem(R.id.action_resume).setVisible(false);
                menu.findItem(R.id.action_start).setVisible(false);
                menu.findItem(R.id.action_stop).setVisible(false);
                break;
            case Task.NOT_STARTED:
                menu.findItem(R.id.action_stop).setVisible(false);
                menu.findItem(R.id.action_pause).setVisible(false);
                menu.findItem(R.id.action_resume).setVisible(false);
                break;
            case Task.PAUSED:
                menu.findItem(R.id.action_pause).setVisible(false);
                menu.findItem(R.id.action_start).setVisible(false);
                menu.findItem(R.id.action_stop).setVisible(false);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.action_delete:
                listContent.remove(info.position);
                jsonList = gson.toJson(listContent);
                sharedPrefEdit.remove(listName);
                sharedPrefEdit.putString(listName, jsonList);
                sharedPrefEdit.commit();
                listAdapter.notifyDataSetChanged();
                Log.d("info", "Task at position " + info.position + " removed");
                return true;
            case R.id.action_start:
                listContent.get(info.position).start();
                Log.d("info", "Task at position " + info.position + " added");
                return true;
            case R.id.action_stop:
                listContent.get(info.position).end();
                Log.d("info", "Task at position " + info.position + " ended");
                return true;
            case R.id.action_pause:
                listContent.get(info.position).pause();
                Log.d("info", "Task at position " + info.position + " paused");
                return true;
            case R.id.action_resume:
                listContent.get(info.position).resume();
                Log.d("info", "Task at position " + info.position + " resumed");
                return true;
            default:
                return super.onContextItemSelected(item);

        }

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

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("info", "Stopped called");
        timer.removeCallbacksAndMessages(null);
    }


}

class Adapter3Col extends BaseAdapter {

    Context context;
    ArrayList<Task> data;
    int[] textFields = {R.id.text1, R.id.text2, R.id.text3};

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
        ((TextView) vi.findViewById(textFields[1])).setText(data.get(position).estimate + "");
        ((TextView) vi.findViewById(textFields[2])).setText(data.get(position).getTime());


        return vi;
    }
}