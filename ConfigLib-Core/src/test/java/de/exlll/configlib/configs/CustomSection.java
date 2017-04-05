package de.exlll.configlib.configs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSection {
    private Map<String, List<String>> map = new HashMap<>();
    private String s1 = "s1";
    private int i1 = 1;
    private CustomObject o1 = new CustomObject();
    private CustomObject o2 = new CustomObject();

    public CustomSection() {
        map.put("xa", Arrays.asList("x1", "x2"));
        map.put("ya", Arrays.asList("y1", "y2"));
        map.put("za", Arrays.asList("z1", "z2"));
    }

    public Map<String, List<String>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<String>> map) {
        this.map = map;
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public int getI1() {
        return i1;
    }

    public void setI1(int i1) {
        this.i1 = i1;
    }

    public CustomObject getO1() {
        return o1;
    }

    public void setO1(CustomObject o1) {
        this.o1 = o1;
    }

    public CustomObject getO2() {
        return o2;
    }

    public void setO2(CustomObject o2) {
        this.o2 = o2;
    }

    @Override
    public String toString() {
        return "CustomSection{" +
                "map=" + map +
                ", s1='" + s1 + '\'' +
                ", i1=" + i1 +
                ", o1=" + o1 +
                ", o2=" + o2 +
                '}';
    }
}
