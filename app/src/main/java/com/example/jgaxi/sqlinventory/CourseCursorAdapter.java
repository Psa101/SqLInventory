package com.example.jgaxi.sqlinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jgaxi.sqlinventory.data.SqLContract;

public class CourseCursorAdapter extends CursorAdapter{

    TextView sizeTextView;

    public CourseCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    //chose layout to inflate
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R. layout.course_item_layout, viewGroup, false);
    }

    //binds textview to info from database
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.item_name);
        sizeTextView = view.findViewById(R.id.item_size);
        TextView priceTextView = view.findViewById(R.id.item_price);
        ImageView imageView = view.findViewById(R.id.drop_button);

        int idIndex = cursor.getColumnIndex(BaseColumns._ID);
        int nameIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_NAME_COLUMN);
        int quantityIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN);
        int priceIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_PRICE_COLUMN);

        String name = cursor.getString(nameIndex);
        final int courseId = cursor.getInt(idIndex);
        final Integer quantity = cursor.getInt(quantityIndex);
        String price = "$" + cursor.getString(priceIndex);

        //list item button updates info on db
        imageView.setOnClickListener(click ->{

            imageView.setVisibility(View.GONE);
            Integer studentsEnrolled = quantity;
            if( studentsEnrolled != null && studentsEnrolled > 0 ) {
                studentsEnrolled--;
                ContentValues values = new ContentValues();
                values.put(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN, studentsEnrolled);
                Uri uri = ContentUris.withAppendedId(SqLContract.SqlEntry.CONTENT_URI, courseId);
                context.getContentResolver().update(uri, values, null, null);

            } else {
                Toast.makeText(context, "No Students", Toast.LENGTH_SHORT).show();
            }

        });

        String students = "Students Enrolled: " + quantity.toString();

        nameTextView.setText(name);
        sizeTextView.setText(students);
        priceTextView.setText(price);

    }

}
