package io.cuber.vdcraft.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemStack {
  private final String name;
  private final int id;
  private final TextureRegion image;
  private int quantity = 1;

  public ItemStack(String name, int id, TextureRegion image) {
    this.name = name;
    this.id = id;
    this.image = image;
  }

  public int getCount() {
    return quantity;
  }

  public void setCount(int quantity) {
    this.quantity = quantity;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public TextureRegion getImage() {
    return image;
  }
}
