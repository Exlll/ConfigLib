package de.exlll.configlib;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class TypeConverterTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void convertValueReturnsSameInstanceIfNoConversion() throws Exception {
        Map<?, ?> map = new HashMap<>();
        assertThat(TypeConverter.convertValue(Map.class, map), sameInstance(map));
    }

    @Test
    public void convertValueConvertsString() throws Exception {
        assertThat(TypeConverter.convertString("z"), is('z'));
    }

    @Test
    public void convertValueConvertsNumber() throws Exception {
        Integer i = 10;
        Object o = TypeConverter.convertValue(Short.class, i);
        assertThat(o, instanceOf(Short.class));
        assertThat(o, is((short) 10));
    }

    @Test
    public void convertValueReturnsInstanceIfTypesMatch() throws Exception {
        Object o = new Object();
        Object converted = TypeConverter.convertValue(Object.class, o);
        assertThat(o, sameInstance(converted));
    }

    @Test
    public void convertValueRequiresNonNullClass() throws Exception {
        exception.expect(NullPointerException.class);
        TypeConverter.convertValue(Object.class, null);
    }

    @Test
    public void convertValueRequiresNonNullValue() throws Exception {
        exception.expect(NullPointerException.class);
        TypeConverter.convertValue(null, new Object());
    }

    @Test
    public void convertStringThrowsExceptionIfStringLength0() throws Exception {
        exception.expect(IllegalArgumentException.class);
        TypeConverter.convertString("");
    }

    @Test
    public void convertStringThrowsExceptionIfStringLengthBiggerThan1() throws Exception {
        exception.expect(IllegalArgumentException.class);
        TypeConverter.convertString("ab");
    }

    @Test
    public void convertStringReturnsCharacter() throws Exception {
        String s = "z";
        char c = 'z';

        Character character = TypeConverter.convertString(s);
        assertThat(character, is(c));
    }

    @Test
    public void convertNumberThrowsExceptionIfUnknownType() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Number cannot be converted to target type " +
                "'class java.lang.Object'");
        TypeConverter.convertNumber(Object.class, 1);
    }

    @Test
    public void convertNumberReturnsConvertedNumber() throws Exception {
        Number[] numbers = {(byte) 1, (short) 2, 3, 4L, 5.0F, 6.0};
        Class<?>[] numClasses = {
                Byte.class, Short.class, Integer.class, Long.class,
                Float.class, Double.class
        };

        for (Class<?> numClass : numClasses) {
            for (Number number : numbers) {
                Number n = TypeConverter.convertNumber(numClass, number);
                assertThat(n, instanceOf(numClass));

                // this is only true because we use values that
                // don't cause an overflow
                assertThat(n.doubleValue(), is(number.doubleValue()));
            }
        }

        int i = Short.MAX_VALUE + 1;
        Number n = TypeConverter.convertNumber(Short.class, i);
        assertThat(n, is(Short.MIN_VALUE));
    }

    @Test
    public void isBooleanClass() throws Exception {
        assertThat(TypeConverter.isBooleanClass(Boolean.class), is(true));
        assertThat(TypeConverter.isBooleanClass(boolean.class), is(true));
        assertThat(TypeConverter.isBooleanClass(Object.class), is(false));
    }

    @Test
    public void isByteClass() throws Exception {
        assertThat(TypeConverter.isByteClass(Byte.class), is(true));
        assertThat(TypeConverter.isByteClass(byte.class), is(true));
        assertThat(TypeConverter.isByteClass(Object.class), is(false));
    }

    @Test
    public void isShortClass() throws Exception {
        assertThat(TypeConverter.isShortClass(Short.class), is(true));
        assertThat(TypeConverter.isShortClass(short.class), is(true));
        assertThat(TypeConverter.isShortClass(Object.class), is(false));
    }

    @Test
    public void isIntegerClass() throws Exception {
        assertThat(TypeConverter.isIntegerClass(Integer.class), is(true));
        assertThat(TypeConverter.isIntegerClass(int.class), is(true));
        assertThat(TypeConverter.isIntegerClass(Object.class), is(false));
    }

    @Test
    public void isLongClass() throws Exception {
        assertThat(TypeConverter.isLongClass(Long.class), is(true));
        assertThat(TypeConverter.isLongClass(long.class), is(true));
        assertThat(TypeConverter.isLongClass(Object.class), is(false));
    }

    @Test
    public void isFloatClass() throws Exception {
        assertThat(TypeConverter.isFloatClass(Float.class), is(true));
        assertThat(TypeConverter.isFloatClass(float.class), is(true));
        assertThat(TypeConverter.isFloatClass(Object.class), is(false));
    }

    @Test
    public void isDoubleClass() throws Exception {
        assertThat(TypeConverter.isDoubleClass(Double.class), is(true));
        assertThat(TypeConverter.isDoubleClass(double.class), is(true));
        assertThat(TypeConverter.isDoubleClass(Object.class), is(false));
    }

    @Test
    public void isCharacterClass() throws Exception {
        assertThat(TypeConverter.isCharacterClass(Character.class), is(true));
        assertThat(TypeConverter.isCharacterClass(char.class), is(true));
        assertThat(TypeConverter.isCharacterClass(Object.class), is(false));
    }


}