package net.mcbrincie.apel.lib.util.models;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

import java.io.File;

public class ModelParserManager implements ModelParser {
    private static final ImmutableMap<String, ModelParser> PARSERS_BY_EXT = parsersMap();

    private static ImmutableMap<String, ModelParser> parsersMap() {
        ImmutableMap.Builder<String, ModelParser> builder = ImmutableMap.builder();
        builder.put("obj", new ObjParser());
        builder.put("fbx", new FbxParser());
        builder.put("gltf", new GltfParser());
        return builder.build();
    }

    @Override
    public ObjModel parse(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("The supplied file object is not a file");
        }
        String extension = Files.getFileExtension(file.getPath());
        ModelParser parser = PARSERS_BY_EXT.get(extension);
        if (parser == null) {
            throw new UnsupportedOperationException("The parser doesn't support this 3D model file");
        }
        return parser.parse(file);
    }
}
