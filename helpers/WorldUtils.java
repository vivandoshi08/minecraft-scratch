package io.cuber.vdcraft.helpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.cuber.vdcraft.display.Section;
import io.cuber.vdcraft.terrain.CubeTypes;
import io.cuber.vdcraft.terrain.GameMap;

import java.util.Random;

public final class WorldUtils {
  public static final int CHUNK_SIZE_X = 16;
  public static final int CHUNK_SIZE_Y = 128;
  public static final int CHUNK_SIZE_Z = 16;
  public static final int CHUNKS_PER_ROW = 16;

  private final static Random random = new Random();

  private WorldUtils() {
    throw new AssertionError("Uninstantiable");
  }

  public static Vector3 toBlockCoordinates(Vector3 worldCoordinates) {
    return worldCoordinates.cpy().scl(1F / GameMap.CUBE_SIZE);
  }

  public static Vector3 toWorldCoordinates(Vector3 blockCoordinates) {
    return blockCoordinates.cpy().scl(GameMap.CUBE_SIZE);
  }

  public static Vector2 toBlockCoordinates(Vector2 worldCoordinates) {
    return worldCoordinates.cpy().scl(1F / GameMap.CUBE_SIZE);
  }

  public static Vector2 toWorldCoordinates(Vector2 blockCoordinates) {
    return blockCoordinates.cpy().scl(GameMap.CUBE_SIZE);
  }

  public static int getChunkIndex(float x, float z) {
    var minAddingValueX = 0.0001F;
    var minAddingValueZ = 0.0001F;

    int rowIndex = (int) Math.ceil((z + minAddingValueZ) / CHUNK_SIZE_Z) + (int) Math.floor(CHUNKS_PER_ROW / 2F) - 1;
    rowIndex %= 16;
    int colIndex = (int) (Math.ceil((x + minAddingValueX) / CHUNK_SIZE_X) + (int) Math.floor(CHUNKS_PER_ROW / 2F) - 1);
    colIndex %= 16;
    return (rowIndex * CHUNKS_PER_ROW) + colIndex;
  }

  public static void buildSection(Section section) {
    CubeTypes blockType = CubeTypes.values()[random.nextInt(CubeTypes.values().length)];
    for (int x = 0; x < CHUNK_SIZE_X; x++) {
      for (int z = 0; z < CHUNK_SIZE_Z; z++) {
        for (int y = 0; y < 6; y++) {
          section.set(x, y, z, blockType.makeCube(x + section.getOrigin().x / GameMap.CUBE_SIZE, y,
                  z + section.getOrigin().z / GameMap.CUBE_SIZE));
        }
      }
    }
  }
}
