package de.exlll.configlib.configurations;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static de.exlll.configlib.TestUtils.*;
import static de.exlll.configlib.configurations.ExampleEnum.*;

public final class ExampleInitializer {
    private static final BigInteger BI_1 = new BigInteger("1");
    private static final BigInteger BI_2 = new BigInteger("2");
    private static final BigInteger BI_3 = new BigInteger("3");
    private static final BigInteger BI_4 = new BigInteger("4");
    private static final BigInteger BI_5 = new BigInteger("5");

    private static final BigDecimal BD_1 = new BigDecimal("1");
    private static final BigDecimal BD_2 = new BigDecimal("2");
    private static final BigDecimal BD_3 = new BigDecimal("3");
    private static final BigDecimal BD_4 = new BigDecimal("4");
    private static final BigDecimal BD_5 = new BigDecimal("5");

    private static final LocalDate LD_1 = LocalDate.of(2000, Month.JANUARY, 1);
    private static final LocalDate LD_2 = LocalDate.of(2000, Month.JANUARY, 2);
    private static final LocalDate LD_3 = LocalDate.of(2000, Month.JANUARY, 3);
    private static final LocalDate LD_4 = LocalDate.of(2000, Month.JANUARY, 4);
    private static final LocalDate LD_5 = LocalDate.of(2000, Month.JANUARY, 5);

    private static final LocalTime LT_1 = LocalTime.of(0, 1);
    private static final LocalTime LT_2 = LocalTime.of(0, 2);
    private static final LocalTime LT_3 = LocalTime.of(0, 3);
    private static final LocalTime LT_4 = LocalTime.of(0, 4);
    private static final LocalTime LT_5 = LocalTime.of(0, 5);

