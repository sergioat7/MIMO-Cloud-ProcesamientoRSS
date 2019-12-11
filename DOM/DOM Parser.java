package DOM;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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

import Model.NewsModel;

class DOMParser {

    private static String filename = "DOM/noticias_";
    private static String xmlExtension = ".xml";
    private static String jsonExtension = ".json";

    private NewsModel model;

    public DOMParser() {
        this.model = new NewsModel();
    }

    /* Parsear un documento XML para obtener su árbol DOM */
    private Document obtenerDOM(String fileURL) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();
        if (!(fileURL.isEmpty()))
            doc = db.parse(new URL(fileURL).openStream());
        return doc;
    }

    /* Mostrar contenido de un documento XML a través de su árbol DOM */
    private void mostrarDOM(Document doc) {

        Element channelNode = (Element) doc.getElementsByTagName("channel").item(0);
        String channelTitle = getTextNodeContent(channelNode, "title");

        System.out.println(
                " - Titulo canal:" + channelTitle + "\n" +
                " - URL canal:" + getTextNodeContent(channelNode, "link") + "\n" +
                " - Descripcion canal:" + getTextNodeContent(channelNode, "description"));

        this.model.setTitle(channelTitle);

        NodeList items = channelNode.getElementsByTagName("item");
        Node currentNode;
        Element currentItem;
        for (int i = 0; i < items.getLength(); i++) {

            currentNode = items.item(i);

            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                currentItem = (Element) items.item(i);
                int id = i + 1;
                String newsTitle = getTextNodeContent(currentItem, "title");

                System.out.println(" - Noticia " + id + "\n" +
                                    "   * Titulo:" + newsTitle + "\n" +
                                    "   * URL:"  + getTextNodeContent(currentItem, "link") + "\n" +
                                    "   * Descripcion:" + getTextNodeContent(currentItem, "description") + "\n" +
                                    "   * Fecha de publicacion:" + getTextNodeContent(currentItem, "pubDate") + "\n" +
                                    "   * Categoria:" + getTextNodeContent(currentItem, "category"));

                this.model.addNews(newsTitle);
            }
        }
    }

    /* Generar un documento XML a partir de un árbol DOM */
    private void generateXMLFile()
            throws TransformerException, IOException, SAXException, ParserConfigurationException {

        Document doc = this.obtenerDOM("");

        Element channelNews = doc.createElement("noticias");
        String channelTitle = this.model.getTitle();
        channelNews.setAttribute("canal", channelTitle);
        doc.appendChild(channelNews);

        List<String> news = this.model.getNews();
        for (int i = 0; i < news.size(); i++) {
            Element singleNews = doc.createElement("noticia");
            channelNews.appendChild(singleNews);
            singleNews.appendChild(doc.createTextNode(news.get(i)));
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(new File(filename + channelTitle + xmlExtension)));
    }

    /* Generar un documento JSON a partir de un documento XML */
    private void generateJSONFileFromXMLFile() {

        String channelTitle = this.model.getTitle();
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filename + channelTitle + xmlExtension),
                StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String XML = contentBuilder.toString();

        try {
            JSONObject xmlJSONObj = JSONML.toJSONObject(XML);

            try (FileWriter file = new FileWriter(filename + channelTitle + jsonExtension)) {

                file.write(xmlJSONObj.toString());
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Obtener el contenido de un nodo de texto */
    private static String getTextNodeContent(Element elem, String tag) {
        return elem.getElementsByTagName(tag).item(0).getTextContent();
    }



    public static void main(String[] args) {

        try {
            DOMParser parser = new DOMParser();

            Document doc = parser.obtenerDOM("https://www.europapress.es/rss/rss.aspx");

            parser.mostrarDOM(doc);

            parser.generateXMLFile();
            parser.generateJSONFileFromXMLFile();
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }
}