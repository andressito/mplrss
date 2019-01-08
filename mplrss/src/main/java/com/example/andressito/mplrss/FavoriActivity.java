package com.example.andressito.mplrss;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class FavoriActivity extends AppCompatActivity implements FavoriFragment.OnFragmentInteractionListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favori);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FavoriFragment fav = FavoriFragment.newInstance("1");
        fragmentManager= getFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentById(R.id.liste_item_all_fragment_frame) == null) {
            fragmentTransaction.add(R.id.liste_item_all_fragment_frame, fav);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.liste_item_all_fragment_frame, fav);
            fragmentTransaction.addToBackStack("changement");
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTitleFavoriSelection(String description) {
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
