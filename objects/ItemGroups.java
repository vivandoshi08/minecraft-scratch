package io.cuber.vdcraft.objects;

public enum ItemGroups {
  COMBAT("Combat"),
  FOOD("Food"),
  BLOCK("Cube"),
  ENTITY("Entity");
  private final String name;

  ItemGroups(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
