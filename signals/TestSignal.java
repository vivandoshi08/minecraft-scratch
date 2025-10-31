package io.cuber.vdcraft.signals;

import io.cuber.vdcraft.signals.basic.*;

public class TestSignal implements SignalReceiver {
  @Override
  public void onBlockBreak(CubeSignal.BlockData blockData) {
    System.out.println("Cube break: " + blockData.getCube().getId() + ", pos: " + blockData.getCube().getPos());
  }

  @Override
  public void onBlockPlaced(CubeSignal.BlockData blockData) {
    System.out.println("Cube place: " + blockData.getCube().getId() + ", pos: " + blockData.getCube().getPos());
  }

  @Override
  public void onBlockClicked(CubeSignal.BlockClickedData blockData) {
    System.out.println("Cube clicked: " + blockData.getCube().getId() + ", pos: " + blockData.getCube().getPos() + ", button: " + blockData.getButton());
  }

  @Override
  public void onKeyPress(KeySignal.KeyboardData keyData) {
    System.out.println("Key pressed " + keyData.getKeyCode());
  }

  @Override
  public void onKeyDown(KeySignal.KeyboardData keyData) {
    System.out.println("Key down: " + keyData.getKeyCode());
  }

  @Override
  public void onKeyUp(KeySignal.KeyboardData keyData) {
    System.out.println("Key up normal: " + keyData.getKeyCode());
  }

  @Signal(value = SignalType.SignalTypes.KEY_UP, priority = SignalPriority.HIGH)
  public void onKeyUp2(KeySignal.KeyboardData keyData) {
    System.out.println("Key up high: " + keyData.getKeyCode());
  }

  @Signal(value = SignalType.SignalTypes.KEY_UP, priority = SignalPriority.LOW)
  public void onKeyUp3(KeySignal.KeyboardData keyData) {
    System.out.println("Key up low: " + keyData.getKeyCode());
  }

  @Override
  public void onMove(CharacterSignal.CharacterSignalData eventData) {
    System.out.println("Character moved to " + eventData.getTargetPos());
  }

  @Override
  public void onMouseButtonPressed(MouseSignal.MousePressData buttonPressedData) {
    System.out.println("Mouse button pressed: " + buttonPressedData.getButtonPressed());
  }

  @Override
  public void onMouseMoved(MouseSignal.MouseMoveData mouseMoveData) {
  }

  @Override
  public void onScrolled(MouseSignal.MouseScrollData mouseScrollData) {
    mouseScrollData.setCancelled(true);
  }
}
