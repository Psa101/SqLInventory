package com.example.jgaxi.sqlinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;
import com.example.jgaxi.sqlinventory.data.SqLContract.SqlEntry;

public class SqLContentProvider extends ContentProvider {

    private static final int COURSES = 100;

    private static final int COURSE_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(SqLContract.CONTENT_AUTHORITY, SqLContract.PATH_COURSES, COURSES);

        uriMatcher.addURI(SqLContract.CONTENT_AUTHORITY, SqLContract.PATH_COURSES + "/#", COURSE_ID);
    }

    private SqLDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new SqLDBHelper(getContext());
        return true;
    }

    //query method
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case COURSES:
                cursor = database.query(SqlEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE_ID:
                selection = SqLContract.SqlEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(SqLContract.SqlEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    //insert method
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case COURSES:
                return insertCourse(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for" + uri);
        }

    }

    //insert course method
    private Uri insertCourse(Uri uri, ContentValues values) {
        String name = values.getAsString(SqLContract.SqlEntry.COURSE_NAME_COLUMN);
        if (name == null) {
            Toast.makeText(getContext(), "Course Name Required", Toast.LENGTH_SHORT).show();
        }

        String price = values.getAsString(SqLContract.SqlEntry.COURSE_PRICE_COLUMN);
        float priceCheck = Float.parseFloat(price);
        if (price != null && priceCheck < 0)
            Toast.makeText(getContext(), "Invalid Price", Toast.LENGTH_SHORT).show();

        Integer quantity = values.getAsInteger(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN);
        if (quantity != null && quantity < 0)
            Toast.makeText(getContext(), "Invalid Quantity", Toast.LENGTH_SHORT).show();

        String supplier = values.getAsString(SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN);
        if (supplier == null)
            Toast.makeText(getContext(), "Supplier Name Required", Toast.LENGTH_SHORT).show();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        long id = database.insert(SqLContract.SqlEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    //checks for incoming match
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case COURSES:
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case COURSE_ID:
                selection = SqLContract.SqlEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCourse(uri, contentValues, selection, selectionArgs);
            default:
                Toast.makeText(getContext(), "Whoops", Toast.LENGTH_SHORT).show();
                return 0;
        }
    }

    //update course in db
    private int updateCourse(Uri uri, ContentValues values, String selections, String[] selectionArgs) {
        if (values.containsKey(SqLContract.SqlEntry.COURSE_NAME_COLUMN)) {
            String name = values.getAsString(SqLContract.SqlEntry.COURSE_NAME_COLUMN);
            if (name == null) {
                Toast.makeText(getContext(), "No name", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(SqLContract.SqlEntry.COURSE_PRICE_COLUMN)) {
            Float price = values.getAsFloat(SqLContract.SqlEntry.COURSE_PRICE_COLUMN);
            if (price != null && price < 0) {
                Toast.makeText(getContext(), "Invalid Price", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN)) {
            String supplier = values.getAsString(SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN);
            if (supplier == null) {
                Toast.makeText(getContext(), "No Supplier", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN)) {
            Integer students = values.getAsInteger(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN);
            if (students == null) {
                Toast.makeText(getContext(), "No Students", Toast.LENGTH_SHORT).show();
                return 0;
            } else {
                if(students < 0){
                    Toast.makeText(getContext(), "No Students", Toast.LENGTH_SHORT).show();
                    return 0;
                }
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = database.update(SqLContract.SqlEntry.TABLE_NAME, values, selections, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    //delete info from db
    @Override
    public int delete(Uri uri, String selection, String[] args) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsToDelete;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case COURSES:
                rowsToDelete = database.delete(SqLContract.SqlEntry.TABLE_NAME, selection, args);
                break;
            case COURSE_ID:
                selection = SqLContract.SqlEntry._ID + "=?";
                args = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsToDelete = database.delete(SqLContract.SqlEntry.TABLE_NAME, selection, args);
                break;
            default:
                throw new IllegalArgumentException("Not supported");

        }

        if (rowsToDelete != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsToDelete;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case COURSES:
                return SqLContract.SqlEntry.CONTENT_LIST_TYPE;
            case COURSE_ID:
                return SqLContract.SqlEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }
}