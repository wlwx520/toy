package com.track.toy.copy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自实现的深度对象copy方法
 * <p>
 * 默认对应非jdk的类进行反射深度copy
 * <p>
 * 如有{@link CopySkip}注解，则跳过该属性的copy
 * 如有{@link CopyReference}注解，则直接copy该属性的引用，即不执行深度copy
 * 如有{@link CopyFiled}注解，根据注解的value数组从copy的最外层对象寻找和value数组中名字相同的数据进行copy
 * 如该注解有自定义{@link CopyCustom}，可以更加该接口内自定义方法进行copy
 * <p>
 * 如copy时遇到Set，Map，List，默认使用HashSet,HashMap,ArrayList，进行深度copy
 * 如需使用实现了Set的其他Set，请使用{@link CopySet},并提供无参默认构造器
 * 如需使用实现了List的其他List，请使用{@link CopyList},并提供无参默认构造器
 * 如需使用实现了Map的其他Map，请使用{@link CopyMap},并提供无参默认构造器
 * <p>
 * 在整个深度copy过程中，如被copy对应中的两个属性指向同一个对象，在深度copy后的对象也会使对象属性指向同一对象，
 * 并且是包括属性不在同一层里（如对象有个属性指向a，在a同层有个Map，Map的value里也有个指向a，深度copy后也将保持该关系）
 * <p>
 * 以上所有注解均含有key的值，如key为空白，默认在所有情况下使用该注解，如key包含"test"，则只有在调用包含String key的重载方法，并设置为test的才会使用该注解
 * <p>
 * 提供对应List和Set的深度copy
 */
public class BeanCopyUtil {
    public static <S, T> List<T> deepCopy(Class<T> targetClz, List<S> sourceList) {
        return deepCopy(targetClz, sourceList, "");
    }

