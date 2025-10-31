package io.cuber.vdcraft.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import io.cuber.vdcraft.visuals.debug.DebugDisplay;

public class DebugControls implements InputProcessor {
  private final Camera cam;
  private boolean vsyncEnabled = false;

  public DebugControls(Camera cam) {
    this.cam = cam;
  }

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
    case Input.Keys.F1 -> {
      vsyncEnabled ^= true;
      Gdx.graphics.setVSync(vsyncEnabled);
      DebugDisplay.INSTANCE.displayText(0, "VSYNC: " + (vsyncEnabled ? "On" : "Off"), (int) cam.viewportWidth - 100,
          (int) cam.viewportHeight - 10);

    }
    }
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return false;
  }

  @Override
  public boolean touchCancelled(int a, int b, int c, int d) {
    return false;
  }
}
