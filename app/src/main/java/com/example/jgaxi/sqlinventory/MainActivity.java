package com.example.jgaxi.sqlinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.jgaxi.sqlinventory.data.SqLContract;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COURSE_LOADER = 0;

    CourseCursorAdapter adapter;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(view ->
        {
            Intent intent = new Intent(this, EditCourseActivity.class);
            startActivity(intent);

        });

        ListView courseList = findViewById(R.id.list_view);

        View emptyView = findViewById(R.id.empty_view);
        courseList.setEmptyView(emptyView);

        adapter = new CourseCursorAdapter(this, null);
        courseList.setAdapter(adapter);

        courseList.setOnItemClickListener((adapterView, view, position, id) -> {

            Intent intent = new Intent(this, EditCourseActivity.class);
            Uri currentUri = ContentUris.withAppendedId(SqLContract.SqlEntry.CONTENT_URI, id);
            intent.setData(currentUri);
            startActivity(intent);
        });

        courseList.setOnItemLongClickListener((adapterView, view, position, id) -> {
            ImageView imageview = view.findViewById(R.id.drop_button);
            imageview.setVisibility(View.VISIBLE);
            return true;
        } );

        getLoaderManager().initLoader(COURSE_LOADER, null, this);

    }

    //inserts default value to main
    private void insertCourse() {
        ContentValues values = new ContentValues();
        values.put(SqLContract.SqlEntry.COURSE_NAME_COLUMN, "Android Nanodegree");
        values.put(SqLContract.SqlEntry.COURSE_PRICE_COLUMN, "1999.99");
        values.put(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN, 24);
        values.put(SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN, "Mr. Smith");
        values.put(SqLContract.SqlEntry.COURSE_PHONENUM_COLUMN, "(323) 555 - 4444");

        Uri uri = getContentResolver().insert(SqLContract.SqlEntry.CONTENT_URI, values);

    }

    //deletes all list items in main
    private void deleteAllCourses() {

        int deleteAll = getContentResolver().delete(SqLContract.SqlEntry.CONTENT_URI, null, null);

    }

    //inflates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_item_menu, menu);
        return true;
    }

    //choose what happens when menu item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.insert_course:
                insertCourse();
                return true;

            case R.id.delete_all:
                deleteAllCourses();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //projection for database
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                SqLContract.SqlEntry._ID,
                SqLContract.SqlEntry.COURSE_NAME_COLUMN,
                SqLContract.SqlEntry.COURSE_PRICE_COLUMN,
                SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN,
                SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN,
                SqLContract.SqlEntry.COURSE_PHONENUM_COLUMN};

        return new CursorLoader(this, SqLContract.SqlEntry.CONTENT_URI, projection, null, null, null);

    }

    //load cursor
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    //reset cursor
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
