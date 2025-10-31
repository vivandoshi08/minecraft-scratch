package io.cuber.vdcraft.actors.character;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.cuber.vdcraft.actors.Actor;
import io.cuber.vdcraft.helpers.WorldUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class Character implements Actor {
  public static final float WALK_SPEED = 8f;
  public static final float RUN_SPEED = 16f;
  public static final float WALKING_VELOCITY = 8f;
  public static final float RUNNING_VELOCITY = 16f;
  private static final int INVENTORY_SIZE = 40;
  private static final float MAX_CAMERA_PITCH_ANGLE = 85.0f;
  private static final float GRAVITY_MULTIPLIER = 4.0f;

  private final Camera cam;
  private final Storage storage = new Storage(INVENTORY_SIZE);
  private final AtomicBoolean jumping = new AtomicBoolean(false);

  private float pitch = 0;
  private float yaw = 0;
  private boolean flying = false;
  private float speed = WALK_SPEED;

  public Character(Camera cam) {
    this.cam = cam;
  }

  public Camera getCam() {
    return cam;
  }

  @Override
  public void move(Vector3 target) {

    cam.translate(target);
  }

  public void turnHorizontal(Vector3 rotationAxis, float angle) {
    cam.rotateAround(cam.position, rotationAxis, angle);
    yaw += angle;
    yaw %= 360;
  }

  public void rotateCameraX(Vector3 rotationAxis, float angle) {
    turnHorizontal(rotationAxis, angle);
  }

  public void turnVertical(Vector3 rotationAxis, float angle) {
    pitch = MathUtils.clamp(pitch - angle, -MAX_CAMERA_PITCH_ANGLE, MAX_CAMERA_PITCH_ANGLE);
    cam.direction.set(0, 0, -1);
    cam.rotate(pitch, 1, 0, 0);
    cam.rotate(yaw, 0, 1, 0);
  }

  public void rotateCameraY(Vector3 rotationAxis, float angle) {
    turnVertical(rotationAxis, angle);
  }

  public Storage getStorage() {
    return storage;
  }

  public Vector3 getPos() {
    return WorldUtils.toBlockCoordinates(cam.position);

  }

  @Override
  public void setPos(Vector3 add) {
    cam.position.set(add);
  }

  @Override
  public float getVelocity() {
    return speed;
  }

  @Override
  public void setVelocity(float speed) {
    this.speed = speed;
  }

  @Override
  public float getGravityCoefficient() {
    return GRAVITY_MULTIPLIER;
  }

  public boolean canFly() {
    return flying;
  }

  public void toggleFly(boolean flying) {
    this.flying = flying;
  }

  public Vector3 getFacing() {
    return cam.direction;
  }

  public boolean isInAir() {
    return jumping.get();
  }

  public void setInAir(boolean isJumping) {
    this.jumping.set(isJumping);
  }
}
