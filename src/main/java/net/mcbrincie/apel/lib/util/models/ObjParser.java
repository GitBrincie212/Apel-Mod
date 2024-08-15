package net.mcbrincie.apel.lib.util.models;

import com.google.common.collect.ImmutableList;
import net.mcbrincie.apel.Apel;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Parses an *.obj file into an {@link ObjModel}.
 */
public class ObjParser implements ModelParser {

    private Vector3f parseVertexToken(String metadata) {
        String[] coords = metadata.split(" ");
        return new Vector3f(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]), Float.parseFloat(coords[2]));
    }

    private ObjModel.PolyLine parsePolyLineToken(String metadata, List<Vector3f> vertices) {
        String[] indices = metadata.split(" ");
        ImmutableList.Builder<Vector3f> positions = ImmutableList.builder();
        for (String index : indices) {
            Vector3f position = vertices.get(Integer.parseInt(index) - 1);
            positions.add(position);
        }
        return new ObjModel.PolyLine(positions.build());
    }

    private Vector3f parseVertexNormalToken(String metadata) {
        String[] coords = metadata.split(" ");
        return new Vector3f(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]), Float.parseFloat(coords[2]));
    }

    private Vector2f parseVertexTextureToken(String metadata) {
        String[] coords = metadata.split(" ");
        return new Vector2f(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
    }

    private ObjModel.Face parseFaceToken(String metadata, List<Vector3f> positions, List<Vector2f> vertexTextures, List<Vector3f> vertexNormals) {
        String[] elements = metadata.split(" ");
        ImmutableList.Builder<ObjModel.Vertex> vertices = ImmutableList.builder();
        for (String element : elements) {
            String[] indices = element.split("(/|//)");
            Vector3f position = positions.get(Integer.parseInt(indices[0]) - 1);
            Vector2f textureCoordinates;
            if (indices[1].isEmpty()) {
                textureCoordinates = null;
            } else {
                textureCoordinates = vertexTextures.get(Integer.parseInt(indices[1]) - 1);
            }
            Vector3f normal = vertexNormals.get(Integer.parseInt(indices[2]) - 1);
            ObjModel.Vertex vertex = new ObjModel.Vertex(position, textureCoordinates, normal);
            vertices.add(vertex);
        }
        return new ObjModel.Face(vertices.build());
    }

    /**
     * Read the file indicated by {@code modelFile} and load it into an {@link ObjModel} for use in a
     * {@code ParticleModel}.
     *
     * @param modelFile a File containing the model
     * @return an ObjModel instance
     */
    @Override
    public ObjModel parse(File modelFile) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> vertexTextures = new ArrayList<>();
        List<Vector3f> vertexNormals = new ArrayList<>();
        ImmutableList.Builder<ObjModel.Face> faces = ImmutableList.builder();
        ImmutableList.Builder<ObjModel.PolyLine> polyLines = ImmutableList.builder();
        try {
            Scanner myReader = new Scanner(modelFile);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                int first_space_index = line.indexOf(" ");
                String token = line.substring(0, first_space_index);
                line = line.substring(first_space_index + 1);
                switch (token) {
                    case "v" -> vertices.add(parseVertexToken(line));
                    case "vn" -> vertexNormals.add(parseVertexNormalToken(line));
                    case "vt" -> vertexTextures.add(parseVertexTextureToken(line));
                    case "f" -> faces.add(parseFaceToken(line, vertices, vertexTextures, vertexNormals));
                    case "l" -> polyLines.add(parsePolyLineToken(line, vertices));
                    case "mtllib" -> parseMTLFileDependency(line);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            Apel.LOGGER.error("Object Model File Has Not Been Found");
        }
        return new ObjModel(faces.build(), polyLines.build());
    }

    private void parseMTLFileDependency(String line) {
        // Work In Progress
    }
}