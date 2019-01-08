package com.example.andressito.mplrss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Searchable extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static String authority="fr.kenymembou.rss";
    private SimpleCursorAdapter adapter;
    private String query;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Intent intent= getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query= intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
            doQuery(query);
        } else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            String id= intent.getData().getLastPathSegment();

        }

    }

    public void doQuery(String query){
        if(query.length()!=0) {
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                    new String[]{"title","description"}, new int[]{android.R.id.text1,android.R.id.text2}, 0);
            setListAdapter(adapter);
            getLoaderManager().initLoader(0,null,this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder= new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("searchItem").build();
        Intent intent= getIntent();
        String query= intent.getStringExtra(SearchManager.QUERY);
        String selectCond = "title like '%" + query + "%'";
        return new CursorLoader(this,uri, new String[]{"title","description"}, selectCond,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor c = (Cursor) l.getAdapter().getItem(position);
        String description = c.getString(c.getColumnIndex("description"));
        AccesDonnees ac = new AccesDonnees(this);
        String link = ac.avoirLinkItem("description",description);
        if(link.length()!=0) {
            try {
                Uri uri = Uri.parse(link);
                Intent web= new Intent(Intent.ACTION_VIEW,uri);
                startActivity(web);
            }catch (Exception e) {
                Log.d("erreur", e.getMessage());
            }
        }
    }
}
