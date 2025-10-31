package io.cuber.vdcraft.actors;

import com.badlogic.gdx.math.Vector3;

public interface MovingActor {
  float getWeightFactor();

  void applyMovement(Vector3 displacement);

  Vector3 getCurrentLocation();

  void updateLocation(Vector3 newCoords);

  float getMovementRate();

  void adjustMovementRate(float newRate);
}
