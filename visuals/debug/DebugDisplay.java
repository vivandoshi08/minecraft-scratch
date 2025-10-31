package io.cuber.vdcraft.visuals.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class DebugDisplay {
  public final static DebugDisplay INSTANCE = new DebugDisplay();
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();
  private final SpriteBatch spriteBatch = new SpriteBatch();
  private final BitmapFont font = new BitmapFont();
  private final Map<Integer, TextToRender> textToRender = new HashMap<>();

  private DebugDisplay() {
    if (INSTANCE != null)
      throw new AssertionError();
  }

  public void renderLine(Camera cam, Line line) {
    Gdx.gl.glLineWidth(2);
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setProjectionMatrix(cam.combined);
    shapeRenderer.setColor(Color.WHITE);
    shapeRenderer.line(line.start, line.end);
    shapeRenderer.end();
    Gdx.gl.glLineWidth(1);
  }

  public void displayText(int id, String text, int x, int y) {
    textToRender.put(id, new TextToRender(text, x, y));
  }

  public Iterable<TextToRender> getTextToRender() {
    return textToRender.values();
  }

  public record Line(Vector3 start, Vector3 end) {
  }

  public record TextToRender(String text, int x, int y) {

  }
}
