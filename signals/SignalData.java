package io.cuber.vdcraft.signals;

public abstract class SignalData {
  private boolean isCancelled = false;

  public boolean isCancelled() {
    return isCancelled;
  }

  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }
}
