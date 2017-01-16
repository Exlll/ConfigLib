package de.exlll.configlib;

import java.nio.file.Path;
import java.util.*;

@Comment({
        "This is a test configuration.",
        "This comment is applied to a class."
})
final class TestConfiguration extends Configuration {
    static final String CONFIG_AS_STRING = "# This is a test configuration.\n" +
            "# This comment is applied to a class.\n" +
            "\n" +
            "# This comment is applied to a field.\n" +
            "# It has more than 1 line.\n" +
            "port: -1\n" +
            "localhost: localhost\n" +
            "modifier: 3.14\n" +
            "# This comment is applied to a field.\n" +
            "allowedIps:\n" +
            "- 127.0.0.1\n" +
            "- 127.0.0.2\n" +
            "- 127.0.0.3\n" +
            "intsByStrings:\n" +
            "  third: 3\n" +
            "  first: 1\n" +
            "  second: 2\n" +
            "stringListsByString:\n" +
            "  za:\n" +
            "  - z1\n" +
            "  - z2\n" +
            "  ya:\n" +
            "  - y1\n" +
            "  - y2\n" +
            "  xa:\n" +
            "  - x1\n" +
            "  - x2\n";

    @Comment({
            "This comment is applied to a field.",
            "It has more than 1 line."
    })
    private int port = -1;
    private String localhost = "localhost";
    private double modifier = 3.14;
    @Comment("This comment is applied to a field.")
    private List<String> allowedIps = new ArrayList<>();
    private Map<String, Integer> intsByStrings = new HashMap<>();
    private Map<String, List<String>> stringListsByString = new HashMap<>();


    public TestConfiguration(Path path) {
        super(path);
        allowedIps.add("127.0.0.1");
        allowedIps.add("127.0.0.2");
        allowedIps.add("127.0.0.3");

        intsByStrings.put("first", 1);
        intsByStrings.put("second", 2);
        intsByStrings.put("third", 3);

        stringListsByString.put("xa", Arrays.asList("x1", "x2"));
        stringListsByString.put("ya", Arrays.asList("y1", "y2"));
        stringListsByString.put("za", Arrays.asList("z1", "z2"));
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocalhost() {
        return localhost;
    }

    public void setLocalhost(String localhost) {
        this.localhost = localhost;
    }

    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    public List<String> getAllowedIps() {
        return allowedIps;
    }

    public void setAllowedIps(List<String> allowedIps) {
        this.allowedIps = allowedIps;
    }

    public Map<String, Integer> getIntsByStrings() {
        return intsByStrings;
    }

    public void setIntsByStrings(Map<String, Integer> intsByStrings) {
        this.intsByStrings = intsByStrings;
    }

    public Map<String, List<String>> getStringListsByString() {
        return stringListsByString;
    }

    public void setStringListsByString(
            Map<String, List<String>> stringListsByString) {
        this.stringListsByString = stringListsByString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestConfiguration that = (TestConfiguration) o;

        if (port != that.port) return false;
        if (Double.compare(that.modifier, modifier) != 0) return false;
        if (!localhost.equals(that.localhost)) return false;
        if (!allowedIps.equals(that.allowedIps)) return false;
        if (!intsByStrings.equals(that.intsByStrings)) return false;
        return stringListsByString.equals(that.stringListsByString);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = port;
        result = 31 * result + localhost.hashCode();
        temp = Double.doubleToLongBits(modifier);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + allowedIps.hashCode();
        result = 31 * result + intsByStrings.hashCode();
        result = 31 * result + stringListsByString.hashCode();
        return result;
    }
}