package com.example.andressito.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AccesDonnees {
    ContentResolver cr;
    public final static String COLONNE_TITLE = "title";
    public final static String COLONNE_LINK = "link";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_ADRESSE = "adresse";
    public final static String COLONNE_DATE="date";
    public static final String COLONNE_UTILITE="utilite";
    public final static String authority="fr.kenymembou.rss";

    public AccesDonnees(Context context){ this.cr=context.getContentResolver(); }

    //fonction d'ajout d'un fichier Rss
    public void ajouterFichierRSS(String link,String adresse,String title,String date) throws ParseException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLONNE_LINK,link);
        contentValues.put(COLONNE_ADRESSE,adresse);
        contentValues.put(COLONNE_TITLE,title);
        contentValues.put(COLONNE_DATE,date);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("rss");
        Uri uri = builder.build();
        uri = cr.insert(uri,contentValues);
    }

    //fonction d'ajout d'un Item
    public void ajouterItem(String link,String adresse,String title, String description,int utilite) throws ParseException{
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLONNE_LINK,link);
        contentValues.put(COLONNE_ADRESSE,adresse);
        contentValues.put(COLONNE_TITLE,title);
        contentValues.put(COLONNE_DESCRIPTION,description);
        contentValues.put(COLONNE_UTILITE,utilite);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        uri = cr.insert(uri,contentValues);
    }

    //fonction pour avoir la date dans un fichier RSS pour l'ajout de celui ci dans la base ou pas
    public String avoirDateRss(String lien){
        Uri.Builder builder = new Uri.Builder();
        Cursor cursor;
        String where="link =?";
        String[] selection={lien};
        Uri uri= builder.scheme("content").authority(authority).appendPath("infoRss").appendPath(lien).build();
        cursor= cr.query(uri,new String[]{"date"},where,selection,null,null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("date"));
            } else
                return null;
        }else
            return null;
    }

    //fonction pour mettre à jour un Item avec comme filtre la description
    public void updateItem(String description){
        Uri.Builder builder= new Uri.Builder();
        String where="description =?";
        String[] selection={description};
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLONNE_UTILITE,1);
        Uri uri = builder.scheme("content").authority(authority).appendPath("item").build();
        cr.update(uri,contentValues,where,selection);
    }

    //fonction pour mettre à jour la date dans un fichier RSS
    public int updateRssDate(String lien,String date){
        Uri.Builder builder=new Uri.Builder();
        String where="link =?";
        String[] selection={lien};
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLONNE_DATE,date);
        Uri uri = builder.scheme("content").authority(authority).appendPath("rss").build();
        return cr.update(uri,contentValues,where,selection);
    }

    //fonction pour avoir le lien d'un item
    public String avoirLinkItem(String colonne,String filtre){
        Uri.Builder builder=new Uri.Builder();
        String where=colonne+" =?";
        String[] selection={filtre};
        Uri uri = builder.scheme("content").authority(authority).appendPath("infosItem").appendPath(filtre).build();
        Cursor cursor= cr.query(uri,new String[] {"link"},where,selection,null,null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("link"));
    }

    // fonction pour avoir toute la table des fichiers RSS
    public Cursor avoirTableRSS() {
        Uri.Builder builder = new Uri.Builder();
        Uri uri= builder.scheme("content").authority(authority).appendPath("rss").build();
        return cr.query(uri,null,null,null,null,null);

    }

    //fonction pour recupérer un item grace à son titre
    public Cursor avoirItem(String title){
        Uri.Builder builder=new Uri.Builder();
        String where="title =?";
        String[] selection={title};
        Uri uri = builder.scheme("content").authority(authority).appendPath("infosItem").appendPath(title).build();
        return cr.query(uri,null,where,selection,null,null);
    }

    //fonction pour supprimer un fichier RSS grace à son titre
    public int supprimerRss(String link) {
        Uri.Builder builder=new Uri.Builder();
        String whereRss="link =?";
        String[] selection={link};
        Uri uriRss= builder.scheme("content").authority(authority).appendPath("rss").build();
        return cr.delete(uriRss,whereRss,selection);
    }

    //fonction de suppression d'un Item grace à son adresse et en fonction de son utilité iu pas
    public int SupprimerItem(String adresse, String utilite){
        Uri.Builder builder= new Uri.Builder();
        String where="adresse =? and utilite=?";
        String[] selection={adresse,utilite};
        Uri uri=builder.scheme("content").authority(authority).appendPath("item").build();
        return cr.delete(uri,where,selection);
    }

    public int SupprimerItemTitle(String title){
        Uri.Builder builder= new Uri.Builder();
        String where="title =?";
        String[] selection={title};
        Uri uri=builder.scheme("content").authority(authority).appendPath("item").build();
        return cr.delete(uri,where,selection);
    }

    public String avoirAdresseRss(String link){
        Uri.Builder builder=new Uri.Builder();
        String where="link =?";
        String[] selection={link};
        Uri uri = builder.scheme("content").authority(authority).appendPath("rss").build();
        Cursor cursor= cr.query(uri,new String[] {"adresse"},where,selection,null,null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("adresse"));
    }

}
