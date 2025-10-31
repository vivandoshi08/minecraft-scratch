package io.cuber.vdcraft.actors;

import com.badlogic.gdx.math.Vector3;

public interface Actor {
  void move(Vector3 target);

  Vector3 getPos();

  void setPos(Vector3 pos);

  float getVelocity();

  void setVelocity(float speed);

  float getGravityCoefficient();
}
