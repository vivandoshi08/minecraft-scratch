package io.cuber.vdcraft.terrain;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Cube {
  private final String name;
  private final int id;

  private final Vector3 pos;
  private final BoundingBox hitbox = new BoundingBox();
  private final TextureRegion topTexture;
  private final TextureRegion bottomTexture;
  private final TextureRegion leftTexture;
  private final TextureRegion rightTexture;
  private final TextureRegion frontTexture;
  private final TextureRegion backTexture;
  private final Vector3 center = new Vector3();
  private boolean selected = false;

  public Cube(String name, int id, Vector3 pos, TextureRegion topTexture, TextureRegion bottomTexture,
               TextureRegion leftTexture, TextureRegion rightTexture, TextureRegion frontTexture, TextureRegion backTexture) {
    this.name = name;
    this.id = id;
    this.pos = pos;
    this.topTexture = topTexture;
    this.bottomTexture = bottomTexture;
    this.leftTexture = leftTexture;
    this.rightTexture = rightTexture;
    this.frontTexture = frontTexture;
    this.backTexture = backTexture;

    hitbox.set(new Vector3(pos.x, pos.y, pos.z), new Vector3(pos.x + 1, pos.y + 1, pos.z + 1));
    hitbox.getCenter(center);
  }


  public Vector3 getPos() {
    return pos;
  }

  public float distTo(float x, float y, float z) {
    return center.cpy().dst(x, y, z);
  }

  public BoundingBox getHitbox() {
    return hitbox;
  }


  public boolean isSelected() {
    return selected;
  }


  public TextureRegion getTopTexture() {
    return topTexture;
  }

  public TextureRegion getBottomTexture() {
    return bottomTexture;
  }

  public TextureRegion getLeftTexture() {
    return leftTexture;
  }

  public TextureRegion getRightTexture() {
    return rightTexture;
  }

  public TextureRegion getFrontTexture() {
    return frontTexture;
  }

  public TextureRegion getBackTexture() {
    return backTexture;
  }

  public int getId() {
    return id;
  }
}
