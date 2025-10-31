package io.cuber.vdcraft.visuals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.cuber.vdcraft.actors.character.Storage;
import io.cuber.vdcraft.visuals.debug.Tooltip;
import io.cuber.vdcraft.objects.ItemStack;

public class StorageScreen {
  private final static int ITEMS_PER_ROW = 8;
  private final static int SLOT_SIZE = 70;

  private final Storage storage;
  private final Hotbar.Slot[] slots = new Hotbar.Slot[40];
  private final Vector2 startingPosition = new Vector2(100, 100);
  private final Texture inventoryBackground;
  private final BitmapFont font = new BitmapFont();
  private boolean open = false;
  private Hotbar.Slot previousSelectedSlot = null;
  private ItemStack pickedItem;
  private int mouseX = 0;
  private int mouseY = 0;
  private Tooltip currentItemPopup;

  public StorageScreen(Storage storage) {
    this.storage = storage;
    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Pixmap pixmap = new Pixmap(ITEMS_PER_ROW * (SLOT_SIZE), (slots.length / ITEMS_PER_ROW) * (SLOT_SIZE) + 20, Format.RGBA4444);

    pixmap.setColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, 0.8F);
    pixmap.fill();
    inventoryBackground = new Texture(pixmap);
    create();
  }

  public void open() {
    open = true;
    Gdx.input.setCursorCatched(false);
  }

  public void close() {
    open = false;
    Gdx.input.setCursorCatched(true);
  }

  private void create() {
    Pixmap initial = Hotbar.Slot.drawSlot(1);
    Pixmap selected = Hotbar.Slot.drawSlot(3);
    for (int rows = 0; rows < slots.length / ITEMS_PER_ROW; rows++) {
      for (int slotIndex = 0; slotIndex < ITEMS_PER_ROW; slotIndex++) {
        slots[rows * ITEMS_PER_ROW + slotIndex] = new Hotbar.Slot(initial, selected);
      }
    }
  }

  public void render(SpriteBatch hudBatch) {
    if (!open)
      return;
    hudBatch.draw(inventoryBackground, startingPosition.x, startingPosition.y - 20);

    for (int rows = 0; rows < slots.length / ITEMS_PER_ROW; rows++) {
      for (int slotIndex = 0; slotIndex < ITEMS_PER_ROW; slotIndex++) {
        var index = rows * ITEMS_PER_ROW + slotIndex;
        var currentSlot = slots[index];

        var inventoryBarPadding = (rows == 0 ? -20 : 0);

        hudBatch.draw(currentSlot.getTexture(), startingPosition.x + (slotIndex * SLOT_SIZE), startingPosition.y + (rows * SLOT_SIZE) + inventoryBarPadding, SLOT_SIZE, SLOT_SIZE);
        if (storage.getStack(index) != null) {
          var paddingX = 3.5F;
          var paddingY = 6F;
          var size = SLOT_SIZE - 10;
          hudBatch.draw(storage.getStack(index).getImage(), startingPosition.x + (slotIndex * SLOT_SIZE) + paddingX, startingPosition.y + paddingY + (rows * SLOT_SIZE) + inventoryBarPadding, size, size);
          font.draw(hudBatch, storage.getStack(index).getCount() + "", startingPosition.x + (slotIndex * SLOT_SIZE) + paddingX + size - 10, startingPosition.y + paddingY + (rows * SLOT_SIZE) + inventoryBarPadding + 15);
        }
      }
    }

    if (pickedItem != null) {
      hudBatch.draw(pickedItem.getImage(), mouseX, mouseY);
    }

    if (currentItemPopup != null) {
      currentItemPopup.render(hudBatch, mouseX, mouseY);
    }
  }

  private int getSlotIndex(int x, int y) {
    if (previousSelectedSlot != null)
      previousSelectedSlot.unselect();

    x -= startingPosition.x;
    y -= startingPosition.y;
    var NUM_OF_ROWS = slots.length / ITEMS_PER_ROW;
    if (x < 0 || y < 0 || x > (SLOT_SIZE * ITEMS_PER_ROW) || y > (SLOT_SIZE * NUM_OF_ROWS))
      return -1;

    y = y / SLOT_SIZE;

    y = NUM_OF_ROWS - y - 1;

    int slotIndex = (x / SLOT_SIZE) + (y * ITEMS_PER_ROW);
    return slotIndex;
  }

  public void updateMouseCursor(int x, int y) {
    mouseX = x;
    mouseY = Gdx.graphics.getHeight() - y;
    var slotIndex = getSlotIndex(x, y);

    if (slotIndex >= slots.length || slotIndex < 0) {
      currentItemPopup = null;
      return;
    }

    var selectedSlot = slots[slotIndex];
    var selectedItem = storage.getStack(slotIndex);
    if (selectedItem != null) {
      currentItemPopup = new Tooltip(selectedItem.getName());
    } else
      currentItemPopup = null;
    selectedSlot.select();
    previousSelectedSlot = selectedSlot;
  }

  public boolean isOpen() {
    return open;
  }

  public void resize(float newWidth, float newHeight) {
    var width = (newWidth / 2) - (ITEMS_PER_ROW * (SLOT_SIZE / 2));
    var height = (newHeight / 2) - ((slots.length / ITEMS_PER_ROW) * (SLOT_SIZE / 2));
    startingPosition.set(width, height);
  }

  public void onMousePressed(int x, int y) {
    var slotIndex = getSlotIndex(x, y);

    if (slotIndex >= slots.length || slotIndex < 0) {
      pickedItem = null;
      return;
    }

    var newItem = storage.getStack(slotIndex);

    if (newItem == null && pickedItem != null) {
      storage.putStack(pickedItem, slotIndex);
      pickedItem = null;
      return;
    }

    if (newItem != null && pickedItem != null) {
      if (newItem.getId() == pickedItem.getId()) {
        newItem.setCount(newItem.getCount() + pickedItem.getCount());
        pickedItem = null;
      } else {
        storage.putStack(pickedItem, slotIndex);
        pickedItem = newItem;
      }
      return;
    }

    if (newItem != null && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
      newItem.setCount(64);

    pickedItem = newItem;
    storage.putStack(null, slotIndex);
  }
}
