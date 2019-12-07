package DOM;

import java.io.IOException;
import java.net.URL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

            Element channelNode = (Element) doc.getElementsByTagName("channel").item(0);

            String channelTitle = channelNode.getElementsByTagName("title").item(0).getTextContent();
            System.out.println("Titulo canal:" + channelTitle);
            System.out.println("URL canal:" + channelNode.getElementsByTagName("link").item(0).getTextContent());
            System.out.println("Descripcion canal:" + channelNode.getElementsByTagName("description").item(0).getTextContent());
            
            NodeList items = channelNode.getElementsByTagName("item");
            Element currentItem;
            for (int i=0; i<items.getLength(); i++) {
                currentItem = (Element) items.item(i);
                int id = i+1;
                System.out.println("Noticia " + id);
                System.out.println("   Titulo:" + currentItem.getElementsByTagName("title").item(0).getTextContent());
                System.out.println("   URL:" + currentItem.getElementsByTagName("link").item(0).getTextContent());
                System.out.println("   Descripcion:" + currentItem.getElementsByTagName("description").item(0).getTextContent());
                System.out.println("   Fecha de publicacion:" + currentItem.getElementsByTagName("pubDate").item(0).getTextContent());
                System.out.println("   Categoria:" + currentItem.getElementsByTagName("category").item(0).getTextContent());
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

}