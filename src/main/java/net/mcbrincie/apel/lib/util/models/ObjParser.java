package net.mcbrincie.apel.lib.util.models;

import net.mcbrincie.apel.Apel;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjParser {
    static public List<Vector3f> vertexTokens = new ArrayList<>();
    static public List<Vector3f> vertexNormalTokens = new ArrayList<>();
    static public List<Vector2f> textureVertexTokens = new ArrayList<>();
    static public List<FaceToken> faceTokens = new ArrayList<>();
    static public List<PolyLineToken> polyLineTokens = new ArrayList<>();

    public static class FaceToken {
        public Vector3f[] vertices;
        public Vector2f[] textureVertices;
        public Vector3f[] normalVertices;

        public FaceToken(
                Vector3f[] vertex_tokens,
                Vector2f[] texture_vertex_tokens,
                Vector3f[] normal_vertex_tokens
        ) {
            this.vertices = vertex_tokens;
            this.textureVertices = texture_vertex_tokens;
            this.normalVertices = normal_vertex_tokens;
        }
    }

    public static class PolyLineToken {
        public Vector3f[] vertices;

        public PolyLineToken(Vector3f[] vertex_tokens) {
            this.vertices = vertex_tokens;
        }
    }

    public void readLine(String line) {
        line = line.replace("#.*", "");
        line = line.replace("o.*", "");
        if (line.isBlank()) return;
        int first_space_index = line.indexOf(" ");
        String token = line.substring(0, first_space_index);
        line = line.substring(first_space_index + 1);
        switch (token) {
            case "v" -> parseVertexToken(line);
            case "vn" -> parseVertexNormalToken(line);
            case "vt" -> parseVertexTextureToken(line);
            case "f" -> parseFaceToken(line);
            case "l" -> parsePolyLineToken(line);
            case "mtllib" -> parseMTLFileDependency(line);
        }
    }

    public void parseVertexToken(String metadata) {
        String[] coords = metadata.split(" ");
        vertexTokens.add(new Vector3f(
                Float.parseFloat(coords[0]),
                Float.parseFloat(coords[1]),
                Float.parseFloat(coords[2])
        ));
    }

    public void parsePolyLineToken(String metadata) {
        String[] verticesString = metadata.split(" ");
        List<Vector3f> arr = new ArrayList<>();
        for (String stringVertex : verticesString) {
            Vector3f vertex = vertexTokens.get(Integer.parseInt(stringVertex) - 1);
            arr.add(vertex);
        }
        polyLineTokens.add(new PolyLineToken(arr.toArray(Vector3f[]::new)));
    }

    public void parseVertexNormalToken(String metadata) {
        String[] coords = metadata.split(" ");
        vertexNormalTokens.add(new Vector3f(
                Float.parseFloat(coords[0]),
                Float.parseFloat(coords[1]),
                Float.parseFloat(coords[2])
        ));
    }

    public void parseVertexTextureToken(String metadata) {
        String[] coords = metadata.split(" ");
        textureVertexTokens.add(new Vector2f(
                Float.parseFloat(coords[0]),
                Float.parseFloat(coords[1])
        ));
    }

    public void parseFaceToken(String metadata) {
        String[] elements = metadata.split(" ");
        for (String element : elements) {
            boolean usingDoubleSlashes = element.contains("//");
            String[] data = element.split("(/|//)");
            List<Vector3f> verticesPair = new ArrayList<>();
            List<Vector3f> normalVerticesPair = new ArrayList<>();
            List<Vector2f> textureVerticesPair = new ArrayList<>();
            int pair_normal_vertex_index = usingDoubleSlashes ? 1 : 2;
            if (!usingDoubleSlashes) {
                int textureVertexIndex = Integer.parseInt(data[1]);
                textureVerticesPair.add(textureVertexTokens.get(textureVertexIndex - 1));
            }
            int vertexIndex = Integer.parseInt(data[0]);
            int vertexNormalIndex = Integer.parseInt(data[pair_normal_vertex_index]);
            verticesPair.add(vertexTokens.get(vertexIndex - 1));
            normalVerticesPair.add(vertexNormalTokens.get(vertexNormalIndex));
            faceTokens.add(new FaceToken(
                    verticesPair.toArray(Vector3f[]::new),
                    textureVerticesPair.toArray(Vector2f[]::new),
                    normalVerticesPair.toArray(Vector3f[]::new)
            ));
        }
    }

    public void parseObjFile(File model_file) {
        try {
            Scanner myReader = new Scanner(model_file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                this.readLine(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            Apel.LOGGER.error("Object Model File Has Not Been Found");
            e.printStackTrace();
        }
    }

    public void parseMTLFileDependency(String line) {
        // Work In Progress
    }
}