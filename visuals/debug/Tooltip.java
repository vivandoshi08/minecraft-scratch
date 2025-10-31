package io.cuber.vdcraft.visuals.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tooltip {
  private final BitmapFont font = new BitmapFont();
  private final List<String> lines = new ArrayList<>();
  private final Texture background;

  public Tooltip(String... lines) {
    this.lines.addAll(Arrays.asList(lines));
    Pixmap backgroundPixmap = new Pixmap(10, lines.length * 10, Format.RGBA4444);
    backgroundPixmap.setColor(Color.rgba4444(Color.GRAY));
    backgroundPixmap.fill();
    background = new Texture(backgroundPixmap);
  }

  public void render(Batch batch, int x, int y) {
    var currentY = y;
    for (var line : lines) {
      font.draw(batch, line, x, currentY);
      currentY -= 10;
    }
  }
}
