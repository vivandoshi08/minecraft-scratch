package io.cuber.vdcraft;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.ScreenUtils;
import io.cuber.vdcraft.actors.character.Character;
import io.cuber.vdcraft.visuals.Overlay;
import io.cuber.vdcraft.visuals.StorageScreen;
import io.cuber.vdcraft.visuals.ScreenManager;
import io.cuber.vdcraft.visuals.config.OptionsScreen;
import io.cuber.vdcraft.visuals.debug.DebugDisplay;
import io.cuber.vdcraft.controls.*;
import io.cuber.vdcraft.objects.ItemRegistry;
import io.cuber.vdcraft.terrain.GameMap;

public class GameCore implements ApplicationListener {
  public final static float SPEED_FACTOR = 2;
  public static float SOUND_VOLUME = 0;
  public static boolean DEBUG_ACTIVE = false;
  private CharacterMover inputMovementHandler;
  private GameMap gameWorldInstance;
  private Character activePlayerEntity;
  private Overlay overlayDisplayManager;
  private GLProfiler renderingProfiler;
  private OptionsScreen configurationPanel;
  private Music backgroundAudioTrack;

  @Override
  public void create() {
    PerspectiveCamera viewportCamera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    viewportCamera.position.set(0f, 300f, 0f);
    viewportCamera.near = 0.5f;
    viewportCamera.far = 300f;
    viewportCamera.update();
    activePlayerEntity = new Character(viewportCamera);
    activePlayerEntity.toggleFly(true);

    int inventorySlotCounter = 0;
    for (var itemDefinition : ItemRegistry.values()) {
      if (inventorySlotCounter >= activePlayerEntity.getStorage().getSize()) {
        break;
      }
      var stackableItem = itemDefinition.makeStack();
      stackableItem.setCount(999);
      activePlayerEntity.getStorage().putStack(stackableItem, inventorySlotCounter++);
    }

    gameWorldInstance = new GameMap("VDCraft_World", activePlayerEntity);
    gameWorldInstance.create();

    overlayDisplayManager = new Overlay(new StorageScreen(activePlayerEntity.getStorage()));
    overlayDisplayManager.create(activePlayerEntity);

    Gdx.input.setCursorCatched(true);
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

    Gdx.gl20.glEnable(Gdx.gl20.GL_CULL_FACE);
    Gdx.gl20.glCullFace(Gdx.gl20.GL_BACK);
    InputMultiplexer combinedInputHandler = new InputMultiplexer();

    configurationPanel = new OptionsScreen(activePlayerEntity, backgroundAudioTrack);

    var interfaceInputHandler = new ScreenControls(new ScreenManager(overlayDisplayManager, configurationPanel), activePlayerEntity);
    inputMovementHandler = new CharacterMover(activePlayerEntity, gameWorldInstance);

    if (DEBUG_ACTIVE)
      combinedInputHandler.addProcessor(new DebugControls(overlayDisplayManager.getCam()));

    combinedInputHandler.addProcessor(configurationPanel.getStage());
    combinedInputHandler.addProcessor(interfaceInputHandler);
    combinedInputHandler.addProcessor(new SignalControls());
    combinedInputHandler.addProcessor(new GameControls(gameWorldInstance, activePlayerEntity));
    combinedInputHandler.addProcessor(inputMovementHandler);
    Gdx.input.setInputProcessor(combinedInputHandler);

    renderingProfiler = new GLProfiler(Gdx.graphics);
    renderingProfiler.enable();

  }

  public void render() {
    renderingProfiler.reset();

    float scaledFrameTime = Gdx.graphics.getDeltaTime() * SPEED_FACTOR;

    inputMovementHandler.update(scaledFrameTime);

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    ScreenUtils.clear(1.0f, 0.6f, 0.4f, 1);

    gameWorldInstance.render(activePlayerEntity.getCam(), scaledFrameTime);

    if (configurationPanel.isOpened())
      configurationPanel.render();

    overlayDisplayManager.render();

    if (DEBUG_ACTIVE) {
      var renderCallCount = renderingProfiler.getDrawCalls();
      var textureBindCount = renderingProfiler.getTextureBindings();
      var shaderSwitchCount = renderingProfiler.getShaderSwitches();
      var openGLCallCount = renderingProfiler.getCalls();
      var vertexCountData = renderingProfiler.getVertexCount();
      var framesPerSecond = Gdx.graphics.getFramesPerSecond();
      System.out.println("Render calls: " + renderCallCount + ", texture binds: " + textureBindCount + ", FPS: " + framesPerSecond + ", shader switches: " + shaderSwitchCount + ", GL calls: " + openGLCallCount + ", vertex count: " + vertexCountData.total);
    }
  }

  @Override
  public void dispose() {
    gameWorldInstance.dispose();
    overlayDisplayManager.dispose();
  }

  @Override
  public void resize(int width, int height) {
    activePlayerEntity.getCam().viewportHeight = height;
    activePlayerEntity.getCam().viewportWidth = width;
    activePlayerEntity.getCam().update();
    overlayDisplayManager.resize(width, height);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }
}
