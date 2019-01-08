package com.example.andressito.mplrss;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParserDocXml extends AppCompatActivity {

    private String file;
    private ArrayList<Node> fichierRssNoeud;
    private ArrayList<Node> itemNoeud;

    public ParserDocXml(String file,ArrayList<Node> fichierRssNoeud, ArrayList<Node> itemNoeud){
         this.file=file;
         this.fichierRssNoeud=fichierRssNoeud;
         this.itemNoeud= itemNoeud;
    }

    public void parserFichier(){
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(new File(file));
            final Element racine= doc.getDocumentElement();
            if(racine.getNodeName().equals("rss")){
                final NodeList liste= racine.getChildNodes();
                for(int i=0; i<liste.getLength();i++){
                    if(liste.item(i).getNodeType()==Node.ELEMENT_NODE){
                        NodeList listeitem = liste.item(i).getChildNodes();
                        for(int j=0; j<listeitem.getLength();j++){
                            if(listeitem.item(j).getNodeType() == Node.ELEMENT_NODE){
                                if(listeitem.item(j).getNodeName().equals("item")){
                                    itemNoeud.add(listeitem.item(j));
                                }else{
                                    fichierRssNoeud.add(listeitem.item(j));
                                }
                            }
                        }
                    }
                }
                Log.d("size",itemNoeud.size()+" ");
            }else{
                Toast.makeText(getApplicationContext(), "ce fichier n'est pas un fichier RSS", Toast.LENGTH_SHORT).show();
            }
        } catch (ParserConfigurationException e) {
            Log.d("ParserConf EXP", e.getMessage());
        } catch (SAXException e){
            Log.d("SAXEXP ", e.getMessage());

        } catch (IOException e){
            Log.d("IOEXP ", e.getMessage());

        }
    }
}
