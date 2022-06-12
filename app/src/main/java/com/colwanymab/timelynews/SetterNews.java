package com.colwanymab.timelynews;

public class SetterNews {

    public SetterNews() {
    }

    private String key, writerUid, editorUid, headline, story, link, tag;
    private long readTime, timeStamp;
    private boolean read, free;

    public SetterNews(String key, String writerUid, String editorUid, String headline,
                      String story, long readTime, long timeStamp, boolean read, String link, String tag, boolean free) {
        this.key = key;
        this.writerUid = writerUid;
        this.editorUid = editorUid;
        this.headline = headline;
        this.story = story;
        this.readTime = readTime;
        this.timeStamp = timeStamp;
        this.read = read;
        this.link = link;
        this.tag = tag;
        this.free = free;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWriterUid() {
        return writerUid;
    }

    public void setWriterUid(String writerUid) {
        this.writerUid = writerUid;
    }

    public String getEditorUid() {
        return editorUid;
    }

    public void setEditorUid(String editorUid) {
        this.editorUid = editorUid;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
