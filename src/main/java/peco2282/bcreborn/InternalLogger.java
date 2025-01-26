package peco2282.bcreborn;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalLogger {
  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);


  public static Logger create() {
    var clazz = STACK_WALKER.getCallerClass();
    String packageName = clazz.getPackageName().split("\\.")[2];

    String className = clazz.getSimpleName();

    String customName = packageName.toUpperCase() + "/" + className;
    return LoggerFactory.getLogger(customName);
  }
}
