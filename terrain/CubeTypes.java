package io.cuber.vdcraft.terrain;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import io.cuber.vdcraft.graphics.GameTextureAtlas;

import java.util.Arrays;
import java.util.Optional;

public enum CubeTypes {
  STONE("Stone", 0, 1),
  GRASS("Grass", 1, 0, 2, 3, 3, 3, 3),
  DIRT("Dirt", 3, 2),
  OAK_WOOD_PLANK("Oak Wood Plank", 4, 4),
  GOLD_ORE("Gold ore", 5, 32),
  IRON_ORE("Iron ore", 6, 33),
  COAL_ORE("Coal ore", 7, 34),
  BEDROCK("Bedrock", 8, 17),
  DIAMOND_ORE("Diamond ore", 9, 50),
  REDSTONE_ORE("Redstone ore", 10, 51);

  public final static int TILES_PER_ROW = 16;

  private final String name;
  private final int id;
  private final int topTextureId;
  private final int bottomTextureId;
  private final int leftTextureId;
  private final int rightTextureId;
  private final int frontTextureId;
  private final int backTextureId;

  CubeTypes(String name, int id, int topTextureId) {
    this(name, id, topTextureId, topTextureId, topTextureId, topTextureId, topTextureId, topTextureId);
  }

  CubeTypes(String name, int id, int topTextureId, int bottomTextureId, int leftTextureId,
         int rightTextureId, int frontTextureId, int backTextureId) {
    this.name = name;
    this.id = id;
    this.topTextureId = topTextureId;
    this.bottomTextureId = bottomTextureId;
    this.frontTextureId = frontTextureId;
    this.backTextureId = backTextureId;
    this.leftTextureId = leftTextureId;
    this.rightTextureId = rightTextureId;
  }

  public static Optional<CubeTypes> fromId(int id) {
    return Arrays.stream(CubeTypes.values()).filter(block -> block.id == id).findFirst();
  }

  public Cube makeCube(float x, float y, float z) {
    TextureRegion[][] tiles = GameTextureAtlas.getRegions();
    return new Cube(this.name, id, new Vector3(x, y, z),
            tiles[topTextureId / TILES_PER_ROW][topTextureId % TILES_PER_ROW],
            tiles[bottomTextureId / TILES_PER_ROW][bottomTextureId % TILES_PER_ROW],
            tiles[leftTextureId / TILES_PER_ROW][leftTextureId % TILES_PER_ROW],
            tiles[rightTextureId / TILES_PER_ROW][rightTextureId % TILES_PER_ROW],
            tiles[frontTextureId / TILES_PER_ROW][frontTextureId % TILES_PER_ROW],
            tiles[backTextureId / TILES_PER_ROW][backTextureId % TILES_PER_ROW]);
  }

  public int getId() {
    return id;
  }
}
