package Model;

import java.util.ArrayList;
import java.util.List;

public class NewsModel {

    private String title;
    private List<String> news;

    public NewsModel() {
        this.news = new ArrayList<String>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getNews() {
        return news;
    }

    public void addNews(String news) {
        this.news.add(this.news.size(), news);
    }
}