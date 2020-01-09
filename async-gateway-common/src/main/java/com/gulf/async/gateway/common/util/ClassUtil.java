package com.gulf.async.gateway.common.util;

import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by xubai on 2019/07/29 3:40 PM.
 */
public final class ClassUtil {

    private ClassUtil() {
    }

    private final static String CODE_ARRAY_SUFFIX = "[]";

    private final static String BYTE_CODE_ARRAY_SUFFIX = "[L";

    private final static Map<String, Class<?>> primitiveTypeClassMap = new ConcurrentHashMap<>(16);

    private final static Map<String, Class<?>> primitiveWrapperClassMap = new ConcurrentHashMap<>(16);

    private final static Map<Class<?>/*wrapper type*/, Class<?>/*primitive type*/> primitiveWrapperTypeClassMap = new ConcurrentHashMap<>(8);

    private static String classPath = "";

    static {
        primitiveWrapperTypeClassMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeClassMap.put(Byte.class, byte.class);
        primitiveWrapperTypeClassMap.put(Character.class, char.class);
        primitiveWrapperTypeClassMap.put(Double.class, double.class);
        primitiveWrapperTypeClassMap.put(Float.class, float.class);
        primitiveWrapperTypeClassMap.put(Integer.class, int.class);
        primitiveWrapperTypeClassMap.put(Long.class, long.class);
        primitiveWrapperTypeClassMap.put(Short.class, short.class);

        Set<Class<?>> primitiveTypeNames = new HashSet<Class<?>>(16);
        primitiveTypeNames.addAll(primitiveWrapperTypeClassMap.values());
        primitiveTypeNames.addAll(Arrays
                .asList(new Class<?>[]{boolean[].class, byte[].class, char[].class, double[].class,
                        float[].class, int[].class, long[].class, short[].class}));
        for (Iterator<Class<?>> it = primitiveTypeNames.iterator(); it.hasNext(); ) {
            Class<?> primitiveClass = (Class<?>) it.next();
            primitiveTypeClassMap.put(primitiveClass.getName(), primitiveClass);
        }

        Set<Class<?>> primitiveWrapperTypeNames = new HashSet<Class<?>>(16);
        primitiveWrapperTypeNames.addAll(primitiveWrapperTypeClassMap.keySet());
        primitiveWrapperTypeNames.addAll(Arrays
                .asList(new Class<?>[]{Boolean[].class, Byte[].class, Character[].class, Double[].class,
                        Float[].class, Integer[].class, Long[].class, Short[].class}));
        for (Iterator<Class<?>> it = primitiveWrapperTypeNames.iterator(); it.hasNext(); ) {
            Class<?> primitiveClass = (Class<?>) it.next();
            primitiveWrapperClassMap.put(primitiveClass.getName(), primitiveClass);
        }


        classPath = loadClassPath();
    }

