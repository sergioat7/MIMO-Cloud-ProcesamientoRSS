package SAX;

import java.net.URL;

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

class SAXParser {

    private static final String uri = "https://www.europapress.es/rss/rss.aspx";

    public static void main(String[] args) {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            javax.xml.parsers.SAXParser saxParser = factory.newSAXParser();

            InputSource input = new InputSource(new URL(uri).openStream());
            SAXHandler handler = new SAXHandler();

            saxParser.parse(input, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}