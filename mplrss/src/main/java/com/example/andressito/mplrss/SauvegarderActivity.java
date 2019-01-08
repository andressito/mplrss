package com.example.andressito.mplrss;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
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

import java.io.File;

public class SauvegarderActivity extends AppCompatActivity implements FavoriFragment.OnFragmentInteractionListener {
    private LoaderManager manager;
    private SimpleCursorAdapter adapter;
    public final static String authority="fr.kenymembou.rss";
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sauvegarder);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lancerAffichage();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTitleFavoriSelection(String description) {
        AccesDonnees ac = new AccesDonnees(this);
        ac.updateItem(description);
        Toast.makeText(getApplicationContext()," Item sauvegardé!!",Toast.LENGTH_SHORT).show();
        lancerAffichage();
    }

    public void lancerAffichage(){
        FavoriFragment all = FavoriFragment.newInstance("0");
        fragmentManager= getFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentById(R.id.liste_item_all_fragment_frame) == null) {
            fragmentTransaction.add(R.id.liste_item_all_fragment_frame, all);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.liste_item_all_fragment_frame, all);
            fragmentTransaction.addToBackStack("changement");
            fragmentTransaction.commit();
        }
    }
}
