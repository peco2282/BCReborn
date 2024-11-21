package peco2282.bcreborn.utils;

import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import peco2282.bcreborn.model.MutableQuad;
import peco2282.bcreborn.model.UvFace;

public final class ModelUtil {
  public static Vector3f readPosition(JsonObject json, String name, int div) {
    var vec = JsonUtil.getVector3f(json, name);
    return new Vector3f(vec.x() / div, vec.y() / div, vec.z() / div);
  }

  public static MutableQuad createFace(Direction face, Vector3f center, Vector3f radius, UvFace uvs) {
    MutableQuad quad = new MutableQuad(-1, face);
    Vector3f centerOfFace = new Vector3f(center);
    Direction.AxisDirection axisDirection = face.getAxisDirection();
    Direction.Axis axis = face.getAxis();
    int x, y, z;
    x = axis == Direction.Axis.X ? axisDirection.getStep() : 0;
    y = axis == Direction.Axis.Y ? axisDirection.getStep() : 0;
    z = axis == Direction.Axis.Z ? axisDirection.getStep() : 0;
    Vector3f faceAdd = new Vector3f(x * radius.x(), y * radius.y(), z * radius.z());
    centerOfFace.add(faceAdd);
    Vector3f faceRadius = new Vector3f(radius);
    if (axisDirection == Direction.AxisDirection.POSITIVE) faceRadius.add(-faceAdd.x(), -faceAdd.y(), -faceAdd.z());
    else faceRadius.add(faceAdd);
    Vector3f[] points = getPoints(centerOfFace, faceRadius);
    return createFace(face, points, uvs)
        .normal(new Vector3f(x, y, z));
  }
  public static <T extends Vector3f> MutableQuad createFace(Direction face, T[] points, UvFace uvs) {
    return createFace(face, points[0], points[1], points[2], points[3], uvs);
  }
  public static MutableQuad createFace(Direction face, Vector3f a, Vector3f b, Vector3f c, Vector3f d, UvFace uvs) {
    MutableQuad quad = new MutableQuad(-1, face);
    if (shouldInvertForRender(face)) {
      quad.vertex_0.setPosition(a).setUV(uvs.minU(), uvs.minV());
      quad.vertex_1.setPosition(b).setUV(uvs.minU(), uvs.maxV());
      quad.vertex_2.setPosition(c).setUV(uvs.maxU(), uvs.maxV());
      quad.vertex_3.setPosition(d).setUV(uvs.maxU(), uvs.minV());
    } else {
      quad.vertex_3.setPosition(a).setUV(uvs.minU(), uvs.minV());
      quad.vertex_2.setPosition(b).setUV(uvs.minU(), uvs.maxV());
      quad.vertex_1.setPosition(c).setUV(uvs.maxU(), uvs.maxV());
      quad.vertex_0.setPosition(d).setUV(uvs.maxU(), uvs.minV());
    }
    return quad;
  }
  public static boolean shouldInvertForRender(Direction face) {
    boolean flip = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
    if (face.getAxis() == Direction.Axis.Z) flip = !flip;
    return flip;
  }
  public static Vector3f[] getPoints(Vector3f centerFace, Vector3f faceRadius) {
    Vector3f[] array = { new Vector3f(centerFace), new Vector3f(centerFace), new Vector3f(centerFace), new Vector3f(centerFace) };
    array[0].add(addOrNegate(faceRadius, false, false));
    array[1].add(addOrNegate(faceRadius, false, true));
    array[2].add(addOrNegate(faceRadius, true, true));
    array[3].add(addOrNegate(faceRadius, true, false));
    return array;
  }
  public static Vector3f addOrNegate(Vector3f coord, boolean u, boolean v) {
    boolean zisv = coord.x != 0 && coord.y == 0;
    float x = coord.x * (u ? 1 : -1);
    float y = coord.y * (v ? -1 : 1);
    float z = coord.z * (zisv ? (v ? -1 : 1) : (u ? 1 : -1));
    return new Vector3f(x, y, z);
  }

}
