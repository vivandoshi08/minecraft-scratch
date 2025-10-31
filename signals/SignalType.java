package io.cuber.vdcraft.signals;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SignalType {
  SignalTypes value();

  enum SignalTypes {
    MOUSE_CLICK,
    KEY_PRESS,
    KEY_UP,
    KEY_DOWN,
    MOUSE_MOVE,
    CUBE_BREAK,
    CUBE_PLACE,
    CUBE_CLICK,
    CHAR_MOVE,
    MESSAGE_SENT,
    COMMAND_SENT,
    INVENTORY_OPEN,
    MOUSE_SCROLL,
    CHUNK_CHANGE,
    CHUNK_LOADED,
    CHUNK_UNLOADED,
    REGION_LOADED,
    REGION_UNLOADED;
  }
}
