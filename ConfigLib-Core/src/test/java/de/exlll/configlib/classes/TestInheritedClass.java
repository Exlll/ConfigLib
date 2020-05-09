package de.exlll.configlib.classes;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestInheritedClass extends TestClass {

    public static final TestInheritedClass TEST_VALUES;

    static {
        TestInheritedClass testValues = (TestInheritedClass) initTestClass(new TestInheritedClass());
        testValues.interitedProp = "from interhited class";
        TEST_VALUES = testValues;
    }

    private String interitedProp = "";

    public TestInheritedClass(Path path, YamlProperties properties) {
        super(path, properties);
    }

    public TestInheritedClass(Path path) {
        super(path);
    }


    public TestInheritedClass() {
        this(Paths.get(""), YamlProperties.DEFAULT);
    }
}
