package dev.beecube31.crazyae2.common.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MirrorUtils {
    @Nullable private static Field fField_modifiers = null;

    public static void writeModifiers(Field field, int modifiers) {
        if (fField_modifiers == null) {
            try {
                fField_modifiers = Field.class.getDeclaredField("modifiers");
                fField_modifiers.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException("Failed to reflect field modifiers!", e);
            }
        }
        try {
            fField_modifiers.setInt(field, modifiers);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write field modifieres!", e);
        }
    }

    public static <T> List<Class<? super T>> getHierarchy(Class<T> clazz) {
        List<Class<? super T>> result = new ArrayList<>();
        Class<? super T> current = clazz;
        while (current != null) {
            result.add(current);
            current = current.getSuperclass();
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> IMethod<T> reflectMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);
            return new IMethod.Impl(method);
        } catch (NoSuchMethodException e) {
            throw new MirrorException(String.format("Could not reflect method: %s/%s %s", clazz.getName(), name, Arrays.toString(args)), e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> IField<T> reflectField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return new IField.Impl(field);
        } catch (NoSuchFieldException e) {
            throw new MirrorException(String.format("Could not reflect field: %s/%s", clazz.getName(), name), e);
        }
    }

    public interface IMethod<T> {
        @SuppressWarnings("unchecked")
        default T invoke(@Nullable Object target, Object... args) {
            try {
                return (T)unwrap().invoke(target, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MirrorException("Could not invoke method: " + unwrap(), e);
            }
        }

        Method unwrap();

        class Impl<T> implements IMethod<T> {
            private final Method method;

            Impl(Method method) {
                this.method = method;
            }

            @Override
            public Method unwrap() {
                return method;
            }
        }
    }

    public interface IField<T> {
        default void set(@Nullable Object target, T value) {
            try {
                Field field = unwrap();
                int modifiers = field.getModifiers();
                if ((modifiers & Modifier.FINAL) != 0) {
                    writeModifiers(field, modifiers & ~Modifier.FINAL);
                }
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new MirrorException("Could not mutate field: " + unwrap(), e);
            }
        }

        @SuppressWarnings("unchecked")
        default T get(@Nullable Object target) {
            try {
                return (T)unwrap().get(target);
            } catch (IllegalAccessException e) {
                throw new MirrorException("Could not read field: " + unwrap(), e);
            }
        }

        Field unwrap();

        class Impl<T> implements IField<T> {
            private final Field field;

            Impl(Field field) {
                this.field = field;
            }

            @Override
            public Field unwrap() {
                return field;
            }
        }
    }

    public static class MirrorException extends RuntimeException {
        MirrorException(String reason, Exception cause) {
            super(reason, cause);
        }
    }
}
