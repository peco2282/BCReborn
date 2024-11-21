package peco2282.bcreborn.model.func;

import net.minecraft.core.Direction;

public class Context {
  public static IBooleanParser booleanParser() {
    return Boolean::parseBoolean;
  }

  public static IIntegerParser integerParser() {
    return Integer::parseInt;
  }

  public static IDirectionParser directionParser() {
    return Direction::valueOf;
  }

  public static  <E extends Enum<E>> IGenericParser<E> enumParser(Class<E> enumClass) {
    return name -> E.valueOf(enumClass, name);
  }
}
