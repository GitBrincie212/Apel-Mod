package net.mcbrincie.apel.lib.util.models;

import com.google.common.io.Files;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModelParserManager {
    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector2f> textureVertices = new ArrayList<>();
    public List<Vector3f> normalVertices = new ArrayList<>();
    public List<List<Vector3f>> drawableLines = new ArrayList<>();
    public List<FaceToken> drawableFaces = new ArrayList<>();

    private static final ObjParser objParser = new ObjParser();
    private static final GltfParser gltfParser = new GltfParser();
    private static final FbxParser fbxParser = new FbxParser();

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

    public void parseFile(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("The supplied file object is not a file");
        }
        String extension = Files.getFileExtension(file.getPath());
        switch (extension) {
            case "obj" -> objParser.parseObjFile(this, file);
            case "gltf" -> gltfParser.parseGltfFile(this, file);
            case "fbx" -> fbxParser.parseFbxFile(this, file);
            default -> throw new UnsupportedOperationException("The parser doesn't support this 3D model file");
        }
    }
}
