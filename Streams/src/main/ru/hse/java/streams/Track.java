package ru.hse.java.streams;

import java.util.Objects;

public class Track {

    private final String name;
    private final int rating;

    public Track(String name, int rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return rating == track.rating &&
                Objects.equals(name, track.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rating);
    }
}