package com.example.andressito.contentprovidermplrss;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class MyContentProvider extends ContentProvider {

    private BaseRSS rss;
    private static final int CODE_RSS =1;
    private static final int CODE_ITEM=2;
    private static final int CODE_INFO_RSS=3;
    private static final int CODE_INFO_ITEM=4;
    private static final int CODE_SUGGEST=5;
    private static final int CODE_SEARCH=6;
    private static final String authority="fr.kenymembou.rss";
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(authority,"rss",CODE_RSS);
        matcher.addURI(authority,"item",CODE_ITEM);
        matcher.addURI(authority,"infoRss/*",CODE_INFO_RSS);
        matcher.addURI(authority,"infosItem/*",CODE_INFO_ITEM);
        matcher.addURI(authority,"searchItem",CODE_SEARCH);
    }

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db= rss.getWritableDatabase();
        int i;
        int code= matcher.match(uri);
        Log.d("RSSITEM",code+" ");
        switch (code) {

            case CODE_RSS:
                i=db.delete("rss",selection,selectionArgs);
                Log.d("delete",Integer.toString(i));
                return i;
            case CODE_ITEM:
                i=db.delete("item",selection,selectionArgs);
                Log.d("delete",Integer.toString(i));
                return i;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db= rss.getWritableDatabase();
        int code= matcher.match(uri);
        long id=0;
        String path;
        switch (code){
            case CODE_RSS:
                try {
                    id=db.insertOrThrow("rss",null,values);
                    Log.d("reussiFichierRss",values.toString());

                }catch(SQLException e){
                    Log.d("echec","instertion impossible",e);
                }
                path="rss";
                break;
            case CODE_ITEM:
                try {
                    id=db.insertOrThrow("item",null,values);
                    Log.d("reussiItem",values.toString());

                }catch(SQLException e){
                    Log.d("echec","instertion impossible",e);
                }
                path="item";
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        Uri.Builder builder = (new Uri.Builder())
                .authority(authority)
                .appendPath(path);
        return ContentUris.appendId(builder,id).build();
    }

    @Override
    public boolean onCreate() {
        rss=BaseRSS.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db= rss.getReadableDatabase();
        String[] colonne={"rowid as _id","*"};
        int code= matcher.match(uri);
        switch (code){
            case CODE_RSS:
                return db.query("rss",colonne,selection,selectionArgs,null,null,sortOrder);
            case CODE_SUGGEST:
                String u=uri.getLastPathSegment();
                String[] columSugges={"rowid as "+BaseColumns._ID,"rowid as "+SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
                "title as "+SearchManager.SUGGEST_COLUMN_TEXT_1};
                return db.query("item",columSugges,"LIKE ?",new String[]{"%"+u+"%"},null,null,sortOrder);
            case CODE_SEARCH:
                return db.query("item",colonne,selection,selectionArgs,null,null,sortOrder);
            case CODE_ITEM:
                return db.query("item",colonne,selection,selectionArgs,null,null,sortOrder);
            case CODE_INFO_RSS:
                return db.query("rss",projection,selection,selectionArgs,null,null,sortOrder);
            case CODE_INFO_ITEM:
                return db.query("item",projection,selection,selectionArgs,null,null,sortOrder);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db= rss.getWritableDatabase();
        int i;
        int code= matcher.match(uri);
        switch (code){
            case CODE_RSS:
                i=db.update("rss",values,selection,selectionArgs);
                Log.d("update",Integer.toString(i));
                return i;
            case CODE_ITEM:
                i=db.update("item",values,selection,selectionArgs);
                Log.d("update",Integer.toString(i));
                return i;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
