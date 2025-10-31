package io.cuber.vdcraft.display;

import com.badlogic.gdx.math.Vector3;
import io.cuber.vdcraft.terrain.Cube;
import io.cuber.vdcraft.terrain.GameMap;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private static final int CUBE_SIZE = 1;
    private static final int POSITION_COMPONENTS = 3;
    private static final int NORMAL_COMPONENTS = 3;
    private static final int UV_COMPONENTS = 2;
    private static final int VERTEX_COMPONENTS = POSITION_COMPONENTS + NORMAL_COMPONENTS + UV_COMPONENTS;
    public static final int VERTS_PER_SIDE = 4;
    public static final int CUBE_SIDES = 6;

    private final int height;
    private final int width;
    private final int depth;

    private final float[] meshData;
    private final Cube[] cubes;
    private final Vector3 origin = new Vector3();
    private final List<Cube> visibleCubes = new ArrayList<>();

    private enum Face {
        TOP(0, 1, 0, 0, 1, 2, 3),
        BOTTOM(0, -1, 0, 4, 5, 6, 7),
        FRONT(0, 0, 1, 3, 7, 6, 2),
        BACK(0, 0, -1, 0, 4, 5, 1),
        LEFT(-1, 0, 0, 1, 5, 7, 3),
        RIGHT(1, 0, 0, 0, 2, 6, 4);

        final Vector3 normal;
        final int[] corners;

        Face(float nx, float ny, float nz, int c1, int c2, int c3, int c4) {
            this.normal = new Vector3(nx, ny, nz);
            this.corners = new int[]{c1, c2, c3, c4};
        }
    }

    private static final Vector3[] CUBE_CORNERS = {
        new Vector3(0, CUBE_SIZE, 0), new Vector3(0, 0, 0), new Vector3(CUBE_SIZE, CUBE_SIZE, 0), new Vector3(CUBE_SIZE, 0, 0),
        new Vector3(0, CUBE_SIZE, CUBE_SIZE), new Vector3(0, 0, CUBE_SIZE), new Vector3(CUBE_SIZE, CUBE_SIZE, CUBE_SIZE), new Vector3(CUBE_SIZE, 0, CUBE_SIZE)
    };

    public Section(int width, int height, int depth, int x, int y, int z) {
        this.height = height;
        this.width = width;
        this.depth = depth;
        this.meshData = new float[width * height * depth * VERTS_PER_SIDE * CUBE_SIDES * VERTEX_COMPONENTS];
        this.cubes = new Cube[width * height * depth];
        this.origin.set(x, y, z);
    }


  public void set(int x, int y, int z, Cube cube) {
    cubes[x + z * width + y * width * depth] = cube;
  }

  public Cube get(int x, int y, int z) {
    return cubes[x + z * width + y * width * depth];
  }







    private int buildFace(int vertexOffset, float x, float y, float z, Face face, com.badlogic.gdx.graphics.g2d.TextureRegion texture) {
        Vector3 normal = face.normal;
        float u1 = texture.getU();
        float v1 = texture.getV();
        float u2 = texture.getU2();
        float v2 = texture.getV2();

        for (int corner : face.corners) {
            Vector3 cornerPos = CUBE_CORNERS[corner];
            meshData[vertexOffset++] = origin.x + x + cornerPos.x;
            meshData[vertexOffset++] = origin.y + y + cornerPos.y;
            meshData[vertexOffset++] = origin.z + z + cornerPos.z;
            meshData[vertexOffset++] = normal.x;
            meshData[vertexOffset++] = normal.y;
            meshData[vertexOffset++] = normal.z;
            if (corner == face.corners[0]) {
                meshData[vertexOffset++] = u1;
                meshData[vertexOffset++] = v1;
            } else if (corner == face.corners[1]) {
                meshData[vertexOffset++] = u1;
                meshData[vertexOffset++] = v2;
            } else if (corner == face.corners[2]) {
                meshData[vertexOffset++] = u2;
                meshData[vertexOffset++] = v2;
            } else {
                meshData[vertexOffset++] = u2;
                meshData[vertexOffset++] = v1;
            }
        }
        return vertexOffset;
    }

    public int buildMesh() {
        int vertexOffset = 0;
        visibleCubes.clear();

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    Cube cube = get(x, y, z);
                    if (cube == null) continue;

                    boolean rendered = false;

                    if (y == height - 1 || get(x, y + 1, z) == null) {
                        vertexOffset = buildFace(vertexOffset, x, y, z, Face.TOP, cube.getTopTexture());
                        rendered = true;
                    }
                    if (y == 0 || get(x, y - 1, z) == null) {
                        vertexOffset = buildFace(vertexOffset, x, y, z, Face.BOTTOM, cube.getBottomTexture());
                        rendered = true;
                    }
                    if (z == depth - 1 || get(x, y, z + 1) == null) {
                        vertexOffset = buildFace(vertexOffset, x, y, z, Face.FRONT, cube.getFrontTexture());
                        rendered = true;
                    }
                    if (z == 0 || get(x, y, z - 1) == null) {
                        vertexOffset = buildFace(vertexOffset, x, y, z, Face.BACK, cube.getBackTexture());
                        rendered = true;
                    }
                    if (x == 0 || get(x - 1, y, z) == null) {
                        vertexOffset = buildFace(vertexOffset, x, y, z, Face.LEFT, cube.getLeftTexture());
                        rendered = true;
                    }
                    if (x == width - 1 || get(x + 1, y, z) == null) {
                        vertexOffset = buildFace(vertexOffset, x, y, z, Face.RIGHT, cube.getRightTexture());
                        rendered = true;
                    }

                    if (rendered) {
                        visibleCubes.add(cube);
                    }
                }
            }
        }
        return vertexOffset / VERTEX_COMPONENTS;
    }

  public float[] getMeshData() {
    return meshData;
  }

  public Cube[] getCubes() {
    return cubes;
  }

  public List<Cube> getVisibleCubes() {
    return visibleCubes;
  }

  public Vector3 getOrigin() {
    return origin;
  }
}
