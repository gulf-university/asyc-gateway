package com.gulf.async.gateway.common.spi;

import com.gulf.async.gateway.common.Constants;
import com.gulf.async.gateway.common.exception.GatewayException;
import com.gulf.async.gateway.common.util.ClassUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by xubai on 2019/07/29 11:47 AM.
 */
public class SpiLoader<T> {

    private static final String SPI_PATH = "META-INF/services/";

    private static final ConcurrentMap<Class<?>, SpiLoader<?>> SPI_LOADERS = new ConcurrentHashMap<Class<?>, SpiLoader<?>>();
    private static final ConcurrentMap<Class<?>, Object> SPI_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

    private static final Comparator<Object> ASC_COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            Activate a1 = o1.getClass().getAnnotation(Activate.class);
            Activate a2 = o2.getClass().getAnnotation(Activate.class);
            if (a1 == null || a2 == null){
                return 0;
            }
            if (a1.sort() > a2.sort()){
                return 1;
            }else if (a1.sort() < a2.sort()){
                return -1;
            }
            return 0;
        }
    };


    private final Class<T> type;

    public SpiLoader(Class<T> type){
        this.type = type;
        loadSpiInstances();
    }

    //获取spi loader
    public static <T> SpiLoader<T> getSpiLoader(Class<T> type){
        if (type == null) {
            throw new IllegalArgumentException("com.async.gateway.register.spi type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("com.async.gateway.register.spi type(" + type + ") is not interface!");
        }
        // 必须包含 @SPI 注解
        if (!withSpiAnnotation(type)) {
            throw new IllegalArgumentException("com.async.gateway.register.spi type(" + type +
                    ") is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");
        }
        if (!SPI_LOADERS.containsKey(type)){
            SPI_LOADERS.putIfAbsent(type, new SpiLoader<T>(type));
        }
        return (SpiLoader<T>)SPI_LOADERS.get(type);
    }

    //获取激活的spi
    public List<T> getAdativateSpi(String group){
        if (group == null || "".equals(group)){
            return null;
        }
        List<T> spiList = (List<T>)SPI_INSTANCES.values().stream().filter(o -> {
            Activate activate = o.getClass().getAnnotation(Activate.class);
            return Arrays.stream(activate.group()).anyMatch(g -> g.equalsIgnoreCase(group));
        }).collect(Collectors.toList());
        Collections.sort(spiList, ASC_COMPARATOR);
        return spiList;
    }

    //获取指定tag的spi
    public List<T> getAdativateSpi(String group, String tag){
        if (group == null || "".equals(group)){
            return null;
        }
        List<T> groupList = getAdativateSpi(group);
        return groupList.stream().filter(o -> {
                Activate a = o.getClass().getAnnotation(Activate.class);
                return Arrays.stream(a.tag()).anyMatch(t -> t.equalsIgnoreCase(tag));
            }).collect(Collectors.toList());
    }



    private static <T> boolean withSpiAnnotation(Class<T> type) {
        return type.isAnnotationPresent(SPI.class);
    }

    private void loadSpiInstances(){
        try {
            List<String> spiClassList = loadSpiClasses();
            createSpiInstance(spiClassList);
        } catch (Exception e) {
            throw new GatewayException("SpiLoader load com.async.gateway.register.spi:"+type.getName()+" instance exception", e);
        }
    }

    private void createSpiInstance(List<String> list)throws Exception{
        for (String c : list){
            try {
                Class<?> clazz = ClassUtil.forName(c);
                if (clazz.isAnnotationPresent(Activate.class)){
                    Object instance = clazz.newInstance();
                    SPI_INSTANCES.putIfAbsent(clazz, instance);
                }
            } catch (ClassNotFoundException e) {
            } catch (Exception ex){
                throw new GatewayException("SpiLoader create instance:"+c+" exception", ex);
            }
        }
    }

    private List<String> loadSpiClasses()throws Exception{
        String spiFile = SPI_PATH + type.getName();
        List<String> classList = new ArrayList<>();
        try {
            ClassLoader cl = ClassUtil.getDefaultClassLoader();
            Enumeration<URL> urls = (cl != null) ? cl.getResources(spiFile) : ClassLoader.getSystemResources(spiFile);
            if (urls != null){
                while (urls.hasMoreElements()){
                    URL url = urls.nextElement();
                    List<String> classNames = parseFile(url);
                    classList.addAll(classNames);
                }
            }
        } catch (Exception e) {
            throw new GatewayException("SpiLoader load com.async.gateway.register.spi:"+type.getName()+" extensions exception");
        }
        return classList;
    }

    private List<String> parseFile(URL url)throws Exception{
        InputStream inputStream = null;
        BufferedReader reader = null;
        List<String> classNames = new ArrayList<>();
        try{
            inputStream = url.openStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Constants.UTF_8));
            String line = null;
            int indexNumber = 0;

            while ((line = reader.readLine()) != null) {
                indexNumber++;
                try {
                    String className = parseLine(line, indexNumber);
                    if (className != null && !"".equals(className)){
                        classNames.add(className);
                    }
                } catch (Exception e) {
                    throw new GatewayException(e);
                }
            }
        }catch (Throwable e){
            throw new GatewayException("SpiLoader parse file:"+url.getPath()+" exception", e);
        }finally {
            try {
                reader.close();
                inputStream.close();
            } catch (Exception e) {
                //NOOP
            }
        }
        return classNames;
    }

    private String parseLine(String line, int indexNumber)throws Exception{
        int ci = line.indexOf('#');

        if (ci >= 0) {
            line = line.substring(0, ci);
        }

        line = line.trim();

        if (line.length() <= 0) {
            return null;
        }

        if ((line.indexOf(' ') >= 0) || (line.indexOf('\t') >= 0)) {
            throw new GatewayException("parse line:"+indexNumber+" start with ' '.");
        }

        int cp = line.codePointAt(0);
        if (!Character.isJavaIdentifierStart(cp)) {
            throw new GatewayException("parse line:"+indexNumber+" start with no java identifer.");
        }

        for (int i = Character.charCount(cp); i < line.length(); i += Character.charCount(cp)) {
            cp = line.codePointAt(i);
            if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                throw new GatewayException("parse line:"+indexNumber+" com.async.gateway.register.spi class contains no java identifer.");
            }
        }
        return line;
    }

}