    public static ClassLoader getDefaultClassLoader(){
        ClassLoader cl = null;
        try {
            // get current Thread context ClassLoader
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            //NOOP
        }
        if (cl == null){
            // get current class ClassLoader
            cl = ClassUtil.class.getClassLoader();
            if (cl == null){
                // get bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    //NOOP
                }
            }
        }
        return cl;
    }

    public static Class<?> forName(String name)throws ClassNotFoundException{
        if (name == null || "".equals(name)){
            return null;
        }
        return forName(name, getDefaultClassLoader());
    }

    public static Class<?> forName(String name, ClassLoader classLoader)throws ClassNotFoundException {
        if (name == null || "".equals(name)) {
            return null;
        }
        Class<?> result = null;
        if (name.length() < 8) {
            result = primitiveTypeClassMap.get(name);
        }
        //primitive type
        if (result != null) {
            return result;
        }
        //java.lang.String[]
        if (name.endsWith(CODE_ARRAY_SUFFIX)) {
            String realClassName = name.substring(0, name.length() - CODE_ARRAY_SUFFIX.length());
            Class<?> realClass = forName(realClassName, getDefaultClassLoader());
            return Array.newInstance(realClass, 0).getClass();
        }
        //[Ljava.lang.String;
        int index = name.indexOf(BYTE_CODE_ARRAY_SUFFIX);
        if (index != -1 && name.endsWith(";")) {
            String realClassName = null;
            if (index == 0) {
                realClassName = name.substring(BYTE_CODE_ARRAY_SUFFIX.length(), name.length() - 1);
            } else if (name.startsWith("[")) {
                realClassName = name.substring(1);
            }
            Class<?> realClass = forName(realClassName, classLoader);
            return Array.newInstance(realClass, 0).getClass();
        }
        if (classLoader == null){
            classLoader = getDefaultClassLoader();
        }
        return classLoader.loadClass(name);
    }

    public static Map<String, Class<?>> getPrimitiveTypes(){
        return primitiveTypeClassMap;
    }

    public static boolean isPrimitiveWrapperType(String className){
        return primitiveWrapperClassMap.containsKey(className);
    }

    public static String getClassPath(){
        return classPath;
    }

    private static String loadClassPath(){
        String classPath = null;
        ClassLoader loader = getDefaultClassLoader();
        try {
            URL url = loader.getResource("");
            // get class path
            if (url != null) {
                classPath = url.getPath();
                classPath = URLDecoder.decode(classPath, "utf-8");
            }

            // 如果是jar包内的，则返回当前路径
            if (Strings.isNullOrEmpty(classPath) || classPath.contains(".jar!")) {
                classPath = System.getProperty("user.dir");
            }
        } catch (Throwable ex) {
            classPath = System.getProperty("user.dir");
        }
        return classPath;
    }

    public static List<Class<?>> getClassByInterface(Class<?> interfaceClass){
        return getClassByInterface(interfaceClass.getPackage().getName(), interfaceClass);
    }

    public static List<Class<?>> getClassByInterface(String packageName, Class<?> interfaceClass){
        ArrayList list = new ArrayList();
        if (interfaceClass.isInterface()){
            List<Class<?>> allClass = getClass(packageName);
            for (int i = 0; i < allClass.size(); i++) {
                if (interfaceClass.isAssignableFrom(allClass.get(i))) {
                    if (!interfaceClass.equals(allClass.get(i))) {//自身并不加进去
                        list.add(allClass.get(i));
                    }
                }
            }
        }
        return list;
    }

    public static List<Class<?>> getClass(String packageName) {
        String path = packageName.replaceAll("\\.", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ArrayList<Class<?>> classes = new ArrayList<>();
        try {
            Enumeration<URL> enumeration = classLoader.getResources(path);
            while (enumeration.hasMoreElements()){
                URL url = enumeration.nextElement();
                String protocol = url.getProtocol();
                if ("jar".equals(protocol)){
                    String jarpath = url.getPath();
                    jarpath = jarpath.replace("file:/", "");
                    jarpath = jarpath.substring(0, jarpath.indexOf("!"));
                    classes.addAll(getClasssFromJarFile(jarpath, path));
                }else if ("file".equals(protocol)){
                    File file = new File(url.getFile());
                    classes.addAll(findClass(file, packageName));
                }
            }
        } catch (Exception e) {
            //TODO
        }
        return classes;
    }

    public static List<Class<?>> findClass(File file, String packageName){
        ArrayList<Class<?>> classes = new ArrayList<>();
        if (file == null || !file.exists()){
            return classes;
        }
        File[] files = file.listFiles();
        for (File f : files){
            if (f.isDirectory()){
                classes.addAll(findClass(f, packageName+"."+f.getName()));
            }else if (f.getName().endsWith(".class")){
                try {
                    classes.add(Class.forName(packageName+"."+f.getName().substring(0, f.getName().indexOf(".class"))));
                } catch (ClassNotFoundException e) {
                    //NOOP
                }
            }
        }
        return classes;
    }

    public static List<Class<?>> getClasssFromJarFile(String jarpath, String path){
        ArrayList<Class<?>> classes = new ArrayList<>();
        JarFile jarFile = null;
        try{
            jarFile = new JarFile(jarpath);
            List<JarEntry> jarEntries = new ArrayList<>();
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()){
                JarEntry entry = enumeration.nextElement();
                if (!entry.isDirectory()
                        && entry.getName().startsWith(path) && entry.getName().endsWith(".class")){
                    jarEntries.add(entry);
                }
            }
            for (JarEntry entry : jarEntries){
                String className = entry.getName().replaceAll("/", ".");
                className = className.substring(0, className.indexOf(".class"));
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){

        }finally {
            if (jarFile != null){
                try {
                    jarFile.close();
                } catch (IOException e) {
                    //NOOP
                }
            }
        }
        return classes;
    }
}