    public static <S, T> List<T> deepCopy(Class<T> targetClz, List<S> sourceList, String key) {
        if (sourceList == null) {
            return null;
        }
        if (sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        return sourceList.stream().map(source -> deepCopy(targetClz, source, key)).collect(Collectors.toList());
    }

    public static <S, T> Set<T> deepCopy(Class<T> targetClz, Set<S> sourceSet) {
        return deepCopy(targetClz, sourceSet, "");
    }

    public static <S, T> Set<T> deepCopy(Class<T> targetClz, Set<S> sourceList, String key) {
        if (sourceList == null) {
            return null;
        }
        if (sourceList.isEmpty()) {
            return new HashSet<>();
        }
        return sourceList.stream().map(source -> deepCopy(targetClz, source, key)).collect(Collectors.toSet());
    }

    public static <S> S deepCopy(S source) {
        Class<S> targetClz = (Class<S>) source.getClass();
        return deepCopy(targetClz, source);
    }

    public static <S, T> T deepCopy(Class<T> targetClz, S source) {
        return deepCopy(targetClz, source, source, targetClz, new HashMap<>(), "");
    }

    public static <S, T> T deepCopy(Class<T> targetClz, S source, String key) {
        return deepCopy(targetClz, source, key);
    }

    private static <S, T> T deepCopy(Class<T> targetClz, S source, Object sRoot, Class<?> tRootClz, Map<Object, Object> beans, String key) {
        try {
            if (isNormalClass(targetClz)) {
                return (T) source;
            }
            if (beans.containsKey(source)) {
                return (T) beans.get(source);
            }
            Constructor<T> constructor = targetClz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T target = constructor.newInstance();
            beans.put(source, target);
            deepCopy(target, source, sRoot, tRootClz, beans, key);
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static <S, T> void deepCopy(T target, S source, Object sRoot, Class<?> tRootClz, Map<Object, Object> beans, String key) {
        try {
            Class<?> sourceClz = source.getClass();
            Class<?> targetClz = target.getClass();

            Map<String, Field> sourceFields = getAllFieldsFromSupperClass(sourceClz);
            Map<String, Field> targetFields = getAllFieldsFromSupperClass(targetClz);

            for (String key0 : targetFields.keySet()) {
                try {
                    Field sField = sourceFields.get(key0);
                    Field tField = targetFields.get(key0);

                    // skip
                    if (getAnnotation(tField, CopySkip.class, key) != null) {
                        continue;
                    }

                    Object obj = null;
                    //other field
                    CopyFiled copyFiled = getAnnotation(tField, CopyFiled.class, key);
                    Class<? extends CopyCustom> handlerClz = CopyCustom.DefaultCopyCustom.class;
                    if (copyFiled != null) {
                        String[] values = copyFiled.value();
                        if (values.length == 0) {
                            values = new String[]{key};
                        }
                        obj = sRoot;
                        Field tempField = null;
                        for (String value : values) {
                            tempField = obj.getClass().getDeclaredField(value);
                            tempField.setAccessible(true);
                            obj = tempField.get(obj);
                        }
                        sField = tempField;
                        handlerClz = copyFiled.handler();
                    }

                    // null
                    if (sField == null || tField == null) {
                        continue;
                    }

                    sField.setAccessible(true);
                    tField.setAccessible(true);

                    Object sValue = copyFiled != null
                            ? obj
                            : sField.get(source);
                    Class<?> tFieldType = tField.getType();

                    // custom handler
                    if (!handlerClz.equals(CopyCustom.DefaultCopyCustom.class)) {
                        Constructor<? extends CopyCustom> handlerClzConstructor = handlerClz.getDeclaredConstructor();
                        handlerClzConstructor.setAccessible(true);
                        CopyCustom copyCustom = handlerClzConstructor.newInstance();
                        tField.set(target, copyCustom.copyCustom(sValue));
                        continue;
                    }

                    // type error
                    if (!tFieldType.isAssignableFrom(sField.getType())) {
                        continue;
                    }

                    // the same bean
                    if (beans.containsKey(sValue)) {
                        tField.set(target, beans.get(sValue));
                        continue;
                    }

                    // reference
                    if (getAnnotation(tField, CopyReference.class, key) != null) {
                        tField.set(target, sValue);
                        continue;
                    }

                    // list
                    if (List.class.isAssignableFrom(tFieldType)) {
                        Class<?> actualClass = getActualClass(tField);
                        List sourceList = (List) sValue;
                        CopyList copyList = getAnnotation(tField, CopyList.class, key);
                        List targetList = copyList != null
                                ? copyList.value().newInstance()
                                : new ArrayList();
                        for (Object subSource : sourceList) {
                            targetList.add(deepCopy(actualClass, subSource, sRoot, tRootClz, beans, key));
                        }
                        tField.set(target, targetList);
                        beans.put(sourceList, targetList);
                        continue;
                    }

                    // set
                    if (Set.class.isAssignableFrom(tFieldType)) {
                        Class<?> actualClass = getActualClass(tField);
                        Set sourceSet = (Set) sValue;
                        CopySet copySet = getAnnotation(tField, CopySet.class, key);
                        Set targetSet = copySet != null
                                ? copySet.value().newInstance()
                                : new HashSet();
                        for (Object subSource : sourceSet) {
                            targetSet.add(deepCopy(actualClass, subSource, sRoot, tRootClz, beans, key));
                        }
                        tField.set(target, targetSet);
                        beans.put(sourceSet, targetSet);
                        continue;
                    }

                    // map
                    if (Map.class.isAssignableFrom(tFieldType)) {
                        Class<?> actualClass = getActualClass(tField);
                        Class<?> actualClassSecond = getActualClassSecond(tField);
                        Map sourceMap = (Map) sValue;
                        CopyMap copyMap = getAnnotation(tField, CopyMap.class, key);
                        Map targetMap = copyMap != null
                                ? copyMap.value().newInstance()
                                : new HashMap();
                        for (Map.Entry subEntry : (Set<Map.Entry>) sourceMap.entrySet()) {
                            Object subKey = deepCopy(actualClass, subEntry.getKey(), sRoot, tRootClz, beans, key);
                            Object subValue = deepCopy(actualClassSecond, subEntry.getValue(), sRoot, tRootClz, beans, key);
                            targetMap.put(subKey, subValue);
                        }
                        tField.set(target, targetMap);
                        beans.put(sourceMap, targetMap);
                        continue;
                    }

                    // object
                    tField.set(target, deepCopy(sField.getType(), sValue, sRoot, tRootClz, beans, key));
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getActualClass(Field field) {
        Type type = field.getGenericType();
        ParameterizedType p = (ParameterizedType) type;
        Class<?> actualClass = (Class) p.getActualTypeArguments()[0];
        return actualClass;
    }

    private static Class<?> getActualClassSecond(Field tField) {
        Type type = tField.getGenericType();
        ParameterizedType p = (ParameterizedType) type;
        Class<?> actualClass = (Class) p.getActualTypeArguments()[1];
        return actualClass;
    }

    private static boolean isNormalClass(Class<?> clz) {
        return clz != null && clz.getClassLoader() == null;
    }

    private static Map<String, Field> getAllFieldsFromSupperClass(Class<?> clz) {
        Map<String, Field> fields = new HashMap<>();
        getAllFieldsFromSupperClass(clz, fields);
        return fields;
    }

    private static void getAllFieldsFromSupperClass(Class<?> clz, Map<String, Field> fields) {
        if (isNormalClass(clz)) {
            return;
        }

        for (Field field : clz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fields.put(field.getName(), field);
            }
        }

        getAllFieldsFromSupperClass(clz.getSuperclass(), fields);
    }

    private static <E extends Annotation> E getAnnotation(Field field, Class<E> annotation, String key) {
        if (field == null || annotation == null) {
            return null;
        }
        E e = field.getAnnotation(annotation);
        if (e == null) {
            return null;
        }
        if (key.equals("")) {
            return e;
        }
        if (annotation.equals(CopySkip.class)) {
            String[] classes = ((CopySkip) e).key();
            if (classes.length == 0) {
                return e;
            }
            for (String aClz : classes) {
                if (aClz.equals(key)) {
                    return e;
                }
            }
        }
        if (annotation.equals(CopyFiled.class)) {
            String[] classes = ((CopyFiled) e).key();
            if (classes.length == 0) {
                return e;
            }
            for (String aClz : classes) {
                if (aClz.equals(key)) {
                    return e;
                }
            }
        }
        if (annotation.equals(CopyReference.class)) {
            String[] classes = ((CopyReference) e).key();
            if (classes.length == 0) {
                return e;
            }
            for (String aClz : classes) {
                if (aClz.equals(key)) {
                    return e;
                }
            }
        }
        if (annotation.equals(CopyList.class)) {
            String[] classes = ((CopyList) e).key();
            if (classes.length == 0) {
                return e;
            }
            for (String aClz : classes) {
                if (aClz.equals(key)) {
                    return e;
                }
            }
        }
        if (annotation.equals(CopySet.class)) {
            String[] classes = ((CopySet) e).key();
            if (classes.length == 0) {
                return e;
            }
            for (String aClz : classes) {
                if (aClz.equals(key)) {
                    return e;
                }
            }
        }
        if (annotation.equals(CopyMap.class)) {
            String[] classes = ((CopyMap) e).key();
            if (classes.length == 0) {
                return e;
            }
            for (String aClz : classes) {
                if (aClz.equals(key)) {
                    return e;
                }
            }
        }
        return null;
    }
}
