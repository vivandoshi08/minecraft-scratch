package io.cuber.vdcraft.display;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import io.cuber.vdcraft.GameCore;
import io.cuber.vdcraft.actors.Actor;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.graphics.GameTextureAtlas;
import io.cuber.vdcraft.terrain.Cube;
import io.cuber.vdcraft.terrain.CubeTypes;
import io.cuber.vdcraft.terrain.GameMap;

import java.util.*;
import java.util.stream.Collectors;

public class MapRenderer implements RenderableProvider {
  public static final int SECTION_SIZE_X = 16;
  public static final int SECTION_SIZE_Y = 128;
  public static final int SECTION_SIZE_Z = 16;
  public static final int RENDER_DISTANCE_SECTIONS = 4;
  private static final float FLOAT_ADJUST = 0.0001F;
  private static final int DEFAULT_NEARBY_DISTANCE = 5;

  private final Section[] sections;

  private final Mesh[] meshes;

  private final Material material;

  private final Character character;

  private final boolean[] dirty;
  private final int[] numOfIndices;
  private final int chunksPerRow;
  private int lastRenderedChunks = 0;

  public MapRenderer(Character character, int totalSections) {
    this.character = character;
    var sectionsPerRow = Math.sqrt(totalSections);
    if (sectionsPerRow != (int) sectionsPerRow) {
      throw new IllegalArgumentException("The number of sections must be a perfect square.");
    }

    this.sections = new Section[totalSections];
    this.chunksPerRow = (int) sectionsPerRow;

    int currentSectionIndex = 0;
    int half = chunksPerRow / 2;

    for (int z = -half; z <= half; z++) {
      for (int x = -half; x <= half; x++) {
        this.sections[currentSectionIndex] = new Section(SECTION_SIZE_X, SECTION_SIZE_Y, SECTION_SIZE_Z, x * SECTION_SIZE_X, 0, z * SECTION_SIZE_Z);
        currentSectionIndex++;
      }
    }

    short[] indices = new short[SECTION_SIZE_X * SECTION_SIZE_Y * SECTION_SIZE_Z * (Section.VERTS_PER_SIDE + 2) * Section.CUBE_SIDES];
    int currentOffset = 0;
    for (int i = 0; i < indices.length; currentOffset += 4) {
      indices[i++] = (short) currentOffset;
      indices[i++] = (short) (currentOffset + 1);
      indices[i++] = (short) (currentOffset + 2);

      indices[i++] = (short) (currentOffset + 2);
      indices[i++] = (short) (currentOffset + 3);
      indices[i++] = (short) (currentOffset + 0);
    }

    this.meshes = new Mesh[totalSections];
    for (int i = 0; i < meshes.length; i++) {
      meshes[i] = new Mesh(true, SECTION_SIZE_X * SECTION_SIZE_Y * SECTION_SIZE_Z * Section.VERTS_PER_SIDE * Section.CUBE_SIDES, indices.length,
              VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
      meshes[i].setIndices(indices);
    }

    this.material = new Material(new TextureAttribute(TextureAttribute.Diffuse, GameTextureAtlas.getAtlas()));

    this.dirty = new boolean[totalSections];
    Arrays.fill(dirty, true);

    this.numOfIndices = new int[totalSections];
    Arrays.fill(numOfIndices, (short) 0);
  }

  public int getSectionIndex(float x, float z) {
    int gridX = Math.floorDiv((int) (x + FLOAT_ADJUST), SECTION_SIZE_X);
    int gridZ = Math.floorDiv((int) (z + FLOAT_ADJUST), SECTION_SIZE_Z);
    int half = chunksPerRow / 2;
    int col = gridX + half;
    int row = gridZ + half;
    if (col < 0 || col >= chunksPerRow || row < 0 || row >= chunksPerRow) {
        return -1;
    }
    return row * chunksPerRow + col;
  }


  public void setCube(int x, int y, int z, Cube cube) {
    int secIdx = getSectionIndex(x, z);
    if (secIdx == -1) return;

    int localX = x % SECTION_SIZE_X;
    int localZ = z % SECTION_SIZE_Z;
    if (localX < 0) localX += SECTION_SIZE_X;
    if (localZ < 0) localZ += SECTION_SIZE_Z;

    sections[secIdx].set(localX, y, localZ, cube);
    dirty[secIdx] = true;
  }

  public void removeCube(int x, int y, int z) {
    setCube(x, y, z, null);
  }

  public List<Cube> getNearbyCubes(Vector3 pos) {
    return getNearbyCubes(pos, DEFAULT_NEARBY_DISTANCE);
  }

  public List<Cube> getNearbyCubes(Vector3 pos, int dist) {
    int centerSecIdx = getSectionIndex(pos.x, pos.z);
    if (centerSecIdx == -1) return new ArrayList<>();

    List<Section> sectionsToCheck = new ArrayList<>();
    sectionsToCheck.add(sections[centerSecIdx]);

    for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
            if (x == 0 && z == 0) continue;

            int neighborIdx = centerSecIdx + x + (z * chunksPerRow);
            if (neighborIdx >= 0 && neighborIdx < sections.length) {
                int centerCol = centerSecIdx % chunksPerRow;
                int neighborCol = neighborIdx % chunksPerRow;
                if (Math.abs(centerCol - neighborCol) <= 1) {
                    sectionsToCheck.add(sections[neighborIdx]);
                }
            }
        }
    }

