package com.example.andressito.mplrss;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class FichierRssActivity extends AppCompatActivity implements  AllRssFragment.OnFragmentInteractionListener, ListItemFragment.OnFragmentInteractionListener, detailsItemFragment.OnFragmentInteractionListener{
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichier_rss);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AllRssFragment all = AllRssFragment.newInstance();
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetailSelection(String description) {
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

    @Override
    public void onTitleSelection(String title) {
        AccesDonnees ac = new AccesDonnees(this);
        String link = ac.avoirLinkItem("title",title);
        detailsItemFragment details= detailsItemFragment.newInstance(link);
        fragmentManager= getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentById(R.id.liste_item_all_fragment_frame) == null) {
            fragmentTransaction.add(R.id.liste_item_all_fragment_frame, details);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.liste_item_all_fragment_frame, details);
            fragmentTransaction.addToBackStack("debut");
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onAllRssSelection(String link) {
        ListItemFragment item = ListItemFragment.newInstance(link);
        fragmentManager= getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentById(R.id.liste_item_all_fragment_frame) == null) {
            fragmentTransaction.add(R.id.liste_item_all_fragment_frame, item);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.liste_item_all_fragment_frame, item);
            fragmentTransaction.addToBackStack("changement");
            fragmentTransaction.commit();
        }
    }
}
