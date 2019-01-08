package com.example.andressito.contentprovidermplrss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseRSS extends SQLiteOpenHelper {

    public final static int VERSION = 27;
    public final static String DB_NAME = "base_rss";
    public final static String TABLE_RSS = "rss";
    public final static String COLONNE_TITLE = "title";
    public final static String COLONNE_LINK = "link";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_ADRESSE = "adresse";
    public final static String COLONNE_DATE="date";
    public final static String TABLE_ITEM="item";
    public final static String COLONE_UTILITE="utilite";


    public final static String CREATE_RSS = "create table " + TABLE_RSS + "(" +
            COLONNE_LINK+" string primary key," +
            COLONNE_ADRESSE+" string, "+
            COLONNE_TITLE+" string, "+
            COLONNE_DATE+ " string "+");";

    public final static String CREATE_ITEM= "create table "+ TABLE_ITEM +"("+
            COLONNE_LINK+" string primary key, "+
            COLONNE_ADRESSE+" string references rss, "+
            COLONNE_TITLE+" string, "+
            COLONNE_DESCRIPTION+" string, "+
            COLONE_UTILITE+" integer "+");";


    private static BaseRSS ourInstance;

    public static BaseRSS getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new BaseRSS(context);
        return ourInstance;
    }

    private BaseRSS(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RSS);
        db.execSQL(CREATE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_RSS);
            db.execSQL("drop table if exists "+ TABLE_ITEM);
            onCreate(db);
            Log.d("onUpgrade","base de donnée update");
        }
    }
}
