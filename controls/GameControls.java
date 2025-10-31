package io.cuber.vdcraft.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.signals.SignalBus;
import io.cuber.vdcraft.signals.SignalType;
import io.cuber.vdcraft.signals.basic.CubeSignal;
import io.cuber.vdcraft.objects.ItemRegistry;
import io.cuber.vdcraft.terrain.Cube;
import io.cuber.vdcraft.terrain.CubeTypes;
import io.cuber.vdcraft.terrain.GameMap;

import java.util.Arrays;
import java.util.Optional;

public class GameControls implements InputProcessor {
  private static final float BLOCK_PICKING_RAY_DISTANCE = 100f;

  private final GameMap gameMap;
  private final Character character;

  public GameControls(GameMap gameMap, Character character) {
    this.gameMap = gameMap;
    this.character = character;
  }

  @Override
  public boolean keyDown(int keyCode) {
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

  private Optional<Cube> findPickedBlock(Vector3 outIntersection) {
    Ray pickRay = character.getCam().getPickRay(character.getCam().viewportWidth / 2F, character.getCam().viewportHeight / 2F);
    Cube closestCube = null;
    float closestDist = BLOCK_PICKING_RAY_DISTANCE;

    for (Cube cube : gameMap.getNearbyCubes(character.getPos())) {
        if (Intersector.intersectRayBounds(pickRay, cube.getHitbox(), outIntersection)) {
            float dist = pickRay.origin.dst2(outIntersection);
            if (dist < closestDist) {
                closestDist = dist;
                closestCube = cube;
            }
        }
    }
    return Optional.ofNullable(closestCube);
  }

  private Vector3 findNextFreeBlock(Cube pointedBlock, Vector3 intersection) {
    Vector3 blockCenter = pointedBlock.getHitbox().getCenter(new Vector3());
    Vector3 direction = intersection.cpy().sub(blockCenter);

    float absX = Math.abs(direction.x);
    float absY = Math.abs(direction.y);
    float absZ = Math.abs(direction.z);

    Vector3 placePos = pointedBlock.getPos().cpy();

    if (absX > absY && absX > absZ) {
        placePos.x += (direction.x > 0) ? 1 : -1;
    } else if (absY > absX && absY > absZ) {
        placePos.y += (direction.y > 0) ? 1 : -1;
    } else {
        placePos.z += (direction.z > 0) ? 1 : -1;
    }
    return placePos;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Vector3 intersection = new Vector3();
    var cube = findPickedBlock(intersection).orElse(null);
    if (cube == null)
      return false;

    var eventData = new CubeSignal.BlockClickedData(cube, button);
    SignalBus.call(SignalType.SignalTypes.CUBE_CLICK, eventData);
    if (eventData.isCancelled())
      return false;

    if (button == Input.Buttons.LEFT) {
      var blockData = new CubeSignal.BlockData(cube);
      SignalBus.call(SignalType.SignalTypes.CUBE_BREAK, blockData);
      if (blockData.isCancelled()) return false;

      gameMap.removeCube(cube);
      var itemType = Arrays.stream(ItemRegistry.values()).filter(stack -> stack.getId() == cube.getId()).findFirst();
      itemType.ifPresent(stack -> character.getStorage().putStack(stack.makeStack()));
    } else if (button == Input.Buttons.RIGHT) {
      var nextCoords = findNextFreeBlock(cube, intersection);
      var targetBlock = gameMap.getCube(nextCoords);
      if (targetBlock != null)
        return false;
      if (gameMap.hasCollision(character.getPos(), nextCoords)) return false;

      var currentItem = character.getStorage().getActiveStack();
      if (currentItem != null) {
        var newBlockType = CubeTypes.fromId(currentItem.getId());
        if (newBlockType.isEmpty()) return false;

        if (currentItem.getCount() == 1) {
          character.getStorage().putStack(null, character.getStorage().getActiveSlot());
        } else {
          currentItem.setCount(currentItem.getCount() - 1);
        }

        gameMap.setCube(newBlockType.get(), nextCoords);
        var newBlock = gameMap.getCube(nextCoords);

        var placeData = new CubeSignal.BlockData(newBlock);
        SignalBus.call(SignalType.SignalTypes.CUBE_PLACE, placeData);
        if (placeData.isCancelled())
          return false;
      }
    } else if (button == Input.Buttons.MIDDLE) {
      var selectedItem = character.getStorage().getActiveStack();
      if (selectedItem != null) return false;

      var selectedSlot = character.getStorage().getActiveSlot();
      var itemType = Arrays.stream(ItemRegistry.values()).filter(stack -> stack.getId() == cube.getId()).findFirst();
      itemType.ifPresent(stacks -> character.getStorage().putStack(stacks.makeStack(), selectedSlot));
    }
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
