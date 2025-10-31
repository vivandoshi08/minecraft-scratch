package io.cuber.vdcraft.signals.basic;

import io.cuber.vdcraft.signals.SignalGroup;
import io.cuber.vdcraft.signals.SignalData;
import io.cuber.vdcraft.signals.SignalType;

public interface KeySignal extends SignalGroup {
  @SignalType(value = SignalType.SignalTypes.KEY_PRESS)
  void onKeyPress(KeyboardData keyData);

  @SignalType(value = SignalType.SignalTypes.KEY_DOWN)
  void onKeyDown(KeyboardData keyData);

  @SignalType(value = SignalType.SignalTypes.KEY_UP)
  void onKeyUp(KeyboardData keyData);

  public final class KeyboardData extends SignalData {
    private final int keyCode;

    public KeyboardData(int keyCode) {
      this.keyCode = keyCode;
    }

    public int getKeyCode() {
      return keyCode;
    }
  }
}
