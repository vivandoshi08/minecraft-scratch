package io.cuber.vdcraft.actors;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

@FunctionalInterface
public interface Renderable {
  ModelInstance getModel();
}
