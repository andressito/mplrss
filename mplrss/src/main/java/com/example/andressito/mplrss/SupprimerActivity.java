package com.example.andressito.mplrss;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import java.io.File;

public class SupprimerActivity extends AppCompatActivity implements AllRssFragment.OnFragmentInteractionListener, AllItemFragment.OnFragmentInteractionListener {


    public final static String authority="fr.kenymembou.rss";
    private SimpleCursorAdapter adapterSpinner;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    String titleRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supprimer);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void ItemFragmentAll(){
        AllItemFragment all = AllItemFragment.newInstance();
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

    public void RssFragmentAll(){
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

    public void rssDelete(View view) { RssFragmentAll();}



    public void itemDelete(View view) {
        ItemFragmentAll();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAllItemSelection(String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SupprimerActivity.this);
        alert.setTitle("Confirmer la suppression...");
        alert.setMessage("Etes-vous sur de vouloir supprimer l'Item?");
        alert.setIcon(R.drawable.delete);
        titleRes=title;
        alert.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UtiliteItem(titleRes);
                        // Write your code here to execute after dialog
                    }
                });
        alert.setNegativeButton("Non",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                    }
                });
        alert.show();
    }

    @Override
    public void onAllRssSelection(final String link) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SupprimerActivity.this);
        alert.setTitle("Confirmer la suppression...");
        alert.setMessage("Etes-vous sur de vouloir supprimer le Fichier RSS et ses Items?");
        alert.setIcon(R.drawable.delete);
        alert.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //supprimer fichier RSS + ses items qui ne sont pas en favori
                        supprimerRss(link);
                        RssFragmentAll();
                        Intent res= new Intent();
                        res.putExtra("changement","ok");
                        setResult(2,res);

                    }
                });
        alert.setNegativeButton("Non",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog

                    }
                });
        alert.show();
    }

    private void supprimerRss(String link) {
        AccesDonnees ac = new AccesDonnees(this);
        String chemin = ac.avoirAdresseRss(link);
        try {
            File fic = new File(chemin);
            if(fic.exists()){
                fic.delete();
            }
            ac.supprimerRss(link);
            ac.SupprimerItem(link,"0");
        }catch (Exception e){
            Log.d("error",e.getMessage());
        }

    }

    public void UtiliteItem(String title){
        final AccesDonnees ac = new AccesDonnees(this);
        Cursor res = ac.avoirItem(title);
        res.moveToFirst();
        int test=res.getInt(res.getColumnIndex("utilite"));
        if(test==0){
            ac.SupprimerItemTitle(title);
            ItemFragmentAll();
        }else{
            SupprimerUtiliteVrai(title);
        }
    }

    public void SupprimerUtiliteVrai(final String title){
        final AccesDonnees ac = new AccesDonnees(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(SupprimerActivity.this);
        alert.setTitle("Confirmer la suppression...");
        alert.setMessage("Cet Item est enregistré comme favori, êtes-vous sur de vouloir le supprimer?");
        alert.setIcon(R.drawable.delete);
        alert.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        ac.SupprimerItemTitle(title);
                        ItemFragmentAll();
                    }
                });
        alert.setNegativeButton("Non",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alert.show();
    }
}

