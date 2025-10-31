package io.cuber.vdcraft.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.cuber.vdcraft.graphics.GameTextureAtlas;
import io.cuber.vdcraft.terrain.CubeTypes;

import java.util.HashMap;
import java.util.Map;

public enum ItemRegistry {
  STONE("Stone", 0, ItemGroups.BLOCK, 1),
  GRASS("Grass", 1, ItemGroups.BLOCK, 3),
  DIRT("Dirt", 3, ItemGroups.BLOCK, 2),
  OAK_WOOD_PLANK("Oak Wood Plank", 4, ItemGroups.BLOCK, 4),
  GOLD_ORE("Gold ore", 5, ItemGroups.BLOCK, 32),
  IRON_ORE("Iron ore", 6, ItemGroups.BLOCK, 33),
  COAL_ORE("Coal ore", 7, ItemGroups.BLOCK, 34),
  BEDROCK("Bedrock", 8, ItemGroups.BLOCK, 17),
  DIAMOND_ORE("Diamond ore", 9, ItemGroups.BLOCK, 50),
  REDSTONE_ORE("Redstone ore", 10, ItemGroups.BLOCK, 51);

  private final static Map<ItemRegistry, TextureRegion> cache = new HashMap<>();

  private final String name;
  private final int id;
  private final int textureId;
  private final ItemGroups category;

  ItemRegistry(String name, int id, ItemGroups category, int textureId) {
    this.name = name;
    this.id = id;
    this.textureId = textureId;
    this.category = category;
  }

  public ItemStack makeStack() {
    if (cache.containsKey(this)) {
      return new ItemStack(this.name, id, cache.get(this));
    }
    var tiles = GameTextureAtlas.getRegions();
    cache.put(this, tiles[textureId / CubeTypes.TILES_PER_ROW][textureId % CubeTypes.TILES_PER_ROW]);
    return new ItemStack(name, id, tiles[textureId / CubeTypes.TILES_PER_ROW][textureId % CubeTypes.TILES_PER_ROW]);
  }

  public int getId() {
    return id;
  }
}
