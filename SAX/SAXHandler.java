package SAX;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SAXHandler extends DefaultHandler {

    private StringBuilder buffer = new StringBuilder();

    boolean image = false;
    boolean item = false;
    boolean printChannelTitle = false;
    boolean printChannelLink = false;
    boolean printChannelDescription = false;
    boolean printItemTitle = false;
    boolean printItemLink = false;
    boolean printItemDescription = false;

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
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (printChannelTitle) {
            System.out.println("Titulo canal:" + buffer.toString());
            printChannelTitle = false;
        }

        if (printChannelLink) {
            System.out.println("URL canal:" + buffer.toString());
            printChannelLink = false;
        }

        if (printChannelDescription) {
            System.out.println("Descripcion canal:" + buffer.toString());
            printChannelDescription = false;
        }

        if (printItemTitle) {
            System.out.println("    Titulo noticia:" + buffer.toString());
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
    }
}