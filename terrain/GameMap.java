package io.cuber.vdcraft.terrain;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import io.cuber.vdcraft.GameCore;
import io.cuber.vdcraft.actors.Actor;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.display.MapRenderer;

import java.util.List;
import java.util.Random;

public class GameMap {
  public static final int CUBE_SIZE = 4;
  private final Character character;
  private final Array<Actor> entities = new Array<>();
  private final String name;
  public boolean collision = false;
  private Environment environment;
  private ModelBatch batch;
  private MapRenderer testWorld;
  private int lastCollisionCheck = 0;

  public GameMap(String name, Character character) {
    this.name = name;
    this.character = character;
    entities.add(character);
  }

  public void create() {
    DefaultShader.Config config = new DefaultShader.Config();

    batch = new ModelBatch(new DefaultShaderProvider(config));
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    testWorld = new MapRenderer(character, 121);

    TerrainBuilder worldGenerator = new TerrainBuilder();
    for (int i = 0; i < 121; i++) {
      worldGenerator.buildSection(testWorld.getSections()[i]);
    }
  }

  public void render(Camera cam, float gameDeltaTime) {
    cam.update();

    for (var entity : new Array.ArrayIterator<>(entities)) {
      if (entity instanceof Character p && (p.canFly() || p.isInAir()))
        continue;

      if (!collision)
        entity.move(new Vector3(0, -10F * entity.getGravityCoefficient(), 0).scl(gameDeltaTime));

      if (lastCollisionCheck >= 5) {
        collision = hasCollision(entity.getPos().scl(GameMap.CUBE_SIZE));
        lastCollisionCheck = 0;
      }

      if (entity.getPos().y < -10) {
        entity.setPos(entity.getPos().add(0, 100, 0));
      }
    }
    lastCollisionCheck++;

    batch.begin(cam);

    batch.render(testWorld, environment);

    batch.end();
  }

  public boolean hasCollision(Vector3 pos) {

    var cubes = testWorld.getNearbyCubes(pos.cpy().scl(1 / (float) GameMap.CUBE_SIZE), 5);
    if (GameCore.DEBUG_ACTIVE)
      System.out.println("CubeTypes n°: " + cubes.size() + ", pos : " + pos);

    var playerBounds = new BoundingBox();
    playerBounds.set(new Vector3(-1.5F, -8, -1.5F), new Vector3(1.5F, 0, 1.5F));
    playerBounds.mul(new Matrix4().setToTranslation(pos));

    for (var cube : cubes) {
      boolean result = playerBounds.intersects(cube.getHitbox());

      if (result)
        return true;

    }
    return false;
  }

  public boolean hasCollision(Vector3 playerPosition, Vector3 targetPosition) {
    var playerBounds = new BoundingBox();
    playerBounds.set(new Vector3(-1.5F, -8, -1.5F), new Vector3(1.5F, 1, 1.5F));
    playerBounds.mul(new Matrix4().setToTranslation(playerPosition));

    var blockBounds = new BoundingBox();
    blockBounds.set(new Vector3(0, 0, 0), new Vector3(4, 4, 4));
    blockBounds.mul(new Matrix4().setToTranslation(targetPosition));

    return playerBounds.intersects(blockBounds);
  }

  public void dispose() {
    batch.dispose();
  }

  public Cube setCube(CubeTypes cube, Vector3 pos) {
    Cube newBlock = cube.makeCube(pos.x, pos.y, pos.z);
    testWorld.setCube((int) pos.x, (int) pos.y, (int) pos.z, newBlock);
    return newBlock;
  }

  public void removeCube(Cube cube) {
    testWorld.removeCube((int) cube.getPos().x, (int) cube.getPos().y, (int) cube.getPos().z);
  }

  public List<Cube> getNearbyCubes(Vector3 pos) {
    return testWorld.getNearbyCubes(pos);
  }

  public Cube getCube(Vector3 pos) {
    return testWorld.getCube((int) pos.x, (int) pos.y, (int) pos.z);
  }

  public String getName() {
    return name;
  }
}
