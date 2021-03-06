package main.ru.hse.projects.java.maze;

import java.util.*;

public class MazeSolver {

    public int[][] matrix;
    public int[] parents;
    public boolean[] used;
    public int[][] verticesCoordinates;

    public IndexPair startPos;
    public Integer startIdx;
    public Integer curIndex = 0;

    public final ArrayList<Integer> terminals = new ArrayList<>();
    public final ArrayList<ArrayList<Integer>> edges = new ArrayList<>();
    public ArrayList<IndexPair> path = new ArrayList<>();
    public final Map<Integer, Vertex> verticesIndices = new HashMap<>();

    public void readInput() {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int m = scan.nextInt();

        if (n == 0 && m == 0) {
            throw new IllegalArgumentException("Matrix does not contain any elements.");
        }

        initializeFields(n, m);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = scan.nextInt();
                if (matrix[i][j] != 1 && matrix[i][j] != 0) {
                    throw new IllegalArgumentException("Incorrect maze format.");
                }
            }
        }
        startPos = new IndexPair(scan.nextInt(), scan.nextInt());

        if (startPos.fst >= n || startPos.snd >= m || matrix[startPos.fst][startPos.snd] == 1) {
            throw new IllegalArgumentException("Impossible starting position");
        }
        scan.close();
    }

    public void initializeFields(int n, int m) {
        matrix = new int[n][m];
        used = new boolean[n * m];
        parents = new int[n * m];
        verticesCoordinates = new int[n][m];
        for (int i = 0; i < n * m; i++) {
            edges.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                verticesCoordinates[i][j] = -1;
            }
        }
    }

    public void traverseMatrix() {
        int vertDim = matrix.length;
        int horDim = matrix[0].length;
        for (int i = 0; i < vertDim; i++) {
            for (int j = 0; j < horDim; j++) {
                if (matrix[i][j] == 0) {
                    boolean terminal = i == 0 || i ==vertDim - 1 || j == horDim - 1 || j == 0;
                    Vertex current = new Vertex(curIndex, i, j, terminal);
                    verticesCoordinates[i][j] = curIndex;
                    verticesIndices.put(curIndex, current);
                    curIndex++;
                }
            }
        }
    }

    private void addEdges(IndexPair idx, int i) {
        if (idx.fst >= 0 && idx.snd >= 0 && idx.fst < verticesCoordinates.length
                && idx.snd < verticesCoordinates[0].length && verticesCoordinates[idx.fst][idx.snd] != -1) {
            Vertex curVertex = verticesIndices.get(verticesCoordinates[idx.fst][idx.snd]);
            edges.get(i).add(curVertex.index);
        }
    }

    public void constructGraph() {
        traverseMatrix();
        for (int i = 0; i < curIndex; i++) {
            IndexPair current = new IndexPair(verticesIndices.get(i).fstPos, verticesIndices.get(i).sndPos);
            if (current.fst == startPos.fst && current.snd == startPos.snd) {
                startIdx = i;
            }
            IndexPair left = new IndexPair(current.fst, current.snd - 1);
            IndexPair right = new IndexPair(current.fst, current.snd + 1);
            IndexPair up = new IndexPair(current.fst - 1, current.snd);
            IndexPair down = new IndexPair(current.fst + 1, current.snd);
            addEdges(left, i);
            addEdges(right, i);
            addEdges(up, i);
            addEdges(down, i);
        }
    }

    public void getPath() {
        dfs(startIdx);
        if (terminals.isEmpty()) {
            System.out.println("No path was found.");
            return;
        }
        int terminal = terminals.get(0);
        for (int i = terminal; i != startIdx; i = parents[i]) {
            path.add(new IndexPair(verticesIndices.get(i).fstPos, verticesIndices.get(i).sndPos));
        }
        path.add(new IndexPair(verticesIndices.get(startIdx).fstPos, verticesIndices.get(startIdx).sndPos));
        Collections.reverse(path);
        writeOutput();
    }

    public void dfs(int vertex) {
        if (vertex != startIdx && verticesIndices.get(vertex).terminal) {
            terminals.add(vertex);
            return;
        }
        used[vertex] = true;
        for (int i : edges.get(vertex)) {
            if (!used[i]) {
                dfs(i);
                parents[i] = vertex;
            }
        }
    }

    private boolean getDirection(int i, ArrayList<IndexPair> path) {
        if (path.get(i).fst == path.get(i + 1).fst && path.get(i).snd == path.get(i + 1).snd - 1) {
            System.out.println("Go to the right");
        } else if (path.get(i).fst == path.get(i + 1).fst && path.get(i).snd == path.get(i + 1).snd + 1) {
            System.out.println("Go to the left");
        } else if (path.get(i).fst == path.get(i + 1).fst - 1 && path.get(i).snd == path.get(i + 1).snd) {
            System.out.println("Go down");
        } else if (path.get(i).fst == path.get(i + 1).fst + 1 && path.get(i).snd == path.get(i + 1).snd) {
            System.out.println("Go up");
        } else {
            return false;
        }
        return true;
    }

    public void writeOutput() {
        System.out.print("Your starting point is {");
        System.out.print(startPos.fst);
        System.out.print("; ");
        System.out.print(startPos.snd);
        System.out.println("}");

        for (int i = 0; i < path.size() - 1; i++) {
            boolean correctDir = getDirection(i, path);
            if (!correctDir) {
                throw new RuntimeException("Impossible path was found.");
            }
        }
        System.out.println("Congratulations! You found the way out!");
    }
}