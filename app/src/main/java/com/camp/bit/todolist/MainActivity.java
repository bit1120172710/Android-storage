package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;
import com.camp.bit.todolist.debug.DebugActivity;
import com.camp.bit.todolist.ui.NoteListAdapter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    public TodoDbHelper mDbHelper;
    public SQLiteDatabase database;
    public SQLiteDatabase writedatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbHelper =new TodoDbHelper(this);
        database=mDbHelper.getReadableDatabase();
        writedatabase=mDbHelper.getWritableDatabase();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        database.close();
        database=null;
        writedatabase.close();
        writedatabase=null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {

        if(database==null)
        {
            return Collections.emptyList();
        }
        List<Note>result =new LinkedList<>();
        Cursor cursor=null;
        try{
            cursor=database.query(TodoContract.TodoEntry.TABLE_NAME, null,null,null,
                    null,null,
                    TodoContract.TodoEntry.COLUMN1_NAME+ " DESC");

            while(cursor.moveToNext()){
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN3_NAME));
                long dateMs=cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN1_NAME));
                int intState =cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN2_NAME));
                Note note =new Note( cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID)));
                note.setContent(content);
                note.setDate(new Date(dateMs));
                note.setState(State.from(intState));
                result.add(note);
            }
        }finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return result;

        // TODO 从数据库中查询数据，并转换成 JavaBeans

    }

    private void deleteNote(Note note) {
        String selection =TodoContract.TodoEntry._ID+" LIKE ?";
        String[] selectionArgs={String.valueOf(note.id)};
       int rows= writedatabase.delete(TodoContract.TodoEntry.TABLE_NAME,selection,selectionArgs);
        if(rows>0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
       // TODO 删除数据
    }

    private void updateNode(Note note) {
      String selection =TodoContract.TodoEntry._ID+" LIKE ?";
         String[] selectionArgs={String.valueOf(note.id)};
         ContentValues values=new ContentValues();
         values.put(TodoContract.TodoEntry.COLUMN2_NAME,note.getState().intValue);
         int rows=writedatabase.update(TodoContract.TodoEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rows>0)
        {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
        // 更新数据
    }

}
