package de.exlll.configlib.classes;

import de.exlll.configlib.Converter;

public final class TestSubClassConverter
        implements Converter<TestSubClass, String> {

    @Override
    public String convertTo(TestSubClass element, ConversionInfo info) {
        return element.getPrimInt() + ":" + element.getString();
    }

    @Override
    public TestSubClass convertFrom(String element, ConversionInfo info) {
        String[] split = element.split(":");
        return TestSubClass.of(Integer.parseInt(split[0]), split[1]);
    }

    @Override
    public String toString() {
        return "TestSubClassConverter";
    }
}
