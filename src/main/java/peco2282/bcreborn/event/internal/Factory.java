/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.event.internal;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import peco2282.bcreborn.api.event.BCEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Factory {
  private static final String HANDLER_DESC = Type.getInternalName(EventListener.class);
  private static final String HANDLER_FUNC_DESC =
      Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(BCEvent.class));
  private static final ASMClassLoader LOADER = new ASMClassLoader();
  private static final Cache cache = new Cache();
  private static final Factory instance = new Factory();

  private Factory() {}

  public static Factory getFactory() {
    return instance;
  }

  private static Class<?> defineClass(ClassNode node) {
    var cw = new ClassWriter(0);
    node.accept(cw);
    return LOADER.define(node.name.replace('/', '.'), cw.toByteArray());
  }

  private static void transformNode(String name, Method callback, ClassNode target) {
    MethodVisitor mv;

    String desc = name.replace('.', '/');
    String instType = Type.getInternalName(callback.getDeclaringClass());
    String eventType = Type.getInternalName(callback.getParameterTypes()[0]);

    target.visit(
        Opcodes.V16,
        Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
        desc,
        null,
        "java/lang/Object",
        new String[] {HANDLER_DESC});

    target.visitSource(".dynamic", null);

    {
      mv = target.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
      mv.visitCode();
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
      mv.visitInsn(Opcodes.RETURN);
      mv.visitMaxs(2, 2);
      mv.visitEnd();
    }
    {
      mv = target.visitMethod(Opcodes.ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
      mv.visitCode();
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitVarInsn(Opcodes.ALOAD, 1);
      mv.visitTypeInsn(Opcodes.CHECKCAST, eventType);
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          instType,
          callback.getName(),
          Type.getMethodDescriptor(callback),
          false);
      mv.visitInsn(Opcodes.RETURN);
      mv.visitMaxs(2, 2);
      mv.visitEnd();
    }
    target.visitEnd();
  }

  private static String getUniqueName(Method callback) {
    return String.format(
        "%s.__%s_%s_%s",
        callback.getDeclaringClass().getPackageName(),
        callback.getDeclaringClass().getSimpleName(),
        callback.getName(),
        callback.getParameterTypes()[0].getSimpleName());
  }

  public EventListener create(Method method, Object target) throws ReflectiveOperationException {
    Class<?> cls = createWrapper(method);
    if (Modifier.isStatic(method.getModifiers()))
      return (EventListener) cls.getDeclaredConstructor().newInstance();
    else return (EventListener) cls.getConstructor(Object.class).newInstance(target);
  }

  private static Class<?> createWrapper(Method callback) {
    return cache.computeIfAbsent(
        callback,
        () -> {
          var node = new ClassNode();
          transformNode(getUniqueName(callback), callback, node);
          return node;
        },
        Factory::defineClass);
  }

  private static class ASMClassLoader extends ClassLoader {
    private ASMClassLoader() {
      super(null);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve)
        throws ClassNotFoundException {
      return Class.forName(name, resolve, Thread.currentThread().getContextClassLoader());
    }

    Class<?> define(String name, byte[] data) {
      return defineClass(name, data, 0, data.length);
    }
  }
}
