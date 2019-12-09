package SAX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import Model.NewsModel;

public class SAXHandler extends DefaultHandler {

    private TransformerHandler transformerHandler;
    private String filename = "SAX/noticias_";
    private NewsModel model;

    private StringBuilder buffer = new StringBuilder();

    boolean image = false;
    boolean item = false;
    boolean printChannelTitle = false;
    boolean printChannelLink = false;
    boolean printChannelDescription = false;
    boolean printItemTitle = false;
    boolean printItemLink = false;
    boolean printItemDescription = false;
    boolean printItemPubdate = false;

    public SAXHandler() throws TransformerConfigurationException, SAXException {
        SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        transformerHandler = saxTransformerFactory.newTransformerHandler();
        model = new NewsModel();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes att) throws SAXException {

        if (qName.equalsIgnoreCase("image")) {
            image = true;
        }

        if (qName.equalsIgnoreCase("item")) {
            item = true;
        }

        if (qName.equalsIgnoreCase("title") && (!image || item)) {
            printChannelTitle = !item;
            printItemTitle = item;
            buffer.delete(0, buffer.length());
        }

        if (qName.equalsIgnoreCase("link") && (!image || item)) {
            printChannelLink = !item;
            printItemLink = item;
            buffer.delete(0, buffer.length());
        }

        if (qName.equalsIgnoreCase("description") && (!image || item)) {
            printChannelDescription = !item;
            printItemDescription = item;
            buffer.delete(0, buffer.length());
        }

        if (qName.equalsIgnoreCase("pubDate") && item) {
            printItemPubdate = true;
            buffer.delete(0, buffer.length());
        }

        if (qName.equalsIgnoreCase("category")) {
            buffer.delete(0, buffer.length());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("channel")) {
            this.generateXMLFile();
        }

        if (printChannelTitle) {
            System.out.println("Titulo canal:" + buffer.toString());
            model.setTitle(buffer.toString());
            printChannelTitle = false;
        }

        if (printChannelLink) {
            System.out.println("URL canal:" + buffer.toString());
            printChannelLink = false;
        }

        if (printChannelDescription) {
            System.out.println("Descripcion canal:" + buffer.toString());
            printChannelDescription = false;
            System.out.println("Noticias:");
        }

        if (printItemTitle) {
            System.out.println("    Titulo noticia:" + buffer.toString());
            model.addNews(buffer.toString());
            printItemTitle = false;
        }

        if (printItemLink) {
            System.out.println("    URL noticia:" + buffer.toString());
            printItemLink = false;
        }

        if (printItemDescription) {
            System.out.println("    Descripcion noticia:" + buffer.toString());
            printItemDescription = false;
        }

        if (printItemPubdate) {
            System.out.println("    Fecha de publicacion:" + buffer.toString());
            printItemPubdate = false;
        }

        if (qName.equalsIgnoreCase("category")) {
            System.out.println("    Categoria:" + buffer.toString());
        }
    }

    private void generateXMLFile() {

        File newXML = new File(filename + model.getTitle() + ".xml");
        FileWriter fileWriter;
        Result result;
        Transformer transformer;

        try {
            fileWriter = new FileWriter(newXML);
            result = new StreamResult(fileWriter);
            transformerHandler.setResult(result);
            this.createDocumentStructure();

            transformer = transformerHandler.getTransformer();
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void createDocumentStructure() throws SAXException {

        transformerHandler.startDocument();
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "", "canal", "String", model.getTitle());
        transformerHandler.startElement("", "", "noticias", atts);
        List<String> news = model.getNews();
        for (int i = 0; i < news.size(); i++) {
            transformerHandler.startElement("", "", "noticia", new AttributesImpl());
            transformerHandler.characters(news.get(i).toCharArray(), 0, news.get(i).length());
            transformerHandler.endElement("", "", "noticia");
        }
        transformerHandler.endElement("", "", "noticias");
        transformerHandler.endDocument();
    }
}