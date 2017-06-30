package de.exlll.configlib.classes;

import de.exlll.configlib.Configuration;

import java.nio.file.Path;
import java.util.*;

public class DefaultTypeClass extends Configuration {
    private boolean bool = true;
    private char c = 'c';
    private byte b = 1;
    private short s = 2;
    private int i = 3;
    private long l = 4;
    private float f = 5.0f;
    private double d = 6.0;
    private Boolean boolObject = true;
    private Character cObject = 'c';
    private Byte bObject = 1;
    private Short sObject = 2;
    private Integer iObject = 3;
    private Long lObject = 4L;
    private Float fObject = 5.0f;
    private Double dObject = 6.0;
    private String string = "string";
    private List<String> list = new ArrayList<>();
    private Set<String> set = new HashSet<>();
    private Map<String, String> map = new HashMap<>();

    public DefaultTypeClass(Path path) {
        super(path);
        list.add("a");
        set.add("b");
        map.put("c", "d");
    }

    public static Map<String, Object> newValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("bool", false);
        map.put("c", 'd');
        map.put("b", (byte) 2);
        map.put("s", (short) 3);
        map.put("i", 4);
        map.put("l", 5L);
        map.put("f", 6.0f);
        map.put("d", 7.0);
        map.put("boolObject", false);
        map.put("cObject", 'd');
        map.put("bObject", (byte) 2);
        map.put("sObject", (short) 3);
        map.put("iObject", 4);
        map.put("lObject", 5L);
        map.put("fObject", 6.0f);
        map.put("dObject", 7.0);
        map.put("string", "string2");
        List<String> list = new ArrayList<>();
        list.add("b");
        map.put("list", list);
        Set<String> set = new HashSet<>();
        set.add("c");
        map.put("set", set);
        Map<String, String> map2 = new HashMap<>();
        map2.put("d", "e");
        map.put("map", map2);
        return map;
    }
}
