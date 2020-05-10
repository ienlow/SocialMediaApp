package com.socialmediaapp.ui.login;

public class NewsFeed {
    String text;
    String url;
    String date;
    int numLikes;
    int numComments;
    int numReshares;

    public NewsFeed(String s, String url, String date, int numLikes, int numComments, int numReshares) {
        this.text = s;
        this.url = url;
        this.date = date;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.numReshares = numReshares;
    }

    public String getText() {
        return text;
    }

    public String getUrl() { return url; }

    public String getDate() { return date; }

    public int getNumLikes() { return numLikes; }

    public int getNumComments() { return numComments; }

    public int getNumReshares() { return numReshares; }
}
