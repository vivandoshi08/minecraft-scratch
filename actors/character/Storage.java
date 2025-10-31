package io.cuber.vdcraft.actors.character;

import io.cuber.vdcraft.objects.ItemStack;

public class Storage {
  private final ItemStack[] stacks;
  private int selectedSlot = 1;

  public Storage(int slots) {
    stacks = new ItemStack[slots];
  }

  public int getSize() {
    return stacks.length;
  }

  public ItemStack getStack(int slot) {
    if (slot > stacks.length || slot < 0)
      throw new IllegalArgumentException("Slots out of bounds: " + slot + "/" + stacks.length);
    return stacks[slot];
  }

  public void putStack(ItemStack stack, int slot) {
    if (slot > stacks.length || slot < 0)
      throw new IllegalArgumentException("Slots out of bounds: " + slot + "/" + stacks.length);
    stacks[slot] = stack;
  }

  public int getSelectedSlot() {
    return selectedSlot;
  }

  public int getActiveSlot() {
    return selectedSlot;
  }

  public void setSelectedSlot(int slot) {
    if (slot < 1)
      slot = 1;
    if (slot > 9)
      slot = 9;
    this.selectedSlot = slot;
  }

  public ItemStack getSelectedItem() {
    return getStack(getSelectedSlot() - 1);
  }

  public ItemStack getActiveStack() {
    return getSelectedItem();
  }

  public void putStack(ItemStack stack) {
    for (var slot : stacks) {
      if (slot != null && slot.getId() == stack.getId()) {
        slot.setCount(slot.getCount() + 1);
        return;
      }
    }
    for (int i = 0; i < stacks.length; i++) {
      if (stacks[i] == null) {
        stacks[i] = stack;
        return;
      }
    }
  }
}
