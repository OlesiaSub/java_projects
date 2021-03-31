package ru.hse.java.streams;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Album {

    private final String name;
    private final List<Track> tracks;
    private final Artist artist;

    public Album(Artist artist, String name, Track... tracks) {
        this.name = name;
        this.tracks = Arrays.asList(tracks);
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public Artist getArtist() {
        return artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(name, album.name) &&
                Objects.equals(tracks, album.tracks) &&
                Objects.equals(artist, album.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tracks, artist);
    }
}