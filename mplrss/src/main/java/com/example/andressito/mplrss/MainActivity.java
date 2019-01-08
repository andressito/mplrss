package com.example.andressito.mplrss;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements ListItemFragment.OnFragmentInteractionListener, detailsItemFragment.OnFragmentInteractionListener,LoaderManager.LoaderCallbacks<Cursor> {
    private EditText url;
    private ProgressBar progressBar;
    private AccesDonnees ac;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean downloading=false;
    private boolean annulerTelechargement=false;
    private String liensauvé=null;
    public final static String authority="fr.kenymembou.rss";
    private Spinner sp_adresse;
    private LoaderManager manager;
    private SimpleCursorAdapter adapterSpinner;
    private String lienRSS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url= findViewById(R.id.adresse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sp_adresse = (Spinner) findViewById(R.id.sp_adresse);
        suppressionAuto();
        ac = new AccesDonnees(this);
        fragmentManager= getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
       adapterSpinner = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,ac.avoirTableRSS(), new String[] {"link"},
                new int[] {android.R.id.text1});
        sp_adresse.setAdapter(adapterSpinner);
        if(savedInstanceState!=null){
            String titre =  savedInstanceState.getString("title");
                if(titre!=null) {
                    if(titre.length()!=0)
                        onTitleSelection(titre);
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menumplrss,menu);
        MenuItem searchItem= menu.findItem(R.id.search);
        SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.mesfavoris:
                Intent fav = new Intent(getApplicationContext(),FavoriActivity.class);
                startActivity(fav);
                return true;
            case R.id.mesfichiers:
                Intent fic = new Intent(getApplicationContext(),FichierRssActivity.class);
                startActivity(fic);
                return true;
            case R.id.supprimer:
                Intent sup= new Intent(getApplicationContext(),SupprimerActivity.class);
                startActivity(sup);
                return true;
            case android.R.id.home:
                intent = getSupportParentActivityIntent();
                NavUtils.navigateUpTo(this,intent);
            case R.id.sauvegarder:
                Intent sauv= new Intent(getApplicationContext(),SauvegarderActivity.class);
                startActivity(sauv);
                return true;
            default:
                return false;
        }
    }


    //fonction de suppression automatique
    public void suppressionAuto(){
        AccesDonnees ac = new AccesDonnees(this);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String tod= mdformat.format(calendar.getTime());
        try {
            Date today= mdformat.parse(tod);
            Cursor tableRss= ac.avoirTableRSS();
            while(tableRss.moveToNext()){
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date dateFic= dateFormat.parse(tableRss.getString(tableRss.getColumnIndex("date")));
                if(estAncien(today,dateFic)){
                    String chemin = ac.avoirAdresseRss(tableRss.getString(tableRss.getColumnIndex("link")));
                    try {
                        File fic = new File(chemin);
                        if(fic.exists()){
                            fic.delete();
                        }
                        ac.supprimerRss(tableRss.getString(tableRss.getColumnIndex("link")));
                        ac.SupprimerItem(tableRss.getString(tableRss.getColumnIndex("link")),"0");
                        adapterSpinner = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,ac.avoirTableRSS(), new String[] {"link"},
                                new int[] {android.R.id.text1});
                        sp_adresse.setAdapter(adapterSpinner);
                    }catch (Exception e){
                        Log.d("error",e.getMessage());
                    }
                }
            }
            tableRss.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //fonction de comparaison de deux dates
    public boolean estAncien(Date dateNow, Date dateFic){
        int anneeNow= dateNow.getYear();
        int moisNow= dateNow.getMonth();
        int jourNow= dateNow.getDay();
        int anneeFic= dateFic.getYear();
        int moisFic= dateFic.getMonth();
        int jourFic= dateFic.getDay();
        if(anneeNow==anneeFic && moisNow==moisFic){
            if(jourNow-jourFic>3){
               return true;
            }else
                return false;
        }else{
            return true;
        }
    }

    public void adressehtml(View view) {
        final String lien= url.getText().toString();
        if(lien.length()!=0){
            Uri uri = Uri.parse(lien);
            if (ConnexionInternet()) {
                final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = null;
                try{
                    request = new DownloadManager.Request(uri);
                    request.setDescription("Téléchargement").setTitle("Notification");
                    request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
                    request.setVisibleInDownloadsUi(true);
                    final long myDownloadReference = dm.enqueue(request);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            downloading = true;
                            while (downloading) {
                                DownloadManager.Query q = new DownloadManager.Query();
                                q.setFilterById(myDownloadReference);
                                Cursor cursor = dm.query(q);
                                cursor.moveToFirst();
                                int bytes_downloaded = cursor.getInt(cursor
                                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    downloading = false;
                                }
                                final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress((int) dl_progress);

                                    }
                                });
                                cursor.close();
                            }
                        }
                    }).start();
                    BroadcastReceiver rnc = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                                if (downloadId == myDownloadReference) {
                                    DownloadManager.Query query = new DownloadManager.Query();
                                    query.setFilterById(myDownloadReference);
                                    Cursor cursor = dm.query(query);
                                    if (cursor.moveToFirst()) {
                                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                                            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                                            String filename = cursor.getString(filenameIndex);
                                            Toast.makeText(getApplicationContext(), "Téléchargement terminé", Toast.LENGTH_SHORT).show();
                                            lireEnregister2(filename,lien);
                                        } else {
                                            Toast.makeText(getApplicationContext(), " Echec Téléchargement", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    };
                    registerReceiver(rnc, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }catch (IllegalArgumentException e){
                    Toast toast = Toast.makeText(getApplicationContext(), "Lien incorrect", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Connection Internet non détectée", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{
            TextView textView = (TextView)sp_adresse.getSelectedView();
            if(textView!=null) {
                final String lienSpinner = textView.getText().toString();
                afficherRss(lienSpinner);
            }else{
                Toast.makeText(getApplicationContext(),"Spinner Vide",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void afficherRss(String lien){
        fragmentManager= getFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        ListItemFragment item = ListItemFragment.newInstance(lien);
            if (fragmentManager.findFragmentById(R.id.liste_item_fragment_frame) == null) {
                fragmentTransaction.add(R.id.liste_item_fragment_frame, item);
                fragmentTransaction.commit();
            } else {
                fragmentTransaction.replace(R.id.liste_item_fragment_frame, item);
                fragmentTransaction.addToBackStack("changement");
                fragmentTransaction.commit();
            }
    }

    public boolean ConnexionInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean estConnecte= activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        return estConnecte;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDetailSelection(String description) {
        String title = ac.avoirLinkItem("description",description);
        if(title.length()!=0) {
            try {
                Uri uri = Uri.parse(title);
                Intent web= new Intent(Intent.ACTION_VIEW,uri);
                startActivity(web);
            }catch (Exception e) {
                Log.d("erreur", e.getMessage());
            }
        }
    }

    @Override
    public void onTitleSelection(String title) {
        liensauvé=title;
        String link = ac.avoirLinkItem("title",title);
        detailsItemFragment details= detailsItemFragment.newInstance(link);
        fragmentManager= getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT) {
            if (fragmentManager.findFragmentById(R.id.liste_item_fragment_frame) == null) {
                fragmentTransaction.add(R.id.liste_item_fragment_frame, details);
                fragmentTransaction.commit();
            } else {
                fragmentTransaction.replace(R.id.liste_item_fragment_frame, details);
                fragmentTransaction.addToBackStack("debut");
                fragmentTransaction.commit();
            }
        }else{
            if(fragmentManager.findFragmentById(R.id.item_description_frame)==null){
                fragmentTransaction.add(R.id.item_description_frame,details);
                fragmentTransaction.commit();
            }else {
                fragmentTransaction.replace(R.id.item_description_frame, details);
                fragmentTransaction.addToBackStack("debut");
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title",liensauvé);
    }

    //check fonction ajout update;;
    public void lireEnregister2(String file,String lien){
        ArrayList<Node> fichierRssNoeud= new ArrayList<>();
        ArrayList<Node> itemNoeud = new ArrayList<>();
        String dateNouvelle="";
        String titleFRSS="";
        ParserDocXml par= new ParserDocXml(file,fichierRssNoeud,itemNoeud);
        par.parserFichier();
        if(fichierRssNoeud.size()==0){
            Toast.makeText(getApplicationContext(),"Ce fichier n'est pas un fichier RSS!!!",Toast.LENGTH_SHORT).show();
        }else {
            try {
                for (int i = 0; i < fichierRssNoeud.size(); i++) {
                    if (fichierRssNoeud.get(i).getNodeType() == Node.ELEMENT_NODE) {
                        if (fichierRssNoeud.get(i).getNodeName().equals("pubDate") || fichierRssNoeud.get(i).getNodeName().equals("lastBuildDate")) {
                            dateNouvelle = fichierRssNoeud.get(i).getTextContent();
                            break;
                        }
                    }
                }
                for (int i = 0; i < fichierRssNoeud.size(); i++) {
                    if (fichierRssNoeud.get(i).getNodeType() == Node.ELEMENT_NODE) {
                        if (fichierRssNoeud.get(i).getNodeName().equals("title") ) {
                            titleFRSS = fichierRssNoeud.get(i).getTextContent();
                            break;
                        }
                    }
                }
                lienRSS=lien;
                String dateAncienne=ac.avoirDateRss(lien);
                if(dateAncienne==null){
                    ac.ajouterFichierRSS(lien,file,titleFRSS,gestionDate(dateNouvelle));
                    ajouterItem(lien,itemNoeud);
                    afficherRss(lien);
                   adapterSpinner = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,ac.avoirTableRSS(), new String[] {"link"},
                            new int[] {android.R.id.text1});
                    sp_adresse.setAdapter(adapterSpinner);
                }else{
                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date ancienne = dateFormat.parse(dateAncienne); //date deja mise en norme
                    Date nouvelle= dateFormat.parse(gestionDate(dateNouvelle));
                    if(ancienne.compareTo(nouvelle)==0){
                        Log.d("dateEgale","ok");
                        afficherRss(lien);
                    }else if(ancienne.compareTo(nouvelle)==-1){
                        Log.d("dateInf","ok");
                        ac.updateRssDate(lien,gestionDate(dateNouvelle));
                        ac.SupprimerItem(lien,"0");
                        ajouterItem(lien,itemNoeud);
                        adapterSpinner.notifyDataSetChanged();
                        sp_adresse.setAdapter(adapterSpinner);
                        afficherRss(lien);

                    }
                }
            } catch (Exception e) {
                Log.d("exception", e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0){
            if(resultCode==2){
                String res=data.getStringExtra("changement");
                if(res.equals("ok")){
                    adapterSpinner.notifyDataSetChanged();
                    sp_adresse.setAdapter(adapterSpinner);
                }
            }
        }
    }

    public String gestionDate(String date){
        if(!date.contains("null")){
            if(date.contains(",")){
                String[] jour={"Mon, ","Tue, ","Wed, ","Thu, ","Fri, ","Sat, ","Sun, "};
                String[] mois={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                for(int i=0; i<jour.length;i++){
                    if(date.contains(jour[i])){
                        date=date.replace(jour[i],"");
                    }
                }
                for(int i=0; i< mois.length ; i++){
                    if(date.contains(mois[i])){
                        int num = i+1;
                        date=date.replace(mois[i],Integer.toString(num));
                    }
                }
                int longu=date.length()-5;
                String coup=date.substring(longu);
                date=date.replace(coup,"");
                String[] dateFinal=date.split(" ");
                return dateFinal[2]+"-"+dateFinal[1]+"-"+dateFinal[0]+"T"+dateFinal[3];
            }else{
                return date.replace(" ","T");
            }
        }else{
            return "2018-11-15T15:58:12";
        }
    }

    public void ajouterItem(String lien,ArrayList<Node> itemNoeud) throws ParseException {
        for (int i = 0; i < itemNoeud.size(); i++) {
            Element item = (Element) itemNoeud.get(i);
            NodeList itemListePris = item.getChildNodes();
            String description="";
            String title="";
            String link="";
            for (int j = 0; j < itemListePris.getLength(); j++) {
                if (itemListePris.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    if (itemListePris.item(j).getNodeName().equals("link")) {
                        link= itemListePris.item(j).getTextContent();
                    } else if (itemListePris.item(j).getNodeName().equals("description")) {
                        description= itemListePris.item(j).getTextContent();
                    } else if (itemListePris.item(j).getNodeName().equals("title")) {
                        title=itemListePris.item(j).getTextContent();
                    }
                }
            }
            ac.ajouterItem(link,lien,title,description,0);
        }
    }

    public void annuler(View view) {
        if(downloading) {
            annulerTelechargement=true;
            downloading = false;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder= new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("rss").build();
        return new CursorLoader(this,uri, new String[]{"link"}, null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapterSpinner.swapCursor(data);
        adapterSpinner.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapterSpinner.swapCursor(null);
        adapterSpinner.notifyDataSetChanged();
    }
}