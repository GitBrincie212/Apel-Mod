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
    public void readLine(ModelParserManager manager, String line) {
        line = line.replace("#.*", "");
        line = line.replace("o.*", "");
        if (line.isBlank()) return;
        int first_space_index = line.indexOf(" ");
        String token = line.substring(0, first_space_index);
        line = line.substring(first_space_index + 1);
        switch (token) {
            case "v" -> parseVertexToken(manager, line);
            case "vn" -> parseVertexNormalToken(manager, line);
            case "vt" -> parseVertexTextureToken(manager, line);
            case "f" -> parseFaceToken(manager, line);
            case "l" -> parsePolyLineToken(manager, line);
            case "mtllib" -> parseMTLFileDependency(line);
        }
    }

    public void parseVertexToken(ModelParserManager manager, String metadata) {
        String[] coords = metadata.split(" ");
        manager.vertices.add(new Vector3f(
                Float.parseFloat(coords[0]),
                Float.parseFloat(coords[1]),
                Float.parseFloat(coords[2])
        ));
    }

    public void parsePolyLineToken(ModelParserManager manager, String metadata) {
        String[] verticesString = metadata.split(" ");
        List<Vector3f> listOfVertices = new ArrayList<>();
        for (String stringVertex : verticesString) {
            Vector3f vertex = manager.vertices.get(Integer.parseInt(stringVertex) - 1);
            listOfVertices.add(vertex);
        }
        manager.drawableLines.add(listOfVertices);
    }

    public void parseVertexNormalToken(ModelParserManager manager, String metadata) {
        String[] coords = metadata.split(" ");
        manager.normalVertices.add(new Vector3f(
                Float.parseFloat(coords[0]),
                Float.parseFloat(coords[1]),
                Float.parseFloat(coords[2])
        ));
    }

    public void parseVertexTextureToken(ModelParserManager manager, String metadata) {
        String[] coords = metadata.split(" ");
        manager.textureVertices.add(new Vector2f(
                Float.parseFloat(coords[0]),
                Float.parseFloat(coords[1])
        ));
    }

    public void parseFaceToken(ModelParserManager manager, String metadata) {
        String[] elements = metadata.split(" ");
        List<Vector3f> verticesPair = new ArrayList<>();
        List<Vector3f> normalVerticesPair = new ArrayList<>();
        List<Vector2f> textureVerticesPair = new ArrayList<>();
        for (String element : elements) {
            boolean usingDoubleSlashes = element.contains("//");
            String[] data = element.split("(/|//)");
            int pair_normal_vertex_index = usingDoubleSlashes ? 1 : 2;
            if (!usingDoubleSlashes) {
                int textureVertexIndex = Integer.parseInt(data[1]);
                textureVerticesPair.add(manager.textureVertices.get(textureVertexIndex - 1));
            }
            int vertexIndex = Integer.parseInt(data[0]);
            int vertexNormalIndex = Integer.parseInt(data[pair_normal_vertex_index]);
            verticesPair.add(manager.vertices.get(vertexIndex - 1));
            normalVerticesPair.add(manager.normalVertices.get(vertexNormalIndex));
        }
        manager.drawableFaces.add(new ModelParserManager.FaceToken(
                verticesPair.toArray(Vector3f[]::new),
                textureVerticesPair.toArray(Vector2f[]::new),
                normalVerticesPair.toArray(Vector3f[]::new)
        ));
    }

    public void parseObjFile(ModelParserManager manager, File model_file) {
        manager.normalVertices.add(new Vector3f());
        try {
            Scanner myReader = new Scanner(model_file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                this.readLine(manager, data);
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