package io.cuber.vdcraft.signals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class SignalBus {
  private static final SignalBus INSTANCE = new SignalBus();

  private final Map<SignalType.SignalTypes, List<SignalWrapper>> subscribers = new HashMap<>();

  private SignalBus() {
    if (INSTANCE != null) {
        throw new AssertionError("Singleton already instanced");
    }
  }

  public static SignalBus getInstance() {
    return INSTANCE;
  }

  public static void register(Object subscriber) {
      INSTANCE.subscribe(subscriber);
  }

  public static void call(SignalType.SignalTypes signalType, SignalData signalData) {
    INSTANCE.publish(signalType, signalData);
  }

  public void publish(SignalType.SignalTypes signalType, SignalData signalData) {
    var signalSubscribers = subscribers.getOrDefault(signalType, new ArrayList<>());
    signalSubscribers.sort(Comparator.comparing(SignalWrapper::getPriority));
    signalSubscribers.forEach(s -> s.call(signalData));
  }

  public void subscribe(Object subscriber) {
    for (var method : subscriber.getClass().getMethods()) {
        if (!method.isAnnotationPresent(Signal.class)) continue;
        if (method.getParameterCount() != 1) continue;

        var parameter = method.getParameters()[0];
        if (!SignalData.class.isAssignableFrom(parameter.getType())) continue;

        Signal signalAnnotation = method.getAnnotation(Signal.class);
        SignalType.SignalTypes signalType = signalAnnotation.value();
        SignalPriority priority = signalAnnotation.priority();

        var signalSubscribers = subscribers.computeIfAbsent(signalType, k -> new ArrayList<>());
        signalSubscribers.add(new SignalWrapper(subscriber, method, priority));
    }
  }

  public record SignalWrapper(Object instance, Method method, SignalPriority priority) {
    public void call(SignalData data) {
      try {
        method.invoke(instance, data);
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    public SignalPriority getPriority() {
      return priority;
    }
  }
}
