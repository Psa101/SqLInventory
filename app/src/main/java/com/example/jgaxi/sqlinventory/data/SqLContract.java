package com.example.jgaxi.sqlinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class SqLContract {

    //private no arg const
    private SqLContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.jgaxi.sqlinventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COURSES = "courses";

    //inner class
    public static final class SqlEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COURSES);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSES;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSES;

        public final static String TABLE_NAME = "courses";

        public final static String _ID = BaseColumns._ID;

        public final static String COURSE_NAME_COLUMN ="name";

        public final static String COURSE_PRICE_COLUMN ="price";

        public final static String COURSE_QUANTITY_COLUMN ="quantity";

        public final static String COURSE_SUPPLIER_COLUMN ="supplier";

        public final static String COURSE_PHONENUM_COLUMN ="phoneNum";

    }



}
