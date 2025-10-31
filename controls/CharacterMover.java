package io.cuber.vdcraft.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.signals.SignalBus;
import io.cuber.vdcraft.signals.SignalType;
import io.cuber.vdcraft.signals.basic.MouseSignal;
import io.cuber.vdcraft.signals.basic.CharacterSignal;
import io.cuber.vdcraft.helpers.WorldUtils;
import io.cuber.vdcraft.terrain.GameMap;

import java.util.HashSet;

public class CharacterMover implements InputProcessor {
  private final HashSet<Integer> pressedKey = new HashSet<>();
  private final Character character;
  private final GameMap gameMap;

  private float verticalVelocity = 0f;
  private static final float GRAVITY = -32f;
  private static final float JUMP_VELOCITY = 12f;
  private static final float TERMINAL_VELOCITY = -50f;
  private static final float MOUSE_SENSITIVITY = 0.15f;
  private boolean isGrounded = false;

  public CharacterMover(Character character, GameMap gameMap) {
    this.character = character;
    this.gameMap = gameMap;
  }

  @Override
  public boolean keyDown(int keyCode) {
    var cam = (PerspectiveCamera) character.getCam();

    switch (keyCode) {
      case Keys.C -> {
        cam.fieldOfView = 10;
      }
      case Keys.CONTROL_LEFT -> character.setVelocity(Character.RUNNING_VELOCITY);
      default -> pressedKey.add(keyCode);
    }
    return true;
  }

  @Override
  public boolean keyUp(int keyCode) {
    var cam = (PerspectiveCamera) character.getCam();

    switch (keyCode) {
      case Keys.C -> {
        cam.fieldOfView = 90;
      }
      case Keys.F1 -> character.toggleFly(!character.canFly());
      case Keys.CONTROL_LEFT -> character.setVelocity(Character.WALKING_VELOCITY);
      default -> pressedKey.remove(keyCode);
    }
    return true;
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
    return mouseMoved(screenX, screenY);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    if (!Gdx.input.isCursorCatched())
      return false;
    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    var cam = character.getCam();

    float deltaX = (screenX - width / 2F) * MOUSE_SENSITIVITY;
    float deltaY = (screenY - height / 2F) * MOUSE_SENSITIVITY;

    Gdx.input.setCursorPosition(width / 2, height / 2);

    var eventData = new MouseSignal.MouseMoveData(screenX, screenY, 0, 0);
    SignalBus.call(SignalType.SignalTypes.MOUSE_MOVE, eventData);

    if (eventData.isCancelled())
      return true;

    if (deltaX != 0)
      character.rotateCameraX(Vector3.Y, -deltaX);
    if (deltaY != 0) {
      var rotationAxis = cam.direction.cpy().crs(cam.up);
      character.rotateCameraY(rotationAxis, -deltaY);
    }
    return true;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return false;
  }

  public void update(float gameDeltaTime) {
    final float movementSpeed = character.getVelocity();
    final float flightSpeed = 20f;
    var cam = (PerspectiveCamera) character.getCam();

    isGrounded = gameMap.hasCollision(character.getPos().scl(GameMap.CUBE_SIZE));

    if (!character.canFly()) {
      if (!isGrounded) {
        verticalVelocity += GRAVITY * gameDeltaTime;
        if (verticalVelocity < TERMINAL_VELOCITY) {
          verticalVelocity = TERMINAL_VELOCITY;
        }
      } else {
        verticalVelocity = 0;
        character.setInAir(false);
      }

      if (verticalVelocity != 0) {
        Vector3 verticalMovement = new Vector3(0, verticalVelocity * gameDeltaTime, 0);
        Vector3 newPosition = cam.position.cpy().add(verticalMovement);
        if (!gameMap.hasCollision(newPosition)) {
          cam.translate(verticalMovement);
        } else {
          verticalVelocity = 0;
        }
      }
    }

    Vector3 moveDirection = new Vector3();

    for (var keyCode : pressedKey) {
      switch (keyCode) {
        case Keys.W -> {
          var direction = character.getFacing().cpy().nor();
          moveDirection.add(direction.x, 0, direction.z);
        }
        case Keys.S -> {
          var direction = character.getFacing().cpy().nor();
          moveDirection.add(-direction.x, 0, -direction.z);
        }
        case Keys.A -> {
          var direction = character.getFacing().cpy();
          var horizontal = direction.crs(cam.up).nor();
          moveDirection.add(-horizontal.x, 0, -horizontal.z);
        }
        case Keys.D -> {
          var direction = character.getFacing().cpy();
          var horizontal = direction.crs(cam.up).nor();
          moveDirection.add(horizontal.x, 0, horizontal.z);
        }
        case Keys.SPACE -> {
          if (character.canFly()) {
            moveDirection.add(0, flightSpeed * gameDeltaTime, 0);
          } else if (isGrounded && !character.isInAir()) {

            verticalVelocity = JUMP_VELOCITY;
            character.setInAir(true);
          }
        }
        case Keys.SHIFT_LEFT -> {
          if (character.canFly()) {
            moveDirection.add(0, -flightSpeed * gameDeltaTime, 0);
          }
        }
      }
    }

    if (moveDirection.x != 0 || moveDirection.z != 0) {

      float horizontalLength = (float) Math.sqrt(moveDirection.x * moveDirection.x + moveDirection.z * moveDirection.z);
      if (horizontalLength > 0 && !character.canFly()) {
        moveDirection.x = (moveDirection.x / horizontalLength) * movementSpeed * gameDeltaTime;
        moveDirection.z = (moveDirection.z / horizontalLength) * movementSpeed * gameDeltaTime;
      } else if (character.canFly()) {
        moveDirection.scl(flightSpeed * gameDeltaTime / Math.max(horizontalLength, 1));
      }

      Vector3 finalPosition = cam.position.cpy().add(moveDirection);

      var eventData = new CharacterSignal.CharacterSignalData(WorldUtils.toBlockCoordinates(finalPosition), character);
      SignalBus.call(SignalType.SignalTypes.CHAR_MOVE, eventData);

      if (!eventData.isCancelled()) {

        if (!gameMap.hasCollision(finalPosition)) {
          cam.translate(moveDirection);
        } else {

          Vector3 xOnly = new Vector3(moveDirection.x, moveDirection.y, 0);
          Vector3 xPosition = cam.position.cpy().add(xOnly);
          if (!gameMap.hasCollision(xPosition)) {
            cam.translate(xOnly);
          } else {

            Vector3 zOnly = new Vector3(0, moveDirection.y, moveDirection.z);
            Vector3 zPosition = cam.position.cpy().add(zOnly);
            if (!gameMap.hasCollision(zPosition)) {
              cam.translate(zOnly);
            }
          }
        }
      }
    }
  }

  @Override
  public boolean touchCancelled(int a, int b, int c, int d) {
    return false;
  }
}
