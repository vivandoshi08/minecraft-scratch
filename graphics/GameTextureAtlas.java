package io.cuber.vdcraft.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameTextureAtlas {
  private static final GameTextureAtlas INSTANCE = new GameTextureAtlas();
  private final int TEXTURE_SIZE = 16;
  private final TextureRegion[][] tiles;
  private final Texture fullTexture;

  {
    var textureAtlas = new com.badlogic.gdx.graphics.g2d.TextureAtlas(Gdx.files.internal("models/minecraft.atlas"));
    var cubeTextureRegion = textureAtlas.findRegion("minecraft");
    cubeTextureRegion.setRegionX(2);
    fullTexture = cubeTextureRegion.getTexture();
    tiles = cubeTextureRegion.split(TEXTURE_SIZE, TEXTURE_SIZE);
  }

  private GameTextureAtlas() {
    if (INSTANCE != null)
      throw new AssertionError();
  }

  public static TextureRegion[][] getRegions() {
    return INSTANCE.tiles;
  }

  public static Texture getAtlas() {
    return INSTANCE.fullTexture;
  }
}
