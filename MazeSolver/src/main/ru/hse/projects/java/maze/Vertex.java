package main.ru.hse.projects.java.maze;

public class Vertex {
    public int index;
    public int fstPos;
    public int sndPos;
    public boolean terminal;

    public Vertex(int idx, int first, int second, boolean term) {
        index = idx;
        fstPos = first;
        sndPos = second;
        terminal = term;
    }
}
