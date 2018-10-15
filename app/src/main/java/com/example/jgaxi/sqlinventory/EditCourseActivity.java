package com.example.jgaxi.sqlinventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jgaxi.sqlinventory.data.SqLContract;

public class EditCourseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COURSE_LOADER = 0;

    private Uri currentUri;

    private EditText courseNameText;
    private EditText coursePriceText;
    private TextView courseQuantityText;
    private EditText courseSupplierText;
    private EditText courseSupplierNumText;

    private boolean haschanged;

    private View.OnTouchListener touchListener = (view, motionEvent) -> {
        view.performClick();
        haschanged = true;
        return false;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_course_layout);

        Intent intent = getIntent();
        currentUri = intent.getData();

        if (currentUri == null) {
            setTitle("New Course");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Course");
            getLoaderManager().initLoader(COURSE_LOADER, null, this);
        }

        courseNameText = findViewById(R.id.edit_course_name);
        coursePriceText = findViewById(R.id.edit_course_price);
        courseQuantityText = findViewById(R.id.edit_students);
        courseSupplierText = findViewById(R.id.edit_professor);
        courseSupplierNumText = findViewById(R.id.edit_phoneNum);

        ImageView addStudent = findViewById(R.id.edit_increase_button);
        ImageView removeStudent = findViewById(R.id.edit_decrease_button);

        FloatingActionButton editFab = findViewById(R.id.edit_call_fab);

        editFab.setOnClickListener(view -> {

            if (courseSupplierNumText != null) {
                if(courseSupplierNumText.getText().toString().trim().isEmpty()){
                    Toast.makeText(this, "Number Required", Toast.LENGTH_SHORT).show();
                } else {
                    Intent callSupplier = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + courseSupplierNumText.getText().toString().trim()));
                    startActivity(callSupplier);
                }
            } else {
                Toast.makeText(this, "Course Required", Toast.LENGTH_SHORT).show();
            }
        });

        addStudent.setOnClickListener(view -> {
            int currentValue = Integer.parseInt(courseQuantityText.getText().toString());
            if (currentValue < 25) {
                currentValue++;
                courseQuantityText.setText(Integer.toString(currentValue));
            } else {
                Toast.makeText(this, "25 Student Limit Reached", Toast.LENGTH_SHORT).show();
            }

        });

        removeStudent.setOnClickListener(view -> {
            int currentValue = Integer.parseInt(courseQuantityText.getText().toString());
            if (currentValue == 0) {
                Toast.makeText(this, "No Students", Toast.LENGTH_SHORT).show();

            } else {
                currentValue--;
                courseQuantityText.setText(Integer.toString(currentValue));
            }

        });


        courseNameText.setOnTouchListener(touchListener);
        coursePriceText.setOnTouchListener(touchListener);
        courseQuantityText.setOnTouchListener(touchListener);
        courseSupplierText.setOnTouchListener(touchListener);
        courseSupplierNumText.setOnTouchListener(touchListener);

    }

    private boolean saveCourse() {
        boolean ready = true;
        String name = courseNameText.getText().toString().trim();
        String price = coursePriceText.getText().toString().trim();
        String quantity = courseQuantityText.getText().toString().trim();
        String supplier = courseSupplierText.getText().toString().trim();
        String supplierNum = courseSupplierNumText.getText().toString().trim();

        if (currentUri == null) {

            if (name.isEmpty()) {
                Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show();
                return false;
            } else if (price.isEmpty()) {
                Toast.makeText(this, "Price Required", Toast.LENGTH_SHORT).show();
                return false;
            } else if (Integer.parseInt(quantity) == 0) {
                Toast.makeText(this, "Quantity Required", Toast.LENGTH_SHORT).show();
                return false;
            } else if (supplier.isEmpty()) {
                Toast.makeText(this, "Professor Required", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        ContentValues values = new ContentValues();
        values.put(SqLContract.SqlEntry.COURSE_NAME_COLUMN, name);
        values.put(SqLContract.SqlEntry.COURSE_PRICE_COLUMN, price);
        values.put(SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN, supplier);
        values.put(SqLContract.SqlEntry.COURSE_PHONENUM_COLUMN, supplierNum);
        values.put(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN, quantity);

        if (currentUri == null) {
            Uri newUri = getContentResolver().insert(SqLContract.SqlEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Whoops", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Course Saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int update = getContentResolver().update(currentUri, values, null, null);

            if (update == 0) {
                Toast.makeText(this, "Whoops", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Course Updated", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentUri == null) {
            MenuItem item = menu.findItem(R.id.delete_item);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                boolean ready;
                ready = saveCourse();
                if (!ready)
                    return true;
                else {
                    finish();
                    return true;
                }
            case R.id.delete_item:
                deleteCourse();
                finish();
                return true;
            case android.R.id.home:
                if (!haschanged) {
                    NavUtils.navigateUpFromSameTask(EditCourseActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener leaveClickListener =
                        (dialogInterface, i) -> {

                            NavUtils.navigateUpFromSameTask(EditCourseActivity.this);
                        };

                showUnsavedChangesDialog(leaveClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!haschanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                SqLContract.SqlEntry._ID,
                SqLContract.SqlEntry.COURSE_NAME_COLUMN,
                SqLContract.SqlEntry.COURSE_PRICE_COLUMN,
                SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN,
                SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN,
                SqLContract.SqlEntry.COURSE_PHONENUM_COLUMN};

        return new CursorLoader(this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_NAME_COLUMN);
            int priceIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_PRICE_COLUMN);
            int quantityIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_QUANTITY_COLUMN);
            int supplierIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_SUPPLIER_COLUMN);
            int phoneNumIndex = cursor.getColumnIndex(SqLContract.SqlEntry.COURSE_PHONENUM_COLUMN);

            String name = cursor.getString(nameIndex);
            String price = cursor.getString(priceIndex);
            int quantity = cursor.getInt(quantityIndex);
            String supplier = cursor.getString(supplierIndex);
            String phoneNum = cursor.getString(phoneNumIndex);

            courseNameText.setText(name);
            coursePriceText.setText(price);
            courseQuantityText.setText(Integer.toString(quantity));
            courseSupplierText.setText(supplier);
            courseSupplierNumText.setText(phoneNum);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        courseNameText.setText("");
        coursePriceText.setText("");
        courseSupplierText.setText("");
        courseSupplierNumText.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener cancelClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Leaving?");
        builder.setNegativeButton("Leave", cancelClickListener);
        builder.setPositiveButton("Keep Working", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteCourse() {

        if (currentUri != null) {

            int delete = getContentResolver().delete(currentUri, null, null);


            if (delete == 0) {

                Toast.makeText(this, "Whoops",
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Course Removed",
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
