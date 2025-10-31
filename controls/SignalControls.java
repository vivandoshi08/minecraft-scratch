package io.cuber.vdcraft.controls;

import com.badlogic.gdx.InputProcessor;
import io.cuber.vdcraft.signals.SignalBus;
import io.cuber.vdcraft.signals.SignalType;
import io.cuber.vdcraft.signals.basic.KeySignal;
import io.cuber.vdcraft.signals.basic.MouseSignal;

public class SignalControls implements InputProcessor {
  @Override
  public boolean keyDown(int keycode) {
    var eventData = new KeySignal.KeyboardData(keycode);
    SignalBus.call(SignalType.SignalTypes.KEY_DOWN, eventData);
    return eventData.isCancelled();
  }

  @Override
  public boolean keyUp(int keycode) {
    var eventData = new KeySignal.KeyboardData(keycode);
    SignalBus.call(SignalType.SignalTypes.KEY_UP, eventData);
    return eventData.isCancelled();
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    var eventData = new MouseSignal.MousePressData(screenX, screenY, button);
    SignalBus.call(SignalType.SignalTypes.MOUSE_CLICK, eventData);
    return eventData.isCancelled();
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
    var eventData = new MouseSignal.MouseScrollData(amountY);
    SignalBus.call(SignalType.SignalTypes.MOUSE_SCROLL, eventData);
    return eventData.isCancelled();
  }

  @Override
  public boolean touchCancelled(int a, int b, int c, int d) {
    return false;
  }
}
