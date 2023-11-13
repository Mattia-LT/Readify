package it.unimib.readify.model;

public class Book {

    private String name;
    private String id;
    private String[] topics;

    public Book(String name, String id, String[] topics) {
        this.name = name;
        this.id = id;
        this.topics = topics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }
}
