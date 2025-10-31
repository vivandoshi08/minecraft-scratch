package io.cuber.vdcraft.signals.basic;

import io.cuber.vdcraft.signals.SignalGroup;
import io.cuber.vdcraft.signals.SignalData;
import io.cuber.vdcraft.signals.SignalType;

public interface MouseSignal extends SignalGroup {
  void onMouseButtonPressed(MousePressData buttonPressedData);

  void onMouseMoved(MouseMoveData mouseMoveData);

  void onScrolled(MouseScrollData mouseScrollData);

  @SignalType(value = SignalType.SignalTypes.MOUSE_MOVE)
  final class MouseMoveData extends SignalData {
    private final int mouseX;
    private final int mouseY;
    private final float angleX;
    private final float angleY;

    public MouseMoveData(int mouseX, int mouseY, float angleX, float angleY) {
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.angleX = angleX;
      this.angleY = angleY;
    }

    public int getMouseX() {
      return mouseX;
    }

    public int getMouseY() {
      return mouseY;
    }

    public float getAngleX() {
      return angleX;
    }

    public float getAngleY() {
      return angleY;
    }
  }

  @SignalType(value = SignalType.SignalTypes.MOUSE_CLICK)
  final class MousePressData extends SignalData {
    private final int buttonPressed;
    private final int screenX;
    private final int screenY;

    public MousePressData(int screenX, int screenY, int buttonPressed) {
      this.screenX = screenX;
      this.screenY = screenY;
      this.buttonPressed = buttonPressed;
    }

    public int getButtonPressed() {
      return buttonPressed;
    }

    public int getScreenX() {
      return screenX;
    }

    public int getScreenY() {
      return screenY;
    }
  }

  @SignalType(value = SignalType.SignalTypes.MOUSE_SCROLL)
  final class MouseScrollData extends SignalData {
    private final float scrollAmount;

    public MouseScrollData(float scrollAmount) {
      this.scrollAmount = scrollAmount;
    }

    public float getButtonPressed() {
      return scrollAmount;
    }
  }
}
