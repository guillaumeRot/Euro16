package com.euro16.Utils;

import android.os.AsyncTask;

import com.euro16.Utils.RowsChoix.RowActualite;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Guillaume on 20/05/2016.
 */
public class XMLDataParser extends AsyncTask<Void, Void, ArrayList<RowActualite>> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<RowActualite> doInBackground(Void... params) {

        ArrayList<RowActualite> alActus = new ArrayList<>();
        try {

            URL url = new URL("http://fr.uefa.com/rssfeed/uefaeuro/rss.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");
            for (int i = 0; i < 20; i++) {

                Node node = nodeList.item(i);

                Element firstElement = (Element) node;

                NodeList titleList = firstElement.getElementsByTagName("title");
                Element titleElement = (Element) titleList.item(0);
                titleList = titleElement.getChildNodes();

                NodeList descList = firstElement.getElementsByTagName("description");
                Element descElement = (Element) descList.item(0);
                descList = descElement.getChildNodes();

                NodeList dateList = firstElement.getElementsByTagName("pubDate");
                Element dateElement = (Element) dateList.item(0);
                dateList = dateElement.getChildNodes();

                String datePub = dateList.item(0).getNodeValue();
                String title = titleList.item(0).getNodeValue();
                String desc = android.text.Html.fromHtml(descList.item(0).getNodeValue()).toString();

                alActus.add(new RowActualite(datePub, title, desc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alActus;
    }

    @Override
    protected void onPostExecute(ArrayList<RowActualite> result) {
        super.onPostExecute(result);
    }

}
