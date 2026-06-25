/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common.event.internal;

import com.peco2282.bcreborn.api.events.BCEvent;
import com.peco2282.bcreborn.api.events.EventListener;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EventFactory {
  private static final String HANDLER_DESC = Type.getInternalName(EventListener.class);
  private static final String HANDLER_FUNC_DESC =
      Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(BCEvent.class));
  private static final ASMClassLoader LOADER = new ASMClassLoader();
  private static final Cache cache = new Cache();
  private static final EventFactory INSTANCE = new EventFactory();

  private EventFactory() {}

  public static EventFactory getFactory() {
    return INSTANCE;
  }

  private static Class<?> defineClass(ClassNode node) {
    var cw = new ClassWriter(0);
    node.accept(cw);
    return LOADER.define(node.name.replace('/', '.'), cw.toByteArray());
  }

  private static void transformNode(String name, Method callback, ClassNode target, boolean isStatic) {
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

    if (!isStatic) {
      // インスタンスを保持するフィールドを追加
      target.visitField(Opcodes.ACC_PRIVATE, "instance", "Ljava/lang/Object;", null, null).visitEnd();

      // Object引数付きコンストラクタ
      mv = target.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
      mv.visitCode();
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitVarInsn(Opcodes.ALOAD, 1);
      mv.visitFieldInsn(Opcodes.PUTFIELD, desc, "instance", "Ljava/lang/Object;");
      mv.visitInsn(Opcodes.RETURN);
      mv.visitMaxs(2, 2);
      mv.visitEnd();
    } else {
      // デフォルトコンストラクタ
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
      if (isStatic) {
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitTypeInsn(Opcodes.CHECKCAST, eventType);
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            instType,
            callback.getName(),
            Type.getMethodDescriptor(callback),
            false);
      } else {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, desc, "instance", "Ljava/lang/Object;");
        mv.visitTypeInsn(Opcodes.CHECKCAST, instType);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitTypeInsn(Opcodes.CHECKCAST, eventType);
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            instType,
            callback.getName(),
            Type.getMethodDescriptor(callback),
            false);
      }
      mv.visitInsn(Opcodes.RETURN);
      mv.visitMaxs(3, 2);
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

  public EventListener create(Method method, @Nullable Object target) throws ReflectiveOperationException {
    boolean isStatic = Modifier.isStatic(method.getModifiers());
    if (isStatic && target != null)
      throw new IllegalArgumentException("Static method cannot have instance target");
    if (!isStatic && target == null)
      throw new IllegalArgumentException("Non-static method must have instance target");
    Class<?> cls = createWrapper(method, isStatic);
    if (isStatic)
      return (EventListener) cls.getDeclaredConstructor().newInstance();
    else return (EventListener) cls.getConstructor(Object.class).newInstance(target);
  }

  private static Class<?> createWrapper(Method callback, boolean isStatic) {
    return cache.computeIfAbsent(
        callback,
        () -> {
          var node = new ClassNode();
          transformNode(getUniqueName(callback), callback, node, isStatic);
          return node;
        },
        EventFactory::defineClass);
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
