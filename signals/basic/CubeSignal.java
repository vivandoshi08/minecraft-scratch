package io.cuber.vdcraft.signals.basic;

import io.cuber.vdcraft.signals.SignalGroup;
import io.cuber.vdcraft.signals.SignalData;
import io.cuber.vdcraft.signals.SignalType;
import io.cuber.vdcraft.terrain.Cube;

public interface CubeSignal extends SignalGroup {
  @SignalType(value = SignalType.SignalTypes.CUBE_BREAK)
  void onBlockBreak(BlockData blockData);

  @SignalType(value = SignalType.SignalTypes.CUBE_PLACE)
  void onBlockPlaced(BlockData blockData);

  void onBlockClicked(BlockClickedData blockData);

  class BlockData extends SignalData {

    private final Cube cube;

    public BlockData(Cube cube) {
      this.cube = cube;
    }

    public Cube getCube() {
      return cube;
    }
  }

  @SignalType(value = SignalType.SignalTypes.CUBE_CLICK)
  final class BlockClickedData extends BlockData {
    private final int button;

    public BlockClickedData(Cube cube, int button) {
      super(cube);
      this.button = button;
    }

    public int getButton() {
      return button;
    }
  }
}
