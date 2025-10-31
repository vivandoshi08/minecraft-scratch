package io.cuber.vdcraft.visuals.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.display.MapRenderer;

import java.util.function.BiConsumer;

public class OptionsScreen implements Screen {
  private final Stage stage = new Stage();
  private final Table table = new Table();
  private final Color backgroundColor = Color.GRAY;
  private final Character character;
  private final Music music;
  private boolean opened = false;

  public OptionsScreen(Character character, Music music) {
    this.character = character;
    this.music = music;
    Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
    var fovValue = ((PerspectiveCamera) character.getCam()).fieldOfView;

    table.setSize(700, 500);
    table.pad(10F);

    var bgPixmap = new Pixmap((int) table.getWidth(), (int) table.getHeight(), Pixmap.Format.RGBA4444);
    backgroundColor.a = 0.8F;
    bgPixmap.setColor(backgroundColor);
    bgPixmap.fill();
    var textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
    table.setBackground(textureRegionDrawableBg);
    table.setColor(Color.GRAY);

    table.align(Align.top);
    table.setTransform(true);
    table.center();

    table.setPosition((Gdx.graphics.getWidth() / 2F) - (table.getWidth() / 2F), (Gdx.graphics.getHeight() / 2F) - (table.getHeight() / 2F));

    stage.addActor(table);

    table.row().pad(10, 0, 10, 10);
    table.align(Align.topLeft);
    createSlider(skin, fovValue, 70, 120, "FOV", (event, actor) -> {
      var perspectiveCamera = (PerspectiveCamera) character.getCam();
      perspectiveCamera.fieldOfView = ((Slider) actor).getValue();
    });
    table.align(Align.topRight);
    createSlider(skin, 50, 0, 100, "Music", (event, actor) -> {
      // GameCore.SOUND_VOLUME = ((Slider) actor).getValue();
      music.setVolume(((Slider) actor).getValue() / 100F);
    });
    table.row();
    table.align(Align.topLeft);
    createSlider(skin, 10, 0, 10, "Chunks to render", (event, actor) -> {
      // MapRenderer.RENDER_DISTANCE_SECTIONS = (int) ((Slider) actor).getValue();
    });
    table.add(new Label("New line", skin));
  }

  private void createSlider(Skin skin, float startingValue, float min, float max, String labelText,
                            BiConsumer<ChangeListener.ChangeEvent, Actor> onChange) {
    Slider slider = new Slider(min, max, 1, false, skin);
    slider.setValue(startingValue);
    var sliderLabel = new Label(labelText + " (" + startingValue + "): ", skin);

    slider.addCaptureListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        onChange.accept(event, actor);
        sliderLabel.setText(labelText + " (" + slider.getValue() + "): ");
      }
    });
    table.add(sliderLabel);
    table.add(slider).fillX().uniformX();
  }

  public void render() {
    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();
  }

  public Stage getStage() {
    return stage;
  }

  public void open() {
    opened = true;
  }

  public void close() {
    opened = false;
  }

  public boolean isOpened() {
    return opened;
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {

  }

  @Override
  public void resize(int width, int height) {
    table.setPosition((Gdx.graphics.getWidth() / 2F) - (table.getWidth() / 2F), (Gdx.graphics.getHeight() / 2F) - (table.getHeight() / 2F));
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {

  }
}
