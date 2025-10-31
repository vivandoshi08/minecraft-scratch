package io.cuber.vdcraft.visuals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import io.cuber.vdcraft.visuals.config.OptionsScreen;

public class ScreenManager {
  private final Overlay hud;
  private final OptionsScreen mainSettings;
  private boolean paused = false;

  public ScreenManager(Overlay hud, OptionsScreen mainSettings) {
    this.hud = hud;
    this.mainSettings = mainSettings;
  }

  public void escMenu() {
    paused = !paused;
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    Gdx.input.setCursorCatched(!paused);
    if (paused) {
      Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
      mainSettings.open();
    } else {
      Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);
      mainSettings.close();
    }
  }

  public boolean isGamePaused() {
    return paused;
  }

  public boolean isInventoryOpen() {
    return hud.getStorageScreen().isOpen();
  }

  public void toggleInventory() {
    if (hud.getStorageScreen().isOpen())
      hud.getStorageScreen().close();
    else
      hud.getStorageScreen().open();
  }

  public void updateCursorPosition(int x, int y) {
    if (hud.getStorageScreen().isOpen())
      hud.getStorageScreen().updateMouseCursor(x, y);
  }

  public void pressMouseButton(int x, int y) {
    if (hud.getStorageScreen().isOpen())
      hud.getStorageScreen().onMousePressed(x, y);
  }
}
