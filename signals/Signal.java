package io.cuber.vdcraft.signals;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Signal {
  SignalType.SignalTypes value();
  SignalPriority priority() default SignalPriority.NORMAL;
}
