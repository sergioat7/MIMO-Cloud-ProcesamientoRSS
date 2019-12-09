package DOM;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

            Document outputDoc = db.newDocument();

            Element channelNode = (Element) doc.getElementsByTagName("channel").item(0);
            String channelTitle = channelNode.getElementsByTagName("title").item(0).getTextContent();

            Element channelNews = outputDoc.createElement("noticias");
            channelNews.setAttribute("canal", channelTitle);
            outputDoc.appendChild(channelNews);
            System.out.println("Titulo canal:" + channelTitle);
            System.out.println("URL canal:" + channelNode.getElementsByTagName("link").item(0).getTextContent());
            System.out.println(
                    "Descripcion canal:" + channelNode.getElementsByTagName("description").item(0).getTextContent());

            NodeList items = channelNode.getElementsByTagName("item");
            Node currentNode;
            Element currentItem;
            for (int i = 0; i < items.getLength(); i++) {

                currentNode = items.item(i);

                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    currentItem = (Element) items.item(i);
                    int id = i + 1;
                    String newsTitle = currentItem.getElementsByTagName("title").item(0).getTextContent();

                    Element news = outputDoc.createElement("noticia");
                    channelNews.appendChild(news);
                    news.appendChild(outputDoc.createTextNode(newsTitle));

                    System.out.println("Noticia " + id);
                    System.out.println("   Titulo:" + newsTitle);
                    System.out.println("   URL:" + currentItem.getElementsByTagName("link").item(0).getTextContent());
                    System.out.println("   Descripcion:"
                            + currentItem.getElementsByTagName("description").item(0).getTextContent());
                    System.out.println("   Fecha de publicacion:"
                            + currentItem.getElementsByTagName("pubDate").item(0).getTextContent());
                    System.out.println(
                            "   Categoria:" + currentItem.getElementsByTagName("category").item(0).getTextContent());
                }
            }

            generateXMLFile(outputDoc, "DOM/noticias_" + channelTitle + ".xml");
            getXMLfromJson("DOM/noticias_" + channelTitle);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    private static void generateXMLFile(Document doc, String filename) {

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(filename)));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private static void getXMLfromJson(String filename) {

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filename + ".xml"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String XML = contentBuilder.toString();

        try {
            JSONObject xmlJSONObj = JSONML.toJSONObject(XML);

            try (FileWriter file = new FileWriter(filename + ".json")) {

                file.write(xmlJSONObj.toString());
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}