package io.cuber.vdcraft.visuals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.visuals.debug.DebugDisplay;

public class Overlay {
  private final StorageScreen inventoryHud;
  private OrthographicCamera hudCamera;
  private BitmapFont font;
  private SpriteBatch batch;
  private Texture crosshair;
  private Hotbar inventoryBar;
  private Character currentPlayer;

  public Overlay(StorageScreen inventoryHud) {
    this.inventoryHud = inventoryHud;
  }

  public void create(Character currentPlayer) {
    this.currentPlayer = currentPlayer;

    hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    hudCamera.position.set(Gdx.graphics.getWidth() / 2.F, Gdx.graphics.getHeight() / 2.F, 1.F);
    batch = new SpriteBatch();

    font = new BitmapFont();
    createCrosshair();
    inventoryBar = new Hotbar(currentPlayer.getStorage());
    inventoryBar.create();
  }

  private void createCrosshair() {

    var crosshairImage = new Pixmap(20, 20, Format.RGBA8888);
    crosshairImage.setColor(1, 1, 1, 0.8f);

    int center = crosshairImage.getWidth() / 2;
    int size = 6;
    int gap = 2;

    crosshairImage.drawLine(center, center - gap - size, center, center - gap);
    crosshairImage.drawLine(center, center + gap, center, center + gap + size);

    crosshairImage.drawLine(center - gap - size, center, center - gap, center);
    crosshairImage.drawLine(center + gap, center, center + gap + size, center);

    crosshair = new Texture(crosshairImage);
  }

  public void render() {
    hudCamera.update();
    batch.setProjectionMatrix(hudCamera.combined);
    batch.begin();
    font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, hudCamera.viewportHeight);
    batch.draw(crosshair, (hudCamera.viewportWidth / 2) - (crosshair.getWidth() / 2), (hudCamera.viewportHeight / 2) - (crosshair.getHeight() / 2));
    inventoryBar.render(batch, hudCamera.viewportWidth);
    inventoryHud.render(batch);

    for (var text : DebugDisplay.INSTANCE.getTextToRender()) {
      batch.setProjectionMatrix(hudCamera.combined);
      font.draw(batch, text.text(), text.x(), text.y());
    }

    DebugDisplay.INSTANCE.displayText(1, "Position: " + currentPlayer.getPos() + "/" + currentPlayer.getCam().position, 10, 10);

    batch.end();
  }

  public void resize(int width, int height) {
    hudCamera.viewportHeight = height;
    hudCamera.viewportWidth = width;
    hudCamera.position.set(width / 2.F, height / 2.F, 1.F);

    hudCamera.update();
  }

  public void dispose() {
    batch.dispose();
    font.dispose();
    crosshair.dispose();
  }

  public Camera getCam() {
    return hudCamera;
  }

  public StorageScreen getInventoryHud() {
    return inventoryHud;
  }

  public StorageScreen getStorageScreen() {
    return inventoryHud;
  }
}