    final int maxDist = dist;
    List<Cube> nearCubes = new ArrayList<>();
    for (Section sec : sectionsToCheck) {
        nearCubes.addAll(sec.getVisibleCubes());
    }

    return nearCubes.stream()
            .filter(Objects::nonNull)
            .filter(c -> c.distTo(pos.x, pos.y, pos.z) < maxDist)
            .sorted(Comparator.comparingDouble(c -> c.distTo(pos.x, pos.y, pos.z)))
            .collect(Collectors.toList());
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    lastRenderedChunks = 0;
    int playerSecIdx = getSectionIndex(character.getPos().x, character.getPos().z);
    if (playerSecIdx == -1) return;

    int halfRenderDist = RENDER_DISTANCE_SECTIONS / 2;

    for (int z = -halfRenderDist; z <= halfRenderDist; z++) {
        for (int x = -halfRenderDist; x <= halfRenderDist; x++) {
            int currentSecIdx = playerSecIdx + x + (z * chunksPerRow);

            if (currentSecIdx < 0 || currentSecIdx >= sections.length) continue;

            int playerCol = playerSecIdx % chunksPerRow;
            int currentCol = currentSecIdx % chunksPerRow;
            if (Math.abs(playerCol - currentCol) > halfRenderDist) continue;

            var sec = sections[currentSecIdx];
            var mesh = meshes[currentSecIdx];

            if (dirty[currentSecIdx]) {
                var numVerts = sec.buildMesh();
                numOfIndices[currentSecIdx] = numVerts / 4 * 6;
                mesh.setVertices(sec.getMeshData(), 0, numVerts * 6);
                dirty[currentSecIdx] = false;
            }

            if (numOfIndices[currentSecIdx] == 0) continue;

            Renderable renderable = pool.obtain();
            renderable.material = material;
            renderable.meshPart.mesh = mesh;
            renderable.meshPart.offset = 0;
            renderable.meshPart.size = numOfIndices[currentSecIdx];
            renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
            renderables.add(renderable);
            lastRenderedChunks++;
        }
    }

    if (GameCore.DEBUG_ACTIVE) {
        System.out.println("Rendered " + lastRenderedChunks + " sections");
    }
  }

  public Section[] getSections() {
    return sections;
  }

  public Section getSection(int x, int y, int z) {
    int secIdx = getSectionIndex(x, z);
    if (secIdx == -1) return null;
    return sections[secIdx];
  }

  public Cube getCube(int x, int y, int z) {
    int secIdx = getSectionIndex(x, z);
    if (secIdx == -1) return null;

    int localX = x % SECTION_SIZE_X;
    int localZ = z % SECTION_SIZE_Z;
    if (localX < 0) localX += SECTION_SIZE_X;
    if (localZ < 0) localZ += SECTION_SIZE_Z;

    return sections[secIdx].get(localX, y, localZ);
  }

  public void fillSection(int secIdx, CubeTypes cubeType) {
    var sec = sections[secIdx];
    for (int x = 0; x < SECTION_SIZE_X; x++) {
      for (int z = 0; z < SECTION_SIZE_Z; z++) {
        for (int y = 0; y < 6; y++) {
          float worldX = x + sec.getOrigin().x;
          float worldY = y + sec.getOrigin().y;
          float worldZ = z + sec.getOrigin().z;
          sec.set(x, y, z, cubeType.makeCube(worldX, worldY, worldZ));
        }
      }
    }
  }

  public void addSection(Section newSec) {
    var secIdx = getSectionIndex(newSec.getOrigin().x, newSec.getOrigin().z);
    if (secIdx == -1) return;
    sections[secIdx] = newSec;
    dirty[secIdx] = true;
  }
}
