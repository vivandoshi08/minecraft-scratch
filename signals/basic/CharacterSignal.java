package io.cuber.vdcraft.signals.basic;

import com.badlogic.gdx.math.Vector3;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.signals.SignalGroup;
import io.cuber.vdcraft.signals.SignalData;
import io.cuber.vdcraft.signals.SignalType;

public interface CharacterSignal extends SignalGroup {
  void onMove(CharacterSignalData eventData);

  @SignalType(value = SignalType.SignalTypes.CHAR_MOVE)
  class CharacterSignalData extends SignalData {
    private final Vector3 targetPosition;
    private final Character character;

    public CharacterSignalData(Vector3 targetPosition, Character character) {
      this.targetPosition = targetPosition;
      this.character = character;
    }

    public Character getPlayer() {
      return character;
    }

    public Vector3 getTargetPos() {
      return targetPosition;
    }
  }
}
