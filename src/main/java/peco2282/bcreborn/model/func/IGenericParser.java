package peco2282.bcreborn.model.func;

@FunctionalInterface
public interface IGenericParser<T> extends INode {
  T parse(String value);

  static IIntegerParser integerParser() {
    return Context.integerParser();
  }

  static IBooleanParser booleanParser() {
    return Context.booleanParser();
  }

  static IDirectionParser directionParser() {
    return Context.directionParser();
  }

  static <E extends Enum<E>> IGenericParser<E> enumParser(Class<E> enumClass) {
    return Context.enumParser(enumClass);
  }
}
