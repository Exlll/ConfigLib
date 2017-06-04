package de.exlll.configlib.classes;

import de.exlll.configlib.ConfigList;
import de.exlll.configlib.ConfigMap;
import de.exlll.configlib.ConfigSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigTypeClass {
    public ConfigList<String> configListSimple = new ConfigList<>(String.class);
    public ConfigSet<String> configSetSimple = new ConfigSet<>(String.class);
    public ConfigMap<String, String> configMapSimple = new ConfigMap<>(String.class, String.class);
    public ConfigList<A> configList = new ConfigList<>(A.class);
    public ConfigSet<A> configSet = new ConfigSet<>(A.class);
    public ConfigMap<String, A> configMap = new ConfigMap<>(String.class, A.class);

    public ConfigTypeClass() {
        configListSimple.add("a");
        configSetSimple.add("b");
        configMapSimple.put("c", "d");

        configList.add(from("e"));
        configSet.add(from("f"));
        configMap.put("g", from("h"));
    }

    public static Map<String, Object> newValues() {
        Map<String, Object> map = new HashMap<>();

        List<String> configListSimple = new ConfigList<>(String.class);
        configListSimple.add("b");
        Set<String> configSetSimple = new ConfigSet<>(String.class);
        configSetSimple.add("c");
        Map<String, String> configMapSimple = new ConfigMap<>(String.class, String.class);
        configMapSimple.put("d", "e");
        List<A> configList = new ConfigList<>(A.class);
        configList.add(from("f"));
        Set<A> configSet = new ConfigSet<>(A.class);
        configSet.add(from("g"));
        Map<String, A> configMap = new ConfigMap<>(String.class, A.class);
        configMap.put("h", from("i"));

        map.put("configListSimple", configListSimple);
        map.put("configSetSimple", configSetSimple);
        map.put("configMapSimple", configMapSimple);
        map.put("configList", configList);
        map.put("configSet", configSet);
        map.put("configMap", configMap);

        return map;
    }

    public static A from(String string) {
        A a = new A();
        a.string = string;
        return a;
    }

    public static final class A {
        private String string = "string";

        @Override
        public String toString() {
            return "A{" +
                    "string='" + string + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            A a = (A) o;

            return string.equals(a.string);
        }

        @Override
        public int hashCode() {
            return string.hashCode();
        }
    }
}
