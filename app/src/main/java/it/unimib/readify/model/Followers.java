package it.unimib.readify.model;

import java.util.List;

public class Followers {
    int counter;
    List<Integer> followers;

    public Followers() {}

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<Integer> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Integer> followers) {
        this.followers = followers;
    }
}