    private static final LocalDateTime LDT_1 = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0);
    private static final LocalDateTime LDT_2 = LocalDateTime.of(2000, Month.JANUARY, 2, 0, 0);
    private static final LocalDateTime LDT_3 = LocalDateTime.of(2000, Month.JANUARY, 3, 0, 0);
    private static final LocalDateTime LDT_4 = LocalDateTime.of(2000, Month.JANUARY, 4, 0, 0);
    private static final LocalDateTime LDT_5 = LocalDateTime.of(2000, Month.JANUARY, 5, 0, 0);

    private static final UUID UUID_1 = UUID.fromString("d50f3bdd-ac66-4b74-a01f-4617b24d68c0");
    private static final UUID UUID_2 = UUID.fromString("d50f3bdd-ac66-4b74-a01f-4617b24d68c1");
    private static final UUID UUID_3 = UUID.fromString("d50f3bdd-ac66-4b74-a01f-4617b24d68c2");
    private static final UUID UUID_4 = UUID.fromString("d50f3bdd-ac66-4b74-a01f-4617b24d68c3");
    private static final UUID UUID_5 = UUID.fromString("d50f3bdd-ac66-4b74-a01f-4617b24d68c4");

    private static final ExampleConfigurationB1 B1_1 = newExampleConfigurationB1_1();
    private static final ExampleConfigurationB1 B1_2 = newExampleConfigurationB1_2();
    private static final ExampleConfigurationB2 B2_1 = newExampleConfigurationB2_1();
    private static final ExampleConfigurationB2 B2_2 = newExampleConfigurationB2_2();

    private static final Point P1 = new Point(0, 1);
    private static final Point P2 = new Point(0, 2);
    private static final Point P3 = new Point(0, 3);
    private static final Point P4 = new Point(0, 4);

    public static ExampleConfigurationA2 newExampleConfigurationA2() {
        ExampleConfigurationA2 a2 = new ExampleConfigurationA2();

        a2.setA1_primBool(true);
        a2.setA2_primBool(true);

        a2.setA1_primChar('a');
        a2.setA2_primChar('b');

        a2.setA1_primByte((byte) 1);
        a2.setA2_primByte((byte) 2);

        a2.setA1_primShort((short) 3);
        a2.setA2_primShort((short) 4);

        a2.setA1_primInt(5);
        a2.setA2_primInt(6);

        a2.setA1_primLong(7);
        a2.setA2_primLong(8);

        a2.setA1_primFloat(9f);
        a2.setA2_primFloat(10f);

        a2.setA1_primDouble(11d);
        a2.setA2_primDouble(12d);

        a2.setA1_refBool(true);
        a2.setA2_refBool(true);

        a2.setA1_refChar('c');
        a2.setA2_refChar('d');

        a2.setA1_refByte((byte) 13);
        a2.setA2_refByte((byte) 14);

        a2.setA1_refShort((short) 15);
        a2.setA2_refShort((short) 16);

        a2.setA1_refInt(17);
        a2.setA2_refInt(18);

        a2.setA1_refLong(19L);
        a2.setA2_refLong(20L);

        a2.setA1_refFloat(21f);
        a2.setA2_refFloat(22f);

        a2.setA1_refDouble(23d);
        a2.setA2_refDouble(24d);

        a2.setA1_string("a");
        a2.setA2_string("b");

        a2.setA1_bigInteger(BI_1);
        a2.setA2_bigInteger(BI_2);

        a2.setA1_bigDecimal(BD_1);
        a2.setA2_bigDecimal(BD_2);

        a2.setA1_localDate(LD_1);
        a2.setA2_localDate(LD_2);

        a2.setA1_localTime(LT_1);
        a2.setA2_localTime(LT_2);

        a2.setA1_localDateTime(LDT_1);
        a2.setA2_localDateTime(LDT_2);

        a2.setA1_uuid(UUID_1);
        a2.setA2_uuid(UUID_2);

        a2.setA1_Enm(A);
        a2.setA2_Enm(B);

        a2.setA1_b1(B1_1);
        a2.setA2_b1(B1_2);

        a2.setA1_b2(B2_1);
        a2.setA2_b2(B2_2);

        a2.setA1_listBoolean(List.of(true, false, true));
        a2.setA2_listBoolean(List.of(false, true, false));

        a2.setA1_listChar(List.of('a', 'b', 'c'));
        a2.setA2_listChar(List.of('d', 'e', 'f'));

        a2.setA1_listByte(List.of((byte) 1, (byte) 2, (byte) 3));
        a2.setA2_listByte(List.of((byte) 2, (byte) 3, (byte) 4));

        a2.setA1_listShort(List.of((short) 1, (short) 2, (short) 3));
        a2.setA2_listShort(List.of((short) 2, (short) 3, (short) 4));

        a2.setA1_listInteger(List.of(1, 2, 3));
        a2.setA2_listInteger(List.of(2, 3, 4));

        a2.setA1_listLong(List.of(1L, 2L, 3L));
        a2.setA2_listLong(List.of(2L, 3L, 4L));

        a2.setA1_listFloat(List.of(1f, 2f, 3f));
        a2.setA2_listFloat(List.of(2f, 3f, 4f));

        a2.setA1_listDouble(List.of(1d, 2d, 3d));
        a2.setA2_listDouble(List.of(2d, 3d, 4d));

        a2.setA1_listString(List.of("1", "2", "3"));
        a2.setA2_listString(List.of("2", "3", "4"));

        a2.setA1_listBigInteger(List.of(BI_1, BI_2, BI_3));
        a2.setA2_listBigInteger(List.of(BI_2, BI_3, BI_4));

        a2.setA1_listBigDecimal(List.of(BD_1, BD_2, BD_3));
        a2.setA2_listBigDecimal(List.of(BD_2, BD_3, BD_4));

        a2.setA1_listLocalDate(List.of(LD_1, LD_2, LD_3));
        a2.setA2_listLocalDate(List.of(LD_2, LD_3, LD_4));

        a2.setA1_listLocalTime(List.of(LT_1, LT_2, LT_3));
        a2.setA2_listLocalTime(List.of(LT_2, LT_3, LT_4));

        a2.setA1_listLocalDateTime(List.of(LDT_1, LDT_2, LDT_3));
        a2.setA2_listLocalDateTime(List.of(LDT_2, LDT_3, LDT_4));

        a2.setA1_listUuid(List.of(UUID_1, UUID_2, UUID_3));
        a2.setA2_listUuid(List.of(UUID_2, UUID_3, UUID_4));

        a2.setA1_listEnm(List.of(A, B, C));
        a2.setA2_listEnm(List.of(B, C, D));

        a2.setA1_listB1(List.of(B1_1));
        a2.setA2_listB1(List.of(B1_1, B1_2));

        a2.setA1_listB2(List.of(B2_1));
        a2.setA2_listB2(List.of(B2_1, B2_2));

        a2.setA1_arrayPrimBoolean(new boolean[]{true, false, true});
        a2.setA2_arrayPrimBoolean(new boolean[]{false, true, false});

        a2.setA1_arrayPrimChar(new char[]{'a', 'b', 'c'});
        a2.setA2_arrayPrimChar(new char[]{'d', 'e', 'f'});

        a2.setA1_arrayPrimByte(new byte[]{(byte) 1, (byte) 2, (byte) 3});
        a2.setA2_arrayPrimByte(new byte[]{(byte) 2, (byte) 3, (byte) 4});

        a2.setA1_arrayPrimShort(new short[]{(short) 1, (short) 2, (short) 3});
        a2.setA2_arrayPrimShort(new short[]{(short) 2, (short) 3, (short) 4});

        a2.setA1_arrayPrimInteger(new int[]{1, 2, 3});
        a2.setA2_arrayPrimInteger(new int[]{2, 3, 4});

        a2.setA1_arrayPrimLong(new long[]{1L, 2L, 3L});
        a2.setA2_arrayPrimLong(new long[]{2L, 3L, 4L});

        a2.setA1_arrayPrimFloat(new float[]{1f, 2f, 3f});
        a2.setA2_arrayPrimFloat(new float[]{2f, 3f, 4f});

        a2.setA1_arrayPrimDouble(new double[]{1d, 2d, 3d});
        a2.setA2_arrayPrimDouble(new double[]{2d, 3d, 4d});

        a2.setA1_arrayBoolean(new Boolean[]{true, false, true});
        a2.setA2_arrayBoolean(new Boolean[]{false, true, false});

        a2.setA1_arrayChar(new Character[]{'a', 'b', 'c'});
        a2.setA2_arrayChar(new Character[]{'d', 'e', 'f'});

        a2.setA1_arrayByte(new Byte[]{(byte) 1, (byte) 2, (byte) 3});
        a2.setA2_arrayByte(new Byte[]{(byte) 2, (byte) 3, (byte) 4});

        a2.setA1_arrayShort(new Short[]{(short) 1, (short) 2, (short) 3});
        a2.setA2_arrayShort(new Short[]{(short) 2, (short) 3, (short) 4});

        a2.setA1_arrayInteger(new Integer[]{1, 2, 3});
        a2.setA2_arrayInteger(new Integer[]{2, 3, 4});

        a2.setA1_arrayLong(new Long[]{1L, 2L, 3L});
        a2.setA2_arrayLong(new Long[]{2L, 3L, 4L});

        a2.setA1_arrayFloat(new Float[]{1f, 2f, 3f});
        a2.setA2_arrayFloat(new Float[]{2f, 3f, 4f});

        a2.setA1_arrayDouble(new Double[]{1d, 2d, 3d});
        a2.setA2_arrayDouble(new Double[]{2d, 3d, 4d});

        a2.setA1_arrayString(new String[]{"1", "2", "3"});
        a2.setA2_arrayString(new String[]{"2", "3", "4"});

        a2.setA1_arrayBigInteger(new BigInteger[]{BI_1, BI_2, BI_3});
        a2.setA2_arrayBigInteger(new BigInteger[]{BI_2, BI_3, BI_4});

        a2.setA1_arrayBigDecimal(new BigDecimal[]{BD_1, BD_2, BD_3});
        a2.setA2_arrayBigDecimal(new BigDecimal[]{BD_2, BD_3, BD_4});

        a2.setA1_arrayLocalDate(new LocalDate[]{LD_1, LD_2, LD_3});
        a2.setA2_arrayLocalDate(new LocalDate[]{LD_2, LD_3, LD_4});

        a2.setA1_arrayLocalTime(new LocalTime[]{LT_1, LT_2, LT_3});
        a2.setA2_arrayLocalTime(new LocalTime[]{LT_2, LT_3, LT_4});

        a2.setA1_arrayLocalDateTime(new LocalDateTime[]{LDT_1, LDT_2, LDT_3});
        a2.setA2_arrayLocalDateTime(new LocalDateTime[]{LDT_2, LDT_3, LDT_4});

        a2.setA1_arrayUuid(new UUID[]{UUID_1, UUID_2, UUID_3});
        a2.setA2_arrayUuid(new UUID[]{UUID_2, UUID_3, UUID_4});

        a2.setA1_arrayEnm(new ExampleEnum[]{A, B, C});
        a2.setA2_arrayEnm(new ExampleEnum[]{B, C, D});

        a2.setA1_arrayB1(new ExampleConfigurationB1[]{B1_1});
        a2.setA2_arrayB1(new ExampleConfigurationB1[]{B1_1, B1_2});

        a2.setA1_arrayB2(new ExampleConfigurationB2[]{B2_1});
        a2.setA2_arrayB2(new ExampleConfigurationB2[]{B2_1, B2_2});

        a2.setA1_setBoolean(asSet(true));
        a2.setA2_setBoolean(asSet(false));

        a2.setA1_setChar(asSet('a', 'b', 'c'));
        a2.setA2_setChar(asSet('d', 'e', 'f'));

        a2.setA1_setByte(asSet((byte) 1, (byte) 2, (byte) 3));
        a2.setA2_setByte(asSet((byte) 2, (byte) 3, (byte) 4));

        a2.setA1_setShort(asSet((short) 1, (short) 2, (short) 3));
        a2.setA2_setShort(asSet((short) 2, (short) 3, (short) 4));

        a2.setA1_setInteger(asSet(1, 2, 3));
        a2.setA2_setInteger(asSet(2, 3, 4));

        a2.setA1_setLong(asSet(1L, 2L, 3L));
        a2.setA2_setLong(asSet(2L, 3L, 4L));

        a2.setA1_setFloat(asSet(1f, 2f, 3f));
        a2.setA2_setFloat(asSet(2f, 3f, 4f));

        a2.setA1_setDouble(asSet(1d, 2d, 3d));
        a2.setA2_setDouble(asSet(2d, 3d, 4d));

        a2.setA1_setString(asSet("1", "2", "3"));
        a2.setA2_setString(asSet("2", "3", "4"));

        a2.setA1_setBigInteger(asSet(BI_1, BI_2, BI_3));
        a2.setA2_setBigInteger(asSet(BI_2, BI_3, BI_4));

        a2.setA1_setBigDecimal(asSet(BD_1, BD_2, BD_3));
        a2.setA2_setBigDecimal(asSet(BD_2, BD_3, BD_4));

        a2.setA1_setLocalDate(asSet(LD_1, LD_2, LD_3));
        a2.setA2_setLocalDate(asSet(LD_2, LD_3, LD_4));

        a2.setA1_setLocalTime(asSet(LT_1, LT_2, LT_3));
        a2.setA2_setLocalTime(asSet(LT_2, LT_3, LT_4));

        a2.setA1_setLocalDateTime(asSet(LDT_1, LDT_2, LDT_3));
        a2.setA2_setLocalDateTime(asSet(LDT_2, LDT_3, LDT_4));

        a2.setA1_setUuid(asSet(UUID_1, UUID_2, UUID_3));
        a2.setA2_setUuid(asSet(UUID_2, UUID_3, UUID_4));

        a2.setA1_setEnm(asSet(A, B, C));
        a2.setA2_setEnm(asSet(B, C, D));

        a2.setA1_setB1(asSet(B1_1));
        a2.setA2_setB1(asSet(B1_1, B1_2));

        a2.setA1_setB2(asSet(B2_1));
        a2.setA2_setB2(asSet(B2_1, B2_2));

        a2.setA1_mapBooleanBoolean(asMap(true, true, false, false));
        a2.setA2_mapBooleanBoolean(asMap(true, true, false, false));

        a2.setA1_mapCharChar(asMap('a', 'b', 'c', 'd'));
        a2.setA2_mapCharChar(asMap('b', 'c', 'd', 'e'));

        a2.setA1_mapByteByte(asMap((byte) 1, (byte) 2, (byte) 3, (byte) 4));
        a2.setA2_mapByteByte(asMap((byte) 2, (byte) 3, (byte) 4, (byte) 5));

        a2.setA1_mapShortShort(asMap((short) 1, (short) 2, (short) 3, (short) 4));
        a2.setA2_mapShortShort(asMap((short) 2, (short) 3, (short) 4, (short) 5));

        a2.setA1_mapIntegerInteger(asMap(1, 2, 3, 4));
        a2.setA2_mapIntegerInteger(asMap(2, 3, 4, 5));

        a2.setA1_mapLongLong(asMap(1L, 2L, 3L, 4L));
        a2.setA2_mapLongLong(asMap(2L, 3L, 4L, 5L));

        a2.setA1_mapFloatFloat(asMap(1f, 2f, 3f, 4f));
        a2.setA2_mapFloatFloat(asMap(2f, 3f, 4f, 5f));

        a2.setA1_mapDoubleDouble(asMap(1d, 2d, 3d, 4d));
        a2.setA2_mapDoubleDouble(asMap(2d, 3d, 4d, 5d));

        a2.setA1_mapStringString(asMap("1", "2", "3", "4"));
        a2.setA2_mapStringString(asMap("2", "3", "4", "5"));

        a2.setA1_mapBigIntegerBigInteger(asMap(BI_1, BI_2, BI_3, BI_4));
        a2.setA2_mapBigIntegerBigInteger(asMap(BI_2, BI_3, BI_4, BI_5));

        a2.setA1_mapBigDecimalBigDecimal(asMap(BD_1, BD_2, BD_3, BD_4));
        a2.setA2_mapBigDecimalBigDecimal(asMap(BD_2, BD_3, BD_4, BD_5));

        a2.setA1_mapLocalDateLocalDate(asMap(LD_1, LD_2, LD_3, LD_4));
        a2.setA2_mapLocalDateLocalDate(asMap(LD_2, LD_3, LD_4, LD_5));

        a2.setA1_mapLocalTimeLocalTime(asMap(LT_1, LT_2, LT_3, LT_4));
        a2.setA2_mapLocalTimeLocalTime(asMap(LT_2, LT_3, LT_4, LT_5));

        a2.setA1_mapLocalDateTimeLocalDateTime(asMap(LDT_1, LDT_2, LDT_3, LDT_4));
        a2.setA2_mapLocalDateTimeLocalDateTime(asMap(LDT_2, LDT_3, LDT_4, LDT_5));

        a2.setA1_mapUuidUuid(asMap(UUID_1, UUID_2, UUID_3, UUID_4));
        a2.setA2_mapUuidUuid(asMap(UUID_2, UUID_3, UUID_4, UUID_5));

        a2.setA1_mapEnmEnm(asMap(A, B, C, D));
        a2.setA2_mapEnmEnm(asMap(B, C, D, E));

        a2.setA1_mapIntegerB1(asMap(1, B1_1, 2, B1_2));
        a2.setA2_mapIntegerB1(asMap(2, B1_1, 3, B1_2));

        a2.setA1_mapEnmB2(asMap(A, B2_1, B, B2_2));
        a2.setA2_mapEnmB2(asMap(B, B2_1, C, B2_2));

        a2.setA1_listEmpty(Collections.emptyList());
        a2.setA2_listEmpty(List.of());

        a2.setA1_arrayEmpty(new Integer[0]);
        a2.setA2_arrayEmpty(new Integer[0]);

        a2.setA1_setEmpty(Collections.emptySet());
        a2.setA2_setEmpty(asSet());

        a2.setA1_mapEmpty(Collections.emptyMap());
        a2.setA2_mapEmpty(asMap());

        a2.setA1_listListByte(List.of(
                List.of(),
                List.of((byte) 1),
                List.of((byte) 1, (byte) 2)
        ));
        a2.setA2_listListByte(List.of(
                List.of((byte) 1),
                List.of((byte) 1, (byte) 2),
                List.of((byte) 1, (byte) 2, (byte) 3)
        ));

        a2.setA1_listArrayFloat(List.of(
                new Float[0],
                new Float[]{1f},
                new Float[]{1f, 2f}
        ));
        a2.setA2_listArrayFloat(List.of(
                new Float[]{1f},
                new Float[]{1f, 2f},
                new Float[]{1f, 2f, 3f}
        ));

        a2.setA1_listSetString(List.of(
                asSet(),
                asSet("1"),
                asSet("1", "2")
        ));
        a2.setA2_listSetString(List.of(
                asSet("1"),
                asSet("1", "2"),
                asSet("1", "2", "3")
        ));

        a2.setA1_listMapEnmLocalDate(List.of(
                asMap(),
                asMap(A, LD_1),
                asMap(A, LD_1, B, LD_2)
        ));
        a2.setA2_listMapEnmLocalDate(List.of(
                asMap(A, LD_1),
                asMap(A, LD_1, B, LD_2),
                asMap(A, LD_1, B, LD_2, C, LD_3)
        ));

        a2.setA1_setSetShort(asSet(
                asSet(),
                asSet((short) 1),
                asSet((short) 1, (short) 2)
        ));
        a2.setA2_setSetShort(asSet(
                asSet((short) 1),
                asSet((short) 1, (short) 2),
                asSet((short) 1, (short) 2, (short) 3)
        ));

        a2.setA1_setArrayDouble(asSet(
                new Double[0],
                new Double[]{1d},
                new Double[]{1d, 2d}
        ));
        a2.setA2_setArrayDouble(asSet(
                new Double[]{1d},
                new Double[]{1d, 2d},
                new Double[]{1d, 2d, 3d}
        ));

        a2.setA1_setListString(asSet(
                List.of(),
                List.of("1"),
                List.of("1", "2")
        ));
        a2.setA2_setListString(asSet(
                List.of("1"),
                List.of("1", "2"),
                List.of("1", "2", "3")
        ));

        a2.setA1_setMapEnmLocalTime(asSet(
                asMap(),
                asMap(A, LT_1),
                asMap(A, LT_1, B, LT_2)
        ));
        a2.setA2_setMapEnmLocalTime(asSet(
                asMap(A, LT_1),
                asMap(A, LT_1, B, LT_2),
                asMap(A, LT_1, B, LT_2, C, LT_3)
        ));

        a2.setA1_mapIntegerMapLongBoolean(asMap(
                1, asMap(),
                2, asMap(1L, true),
                3, asMap(1L, true, 2L, false)
        ));
        a2.setA2_mapIntegerMapLongBoolean(asMap(
                2, asMap(1L, true),
                3, asMap(1L, true, 2L, false),
                4, asMap(1L, true, 2L, false, 3L, true)
        ));

        a2.setA1_mapStringListB1(asMap(
                "1", List.of(),
                "2", List.of(B1_1),
                "3", List.of(B1_1, B1_2)

        ));
        a2.setA2_mapStringListB1(asMap(
                "2", List.of(B1_1),
                "3", List.of(B1_1, B1_2),
                "4", List.of(B1_1, B1_2, B1_1)

        ));

        a2.setA1_mapBigIntegerArrayBigDecimal(asMap(
                BI_1, new BigDecimal[0],
                BI_2, new BigDecimal[]{BD_1},
                BI_3, new BigDecimal[]{BD_1, BD_2}
        ));
        a2.setA2_mapBigIntegerArrayBigDecimal(asMap(
                BI_2, new BigDecimal[]{BD_1},
                BI_3, new BigDecimal[]{BD_1, BD_2},
                BI_4, new BigDecimal[]{BD_1, BD_2, BD_3}
        ));

        a2.setA1_mapEnmSetB2(asMap(
                A, asSet(),
                B, asSet(B2_1),
                C, asSet(B2_1, B2_2)
        ));
        a2.setA2_mapEnmSetB2(asMap(
                B, asSet(B2_1),
                C, asSet(B2_1, B2_2),
                D, asSet()
        ));

        a2.setA1_mapIntegerListMapShortSetB2(asMap(
                1, List.of(),
                2, List.of(asMap()),
                3, List.of(asMap((short) 1, asSet(), (short) 2, asSet(B2_1))),
                4, List.of(asMap((short) 1, asSet(), (short) 2, asSet(B2_1)), asMap())
        ));
        a2.setA2_mapIntegerListMapShortSetB2(asMap(
                2, List.of(asMap()),
                3, List.of(asMap((short) 1, asSet(), (short) 2, asSet(B2_1))),
                4, List.of(asMap((short) 1, asSet(), (short) 2, asSet(B2_1)), asMap()),
                5, List.of(
                        asMap((short) 1, asSet(), (short) 2, asSet(B2_1)),
                        asMap((short) 1, asSet())
                )
        ));

        a2.setA1_arrayArrayPrimBoolean(new boolean[][]{
                {},
                {true},
                {true, false},
        });
        a2.setA2_arrayArrayPrimBoolean(new boolean[][]{
                {true},
                {true, false},
                {true, false, true},
        });

        a2.setA1_arrayArrayPrimChar(new char[][]{
                {},
                {'a'},
                {'a', 'b'}
        });
        a2.setA2_arrayArrayPrimChar(new char[][]{
                {'a'},
                {'a', 'b'},
                {'a', 'b', 'c'},
        });

        a2.setA1_arrayArrayPrimByte(new byte[][]{
                {},
                {(byte) 1},
                {(byte) 1, (byte) 2}
        });
        a2.setA2_arrayArrayPrimByte(new byte[][]{
                {1},
                {(byte) 1, (byte) 2},
                {(byte) 1, (byte) 2, (byte) 3},
        });

        a2.setA1_arrayArrayPrimShort(new short[][]{
                {},
                {(short) 1},
                {(short) 1, (short) 2}
        });
        a2.setA2_arrayArrayPrimShort(new short[][]{
                {(short) 1},
                {(short) 1, (short) 2},
                {(short) 1, (short) 2, (short) 3},
        });

        a2.setA1_arrayArrayPrimInteger(new int[][]{
                {},
                {1},
                {1, 2}
        });
        a2.setA2_arrayArrayPrimInteger(new int[][]{
                {1},
                {1, 2},
                {1, 2, 3},
        });

        a2.setA1_arrayArrayPrimLong(new long[][]{
                {},
                {1L},
                {1L, 2L}
        });
        a2.setA2_arrayArrayPrimLong(new long[][]{
                {1L},
                {1L, 2L},
                {1L, 2L, 3L},
        });

        a2.setA1_arrayArrayPrimFloat(new float[][]{
                {},
                {1f},
                {1f, 2f}
        });
        a2.setA2_arrayArrayPrimFloat(new float[][]{
                {1f},
                {1f, 2f},
                {1f, 2f, 3f},
        });

        a2.setA1_arrayArrayPrimDouble(new double[][]{
                {},
                {1d},
                {1d, 2d}
        });
        a2.setA2_arrayArrayPrimDouble(new double[][]{
                {1d},
                {1d, 2d},
                {1d, 2d, 3d},
        });

        a2.setA1_arrayArrayBoolean(new Boolean[][]{
                {},
                {true},
                {true, false},
        });
        a2.setA2_arrayArrayBoolean(new Boolean[][]{
                {true},
                {true, false},
                {true, false, true},
        });

        a2.setA1_arrayArrayChar(new Character[][]{
                {},
                {'a'},
                {'a', 'b'}
        });
        a2.setA2_arrayArrayChar(new Character[][]{
                {'a'},
                {'a', 'b'},
                {'a', 'b', 'c'},
        });

        a2.setA1_arrayArrayByte(new Byte[][]{
                {},
                {(byte) 1},
                {(byte) 1, (byte) 2}
        });
        a2.setA2_arrayArrayByte(new Byte[][]{
                {1},
                {(byte) 1, (byte) 2},
                {(byte) 1, (byte) 2, (byte) 3},
        });

        a2.setA1_arrayArrayShort(new Short[][]{
                {},
                {(short) 1},
                {(short) 1, (short) 2}
        });
        a2.setA2_arrayArrayShort(new Short[][]{
                {(short) 1},
                {(short) 1, (short) 2},
                {(short) 1, (short) 2, (short) 3},
        });

        a2.setA1_arrayArrayInteger(new Integer[][]{
                {},
                {1},
                {1, 2}
        });
        a2.setA2_arrayArrayInteger(new Integer[][]{
                {1},
                {1, 2},
                {1, 2, 3},
        });

        a2.setA1_arrayArrayLong(new Long[][]{
                {},
                {1L},
                {1L, 2L}
        });
        a2.setA2_arrayArrayLong(new Long[][]{
                {1L},
                {1L, 2L},
                {1L, 2L, 3L},
        });

        a2.setA1_arrayArrayFloat(new Float[][]{
                {},
                {1f},
                {1f, 2f}
        });
        a2.setA2_arrayArrayFloat(new Float[][]{
                {1f},
                {1f, 2f},
                {1f, 2f, 3f},
        });

        a2.setA1_arrayArrayDouble(new Double[][]{
                {},
                {1d},
                {1d, 2d}
        });
        a2.setA2_arrayArrayDouble(new Double[][]{
                {1d},
                {1d, 2d},
                {1d, 2d, 3d},
        });

        a2.setA1_arrayArrayString(new String[][]{
                {},
                {"a"},
                {"a", "b"}
        });
        a2.setA2_arrayArrayString(new String[][]{
                {"a"},
                {"a", "b"},
                {"a", "b", "c"},
        });

        a2.setA1_arrayArrayBigInteger(new BigInteger[][]{
                {},
                {BI_1},
                {BI_1, BI_2},
        });
        a2.setA2_arrayArrayBigInteger(new BigInteger[][]{
                {BI_1},
                {BI_1, BI_2},
                {BI_1, BI_2, BI_3},
        });

        a2.setA1_arrayArrayBigDecimal(new BigDecimal[][]{
                {},
                {BD_1},
                {BD_1, BD_2},
        });
        a2.setA2_arrayArrayBigDecimal(new BigDecimal[][]{
                {BD_1},
                {BD_1, BD_2},
                {BD_1, BD_2, BD_3},
        });

        a2.setA1_arrayArrayLocalDate(new LocalDate[][]{
                {},
                {LD_1},
                {LD_1, LD_2},
        });
        a2.setA2_arrayArrayLocalDate(new LocalDate[][]{
                {LD_1},
                {LD_1, LD_2},
                {LD_1, LD_2, LD_3},
        });

        a2.setA1_arrayArrayLocalTime(new LocalTime[][]{
                {},
                {LT_1},
                {LT_1, LT_2},
        });
        a2.setA2_arrayArrayLocalTime(new LocalTime[][]{
                {LT_1},
                {LT_1, LT_2},
                {LT_1, LT_2, LT_3},
        });

        a2.setA1_arrayArrayLocalDateTime(new LocalDateTime[][]{
                {},
                {LDT_1},
                {LDT_1, LDT_2},
        });
        a2.setA2_arrayArrayLocalDateTime(new LocalDateTime[][]{
                {LDT_1},
                {LDT_1, LDT_2},
                {LDT_1, LDT_2, LDT_3},
        });

        a2.setA1_arrayArrayUuid(new UUID[][]{
                {},
                {UUID_1},
                {UUID_1, UUID_2},
        });
        a2.setA2_arrayArrayUuid(new UUID[][]{
                {UUID_1},
                {UUID_1, UUID_2},
                {UUID_1, UUID_2, UUID_3},
        });

        a2.setA1_arrayArrayEnm(new ExampleEnum[][]{
                {},
                {A},
                {A, B},
        });
        a2.setA2_arrayArrayEnm(new ExampleEnum[][]{
                {A},
                {A, B},
                {A, B, C},
        });

        a2.setA1_arrayArrayB1(new ExampleConfigurationB1[][]{{}, {B1_1}});
        a2.setA2_arrayArrayB1(new ExampleConfigurationB1[][]{{B1_1}, {B1_2}});

        a2.setA1_arrayArrayB2(new ExampleConfigurationB2[][]{{}, {B2_1}});
        a2.setA2_arrayArrayB2(new ExampleConfigurationB2[][]{{B2_1}, {B2_2}});

        a2.setA1_point(P1);
        a2.setA2_point(P2);

        a2.setA1_listPoint(List.of(P1, P2, P3));
        a2.setA2_listPoint(List.of(P2, P3, P4));

        a2.setA1_arrayPoint(new Point[]{P1, P2, P3});
        a2.setA2_arrayPoint(new Point[]{P2, P3, P4});

        a2.setA1_setPoint(asSet(P1, P2, P3));
        a2.setA2_setPoint(asSet(P2, P3, P4));

        a2.setA1_mapEnmListPoint(asMap(
                A, List.of(),
                B, List.of(P1),
                C, List.of(P1, P2)
        ));
        a2.setA2_mapEnmListPoint(asMap(
                B, List.of(P1),
                C, List.of(P1, P2),
                D, List.of(P1, P2, P3)
        ));

        return a2;
    }

    public static ExampleConfigurationB1 newExampleConfigurationB1_1() {
        ExampleConfigurationB1 b1 = new ExampleConfigurationB1();
        initializeExampleConfigurationB1_1(b1);
        return b1;
    }

    private static void initializeExampleConfigurationB1_1(ExampleConfigurationB1 b1) {
        b1.setB1_primBool(true);
        b1.setB1_refChar('b');
        b1.setB1_string("c");
        b1.setB1_listByte(List.of((byte) 1, (byte) 2, (byte) 3));
        b1.setB1_arrayShort(new Short[]{1, 2, 3});
        b1.setB1_setInteger(asSet(1, 2, 3));
        b1.setB1_listEmpty(Collections.emptyList());
        b1.setB1_mapLongLong(asMap(1L, 2L, 3L, 4L));
        b1.setB1_listListByte(List.of(
                List.of((byte) 1, (byte) 2, (byte) 3),
                List.of((byte) 2, (byte) 3, (byte) 4)
        ));
        b1.setB1_point(new Point(1, 2));
    }

    public static ExampleConfigurationB1 newExampleConfigurationB1_2() {
        ExampleConfigurationB1 b1 = new ExampleConfigurationB1();
        initializeExampleConfigurationB1_2(b1);
        return b1;
    }

    private static void initializeExampleConfigurationB1_2(ExampleConfigurationB1 b1) {
        b1.setB1_primBool(false);
        b1.setB1_refChar('c');
        b1.setB1_string("d");
        b1.setB1_listByte(List.of((byte) 2, (byte) 3, (byte) 4));
        b1.setB1_arrayShort(new Short[]{2, 3, 4});
        b1.setB1_setInteger(asSet(2, 3, 4));
        b1.setB1_listEmpty(Collections.emptyList());
        b1.setB1_mapLongLong(asMap(2L, 3L, 4L, 5L));
        b1.setB1_listListByte(List.of(
                List.of((byte) 2, (byte) 3, (byte) 4),
                List.of((byte) 3, (byte) 4, (byte) 5)
        ));
        b1.setB1_point(new Point(1, 3));
    }

    public static ExampleConfigurationB2 newExampleConfigurationB2_1() {
        ExampleConfigurationB2 b2 = new ExampleConfigurationB2();
        initializeExampleConfigurationB1_1(b2);
        b2.setB2_primChar('a');
        b2.setB2_refBool(true);
        b2.setB2_bigInteger(BI_1);
        b2.setB2_listShort(List.of((short) 2, (short) 3, (short) 4));
        b2.setB2_arrayInteger(new Integer[]{2, 3, 4});
        b2.setB2_setLong(asSet(2L, 3L, 4L));
        b2.setB2_arrayEmpty(new BigInteger[0]);
        b2.setB2_mapFloatFloat(asMap(1f, 2f, 3f, 4f));
        b2.setB2_setArrayDouble(asSet(
                new Double[]{1d, 2d, 3d, 4d},
                new Double[]{5d, 6d, 7d, 8d}
        ));
        b2.setB2_listPoint(List.of(
                new Point(1, 2),
                new Point(3, 4)
        ));
        return b2;
    }

    public static ExampleConfigurationB2 newExampleConfigurationB2_2() {
        ExampleConfigurationB2 b2 = new ExampleConfigurationB2();
        initializeExampleConfigurationB1_2(b2);
        b2.setB2_primChar('b');
        b2.setB2_refBool(false);
        b2.setB2_bigInteger(BI_2);
        b2.setB2_listShort(List.of((short) 3, (short) 4, (short) 5));
        b2.setB2_arrayInteger(new Integer[]{3, 4, 5});
        b2.setB2_setLong(asSet(3L, 4L, 5L));
        b2.setB2_arrayEmpty(new BigInteger[0]);
        b2.setB2_mapFloatFloat(asMap(2f, 3f, 4f, 5f));
        b2.setB2_setArrayDouble(asSet(
                new Double[]{2d, 3d, 4d, 5d},
                new Double[]{6d, 7d, 8d, 9d}
        ));
        b2.setB2_listPoint(List.of(
                new Point(2, 3),
                new Point(4, 5)
        ));
        return b2;
    }

    public static ExampleConfigurationNulls newExampleConfigurationNullsWithNullCollectionElements1() {
        ExampleConfigurationNulls result = new ExampleConfigurationNulls();
        result.setListNullString(Arrays.asList(null, "a", null));
        result.setArrayNullDouble(new Double[]{null, 1d, null});
        result.setSetNullInteger(asSet((Integer) null));
        result.setMapNullEnmKey(entriesAsMap(entry(null, BI_1)));
        result.setMapNullBigIntegerValue(entriesAsMap(entry(A, null)));
        return result;
    }

    public static ExampleConfigurationNulls newExampleConfigurationNullsWithNullCollectionElements2() {
        ExampleConfigurationNulls result = new ExampleConfigurationNulls();
        result.setListNullString(Arrays.asList("b", null, "c"));
        result.setArrayNullDouble(new Double[]{2d, null, 3d});
        result.setSetNullInteger(asSet(null, 1));
        result.setMapNullEnmKey(entriesAsMap(entry(null, BI_1), entry(A, BI_2)));
        result.setMapNullBigIntegerValue(entriesAsMap(entry(A, null), entry(B, BI_1)));
        return result;
    }

    public static ExampleConfigurationNulls newExampleConfigurationNullsWithoutNullCollectionElements1() {
        ExampleConfigurationNulls result = new ExampleConfigurationNulls();
        result.setListNullString(List.of("a"));
        result.setArrayNullDouble(new Double[]{1d});
        result.setSetNullInteger(asSet());
        result.setMapNullEnmKey(entriesAsMap());
        result.setMapNullBigIntegerValue(entriesAsMap());
        return result;
    }

    public static ExampleConfigurationNulls newExampleConfigurationNullsWithoutNullCollectionElements2() {
        ExampleConfigurationNulls result = new ExampleConfigurationNulls();
        result.setListNullString(Arrays.asList("b", "c"));
        result.setArrayNullDouble(new Double[]{2d, 3d});
        result.setSetNullInteger(asSet(1));
        result.setMapNullEnmKey(entriesAsMap(entry(A, BI_2)));
        result.setMapNullBigIntegerValue(entriesAsMap(entry(B, BI_1)));
        return result;
    }
}
