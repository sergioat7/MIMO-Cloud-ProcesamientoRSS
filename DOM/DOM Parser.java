package DOM;

import java.io.IOException;
import java.net.URL;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

class DOMParser {

    private static final String uri = "https://www.europapress.es/rss/rss.aspx";

    public static void main(String[] args) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document doc;

        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new URL(uri).openStream());
            doc.getDocumentElement().normalize();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

}