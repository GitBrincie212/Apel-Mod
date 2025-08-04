package net.mcbrincie.apel.lib.util.models;

import com.google.common.collect.ImmutableList;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

/**
 * Represents a read-only *.obj model
 *
 * @param faces What are the faces of the model
 * @param polyLines What are the polylines of the model
 */
public record ObjModel(ImmutableList<Face> faces, ImmutableList<PolyLine> polyLines) {
    public record Vertex(Vector3f position, Vector2f textureCoordinates, Vector3f normal) {
    }

    public record Face(ImmutableList<Vertex> vertices) {
    }

    public record PolyLine(ImmutableList<Vector3f> positions) {
    }
}
