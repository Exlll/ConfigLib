package de.exlll.configlib.configurations;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Configuration
public class ExampleConfigurationA1 {
    /* IGNORED FIELDS */
    private static final int a1_staticFinalInt = 1;
    private static int a1_staticInt = 2;
    private final int a1_finalInt = 3;
    private transient int a1_transientInt = 4;
    @Ignore
    private int a1_ignoredInt = 5;
    @Ignore
    private String a1_ignoredString = "ignoredString";
    @Ignore
    private List<String> a1_ignoredListString = List.of("ignored", "list", "string");

    /* PRIMITIVE TYPES */
    private boolean a1_primBool;
    private char a1_primChar;
    private byte a1_primByte;
    private short a1_primShort;
    private int a1_primInt;
    private long a1_primLong;
    private float a1_primFloat;
    private double a1_primDouble;

    /* WRAPPER TYPES */
    private Boolean a1_refBool;
    private Character a1_refChar;
    private Byte a1_refByte;
    private Short a1_refShort;
    private Integer a1_refInt;
    private Long a1_refLong;
    private Float a1_refFloat;
    private Double a1_refDouble;

    /* OTHER TYPES */
    private String a1_string;
    private BigInteger a1_bigInteger;
    private BigDecimal a1_bigDecimal;
    private LocalDate a1_localDate;
    private LocalTime a1_localTime;
    private LocalDateTime a1_localDateTime;
    private ExampleEnum a1_Enm;

    /* OTHER CONFIGURATIONS */
    private ExampleConfigurationB1 a1_b1;
    private ExampleConfigurationB2 a1_b2;

    /* COLLECTIONS: Lists */
    private List<Boolean> a1_listBoolean;
    private List<Character> a1_listChar;
    private List<Byte> a1_listByte;
    private List<Short> a1_listShort;
    private List<Integer> a1_listInteger;
    private List<Long> a1_listLong;
    private List<Float> a1_listFloat;
    private List<Double> a1_listDouble;
    private List<String> a1_listString;
    private List<BigInteger> a1_listBigInteger;
    private List<BigDecimal> a1_listBigDecimal;
    private List<LocalDate> a1_listLocalDate;
    private List<LocalTime> a1_listLocalTime;
    private List<LocalDateTime> a1_listLocalDateTime;
    private List<ExampleEnum> a1_listEnm;
    private List<ExampleConfigurationB1> a1_listB1;
    private List<ExampleConfigurationB2> a1_listB2;

    /* COLLECTIONS: Arrays */
    private boolean[] a1_arrayPrimBoolean;
    private char[] a1_arrayPrimChar;
    private byte[] a1_arrayPrimByte;
    private short[] a1_arrayPrimShort;
    private int[] a1_arrayPrimInteger;
    private long[] a1_arrayPrimLong;
    private float[] a1_arrayPrimFloat;
    private double[] a1_arrayPrimDouble;
    private Boolean[] a1_arrayBoolean;
    private Character[] a1_arrayChar;
    private Byte[] a1_arrayByte;
    private Short[] a1_arrayShort;
    private Integer[] a1_arrayInteger;
    private Long[] a1_arrayLong;
    private Float[] a1_arrayFloat;
    private Double[] a1_arrayDouble;
    private String[] a1_arrayString;
    private BigInteger[] a1_arrayBigInteger;
    private BigDecimal[] a1_arrayBigDecimal;
    private LocalDate[] a1_arrayLocalDate;
    private LocalTime[] a1_arrayLocalTime;
    private LocalDateTime[] a1_arrayLocalDateTime;
    private ExampleEnum[] a1_arrayEnm;
    private ExampleConfigurationB1[] a1_arrayB1;
    private ExampleConfigurationB2[] a1_arrayB2;

    /* COLLECTIONS: Sets */
    private Set<Boolean> a1_setBoolean;
    private Set<Character> a1_setChar;
    private Set<Byte> a1_setByte;
    private Set<Short> a1_setShort;
    private Set<Integer> a1_setInteger;
    private Set<Long> a1_setLong;
    private Set<Float> a1_setFloat;
    private Set<Double> a1_setDouble;
    private Set<String> a1_setString;
    private Set<BigInteger> a1_setBigInteger;
    private Set<BigDecimal> a1_setBigDecimal;
    private Set<LocalDate> a1_setLocalDate;
    private Set<LocalTime> a1_setLocalTime;
    private Set<LocalDateTime> a1_setLocalDateTime;
    private Set<ExampleEnum> a1_setEnm;
    private Set<ExampleConfigurationB1> a1_setB1;
    private Set<ExampleConfigurationB2> a1_setB2;

    /* COLLECTIONS: Maps */
    private Map<Boolean, Boolean> a1_mapBooleanBoolean;
    private Map<Character, Character> a1_mapCharChar;
    private Map<Byte, Byte> a1_mapByteByte;
    private Map<Short, Short> a1_mapShortShort;
    private Map<Integer, Integer> a1_mapIntegerInteger;
    private Map<Long, Long> a1_mapLongLong;
    private Map<Float, Float> a1_mapFloatFloat;
    private Map<Double, Double> a1_mapDoubleDouble;
    private Map<String, String> a1_mapStringString;
    private Map<BigInteger, BigInteger> a1_mapBigIntegerBigInteger;
    private Map<BigDecimal, BigDecimal> a1_mapBigDecimalBigDecimal;
    private Map<LocalDate, LocalDate> a1_mapLocalDateLocalDate;
    private Map<LocalTime, LocalTime> a1_mapLocalTimeLocalTime;
    private Map<LocalDateTime, LocalDateTime> a1_mapLocalDateTimeLocalDateTime;
    private Map<ExampleEnum, ExampleEnum> a1_mapEnmEnm;

    private Map<Integer, ExampleConfigurationB1> a1_mapIntegerB1;
    private Map<ExampleEnum, ExampleConfigurationB2> a1_mapEnmB2;

    /* COLLECTIONS: Empty */
    private List<Boolean> a1_listEmpty;
    private Integer[] a1_arrayEmpty;
    private Set<Double> a1_setEmpty;
    private Map<ExampleEnum, ExampleConfigurationB1> a1_mapEmpty;

    /* COLLECTIONS: Nested */
    private List<List<Byte>> a1_listListByte;
    private List<Float[]> a1_listArrayFloat;
    private List<Set<String>> a1_listSetString;
    private List<Map<ExampleEnum, LocalDate>> a1_listMapEnmLocalDate;

    private Set<Set<Short>> a1_setSetShort;
    private Set<Double[]> a1_setArrayDouble;
    private Set<List<String>> a1_setListString;
    private Set<Map<ExampleEnum, LocalTime>> a1_setMapEnmLocalTime;

    private Map<Integer, Map<Long, Boolean>> a1_mapIntegerMapLongBoolean;
    private Map<String, List<ExampleConfigurationB1>> a1_mapStringListB1;
    private Map<BigInteger, BigDecimal[]> a1_mapBigIntegerArrayBigDecimal;
    private Map<ExampleEnum, Set<ExampleConfigurationB2>> a1_mapEnmSetB2;

    private Map<Integer, List<Map<Short, Set<ExampleConfigurationB2>>>>
            a1_mapIntegerListMapShortSetB2;

    private boolean[][] a1_arrayArrayPrimBoolean;
    private char[][] a1_arrayArrayPrimChar;
    private byte[][] a1_arrayArrayPrimByte;
    private short[][] a1_arrayArrayPrimShort;
    private int[][] a1_arrayArrayPrimInteger;
    private long[][] a1_arrayArrayPrimLong;
    private float[][] a1_arrayArrayPrimFloat;
    private double[][] a1_arrayArrayPrimDouble;
    private Boolean[][] a1_arrayArrayBoolean;
    private Character[][] a1_arrayArrayChar;
    private Byte[][] a1_arrayArrayByte;
    private Short[][] a1_arrayArrayShort;
    private Integer[][] a1_arrayArrayInteger;
    private Long[][] a1_arrayArrayLong;
    private Float[][] a1_arrayArrayFloat;
    private Double[][] a1_arrayArrayDouble;
    private String[][] a1_arrayArrayString;
    private BigInteger[][] a1_arrayArrayBigInteger;
    private BigDecimal[][] a1_arrayArrayBigDecimal;
    private LocalDate[][] a1_arrayArrayLocalDate;
    private LocalTime[][] a1_arrayArrayLocalTime;
    private LocalDateTime[][] a1_arrayArrayLocalDateTime;
    private ExampleEnum[][] a1_arrayArrayEnm;
    private ExampleConfigurationB1[][] a1_arrayArrayB1;
    private ExampleConfigurationB2[][] a1_arrayArrayB2;

    /* CUSTOM CONVERTERS */
    private Point a1_point;
    private List<Point> a1_listPoint;
    private Point[] a1_arrayPoint;
    private Set<Point> a1_setPoint;
    private Map<ExampleEnum, List<Point>> a1_mapEnmListPoint;

    public static int getA1_staticFinalInt() {
        return a1_staticFinalInt;
    }

    public static int getA1_staticInt() {
        return a1_staticInt;
    }

    public int getA1_finalInt() {
        return a1_finalInt;
    }

    public int getA1_transientInt() {
        return a1_transientInt;
    }

    public int getA1_ignoredInt() {
        return a1_ignoredInt;
    }

    public String getA1_ignoredString() {
        return a1_ignoredString;
    }

    public List<String> getA1_ignoredListString() {
        return a1_ignoredListString;
    }

    public boolean isA1_primBool() {
        return a1_primBool;
    }

    public void setA1_primBool(boolean a1_primBool) {
        this.a1_primBool = a1_primBool;
    }

    public char getA1_primChar() {
        return a1_primChar;
    }

    public void setA1_primChar(char a1_primChar) {
        this.a1_primChar = a1_primChar;
    }

    public byte getA1_primByte() {
        return a1_primByte;
    }

    public void setA1_primByte(byte a1_primByte) {
        this.a1_primByte = a1_primByte;
    }

    public short getA1_primShort() {
        return a1_primShort;
    }

    public void setA1_primShort(short a1_primShort) {
        this.a1_primShort = a1_primShort;
    }

    public int getA1_primInt() {
        return a1_primInt;
    }

    public void setA1_primInt(int a1_primInt) {
        this.a1_primInt = a1_primInt;
    }

    public long getA1_primLong() {
        return a1_primLong;
    }

    public void setA1_primLong(long a1_primLong) {
        this.a1_primLong = a1_primLong;
    }

    public float getA1_primFloat() {
        return a1_primFloat;
    }

    public void setA1_primFloat(float a1_primFloat) {
        this.a1_primFloat = a1_primFloat;
    }

    public double getA1_primDouble() {
        return a1_primDouble;
    }

    public void setA1_primDouble(double a1_primDouble) {
        this.a1_primDouble = a1_primDouble;
    }

    public Boolean getA1_refBool() {
        return a1_refBool;
    }

    public void setA1_refBool(Boolean a1_refBool) {
        this.a1_refBool = a1_refBool;
    }

    public Character getA1_refChar() {
        return a1_refChar;
    }

    public void setA1_refChar(Character a1_refChar) {
        this.a1_refChar = a1_refChar;
    }

    public Byte getA1_refByte() {
        return a1_refByte;
    }

    public void setA1_refByte(Byte a1_refByte) {
        this.a1_refByte = a1_refByte;
    }

    public Short getA1_refShort() {
        return a1_refShort;
    }

    public void setA1_refShort(Short a1_refShort) {
        this.a1_refShort = a1_refShort;
    }

    public Integer getA1_refInt() {
        return a1_refInt;
    }

    public void setA1_refInt(Integer a1_refInt) {
        this.a1_refInt = a1_refInt;
    }

    public Long getA1_refLong() {
        return a1_refLong;
    }

    public void setA1_refLong(Long a1_refLong) {
        this.a1_refLong = a1_refLong;
    }

    public Float getA1_refFloat() {
        return a1_refFloat;
    }

    public void setA1_refFloat(Float a1_refFloat) {
        this.a1_refFloat = a1_refFloat;
    }

    public Double getA1_refDouble() {
        return a1_refDouble;
    }

    public void setA1_refDouble(Double a1_refDouble) {
        this.a1_refDouble = a1_refDouble;
    }

    public String getA1_string() {
        return a1_string;
    }

    public void setA1_string(String a1_string) {
        this.a1_string = a1_string;
    }

    public BigInteger getA1_bigInteger() {
        return a1_bigInteger;
    }

    public void setA1_bigInteger(BigInteger a1_bigInteger) {
        this.a1_bigInteger = a1_bigInteger;
    }

    public BigDecimal getA1_bigDecimal() {
        return a1_bigDecimal;
    }

    public void setA1_bigDecimal(BigDecimal a1_bigDecimal) {
        this.a1_bigDecimal = a1_bigDecimal;
    }

    public LocalDate getA1_localDate() {
        return a1_localDate;
    }

    public void setA1_localDate(LocalDate a1_localDate) {
        this.a1_localDate = a1_localDate;
    }

    public LocalTime getA1_localTime() {
        return a1_localTime;
    }

    public void setA1_localTime(LocalTime a1_localTime) {
        this.a1_localTime = a1_localTime;
    }

    public LocalDateTime getA1_localDateTime() {
        return a1_localDateTime;
    }

    public void setA1_localDateTime(LocalDateTime a1_localDateTime) {
        this.a1_localDateTime = a1_localDateTime;
    }

    public ExampleEnum getA1_Enm() {
        return a1_Enm;
    }

    public void setA1_Enm(ExampleEnum a1_Enm) {
        this.a1_Enm = a1_Enm;
    }

    public ExampleConfigurationB1 getA1_b1() {
        return a1_b1;
    }

    public void setA1_b1(ExampleConfigurationB1 a1_b1) {
        this.a1_b1 = a1_b1;
    }

    public ExampleConfigurationB2 getA1_b2() {
        return a1_b2;
    }

    public void setA1_b2(ExampleConfigurationB2 a1_b2) {
        this.a1_b2 = a1_b2;
    }

    public List<Boolean> getA1_listBoolean() {
        return a1_listBoolean;
    }

    public void setA1_listBoolean(List<Boolean> a1_listBoolean) {
        this.a1_listBoolean = a1_listBoolean;
    }

    public List<Character> getA1_listChar() {
        return a1_listChar;
    }

    public void setA1_listChar(List<Character> a1_listChar) {
        this.a1_listChar = a1_listChar;
    }

    public List<Byte> getA1_listByte() {
        return a1_listByte;
    }

    public void setA1_listByte(List<Byte> a1_listByte) {
        this.a1_listByte = a1_listByte;
    }

    public List<Short> getA1_listShort() {
        return a1_listShort;
    }

    public void setA1_listShort(List<Short> a1_listShort) {
        this.a1_listShort = a1_listShort;
    }

    public List<Integer> getA1_listInteger() {
        return a1_listInteger;
    }

    public void setA1_listInteger(List<Integer> a1_listInteger) {
        this.a1_listInteger = a1_listInteger;
    }

    public List<Long> getA1_listLong() {
        return a1_listLong;
    }

    public void setA1_listLong(List<Long> a1_listLong) {
        this.a1_listLong = a1_listLong;
    }

    public List<Float> getA1_listFloat() {
        return a1_listFloat;
    }

    public void setA1_listFloat(List<Float> a1_listFloat) {
        this.a1_listFloat = a1_listFloat;
    }

    public List<Double> getA1_listDouble() {
        return a1_listDouble;
    }

    public void setA1_listDouble(List<Double> a1_listDouble) {
        this.a1_listDouble = a1_listDouble;
    }

    public List<String> getA1_listString() {
        return a1_listString;
    }

    public void setA1_listString(List<String> a1_listString) {
        this.a1_listString = a1_listString;
    }

    public List<BigInteger> getA1_listBigInteger() {
        return a1_listBigInteger;
    }

    public void setA1_listBigInteger(List<BigInteger> a1_listBigInteger) {
        this.a1_listBigInteger = a1_listBigInteger;
    }

    public List<BigDecimal> getA1_listBigDecimal() {
        return a1_listBigDecimal;
    }

    public void setA1_listBigDecimal(List<BigDecimal> a1_listBigDecimal) {
        this.a1_listBigDecimal = a1_listBigDecimal;
    }

    public List<LocalDate> getA1_listLocalDate() {
        return a1_listLocalDate;
    }

    public void setA1_listLocalDate(List<LocalDate> a1_listLocalDate) {
        this.a1_listLocalDate = a1_listLocalDate;
    }

    public List<LocalTime> getA1_listLocalTime() {
        return a1_listLocalTime;
    }

    public void setA1_listLocalTime(List<LocalTime> a1_listLocalTime) {
        this.a1_listLocalTime = a1_listLocalTime;
    }

    public List<LocalDateTime> getA1_listLocalDateTime() {
        return a1_listLocalDateTime;
    }

    public void setA1_listLocalDateTime(List<LocalDateTime> a1_listLocalDateTime) {
        this.a1_listLocalDateTime = a1_listLocalDateTime;
    }

    public List<ExampleEnum> getA1_listEnm() {
        return a1_listEnm;
    }

    public void setA1_listEnm(List<ExampleEnum> a1_listEnm) {
        this.a1_listEnm = a1_listEnm;
    }

    public List<ExampleConfigurationB1> getA1_listB1() {
        return a1_listB1;
    }

    public void setA1_listB1(List<ExampleConfigurationB1> a1_listB1) {
        this.a1_listB1 = a1_listB1;
    }

    public List<ExampleConfigurationB2> getA1_listB2() {
        return a1_listB2;
    }

    public void setA1_listB2(List<ExampleConfigurationB2> a1_listB2) {
        this.a1_listB2 = a1_listB2;
    }

    public boolean[] getA1_arrayPrimBoolean() {
        return a1_arrayPrimBoolean;
    }

    public void setA1_arrayPrimBoolean(boolean[] a1_arrayPrimBoolean) {
        this.a1_arrayPrimBoolean = a1_arrayPrimBoolean;
    }

    public char[] getA1_arrayPrimChar() {
        return a1_arrayPrimChar;
    }

    public void setA1_arrayPrimChar(char[] a1_arrayPrimChar) {
        this.a1_arrayPrimChar = a1_arrayPrimChar;
    }

    public byte[] getA1_arrayPrimByte() {
        return a1_arrayPrimByte;
    }

    public void setA1_arrayPrimByte(byte[] a1_arrayPrimByte) {
        this.a1_arrayPrimByte = a1_arrayPrimByte;
    }

    public short[] getA1_arrayPrimShort() {
        return a1_arrayPrimShort;
    }

    public void setA1_arrayPrimShort(short[] a1_arrayPrimShort) {
        this.a1_arrayPrimShort = a1_arrayPrimShort;
    }

    public int[] getA1_arrayPrimInteger() {
        return a1_arrayPrimInteger;
    }

    public void setA1_arrayPrimInteger(int[] a1_arrayPrimInteger) {
        this.a1_arrayPrimInteger = a1_arrayPrimInteger;
    }

    public long[] getA1_arrayPrimLong() {
        return a1_arrayPrimLong;
    }

    public void setA1_arrayPrimLong(long[] a1_arrayPrimLong) {
        this.a1_arrayPrimLong = a1_arrayPrimLong;
    }

    public float[] getA1_arrayPrimFloat() {
        return a1_arrayPrimFloat;
    }

    public void setA1_arrayPrimFloat(float[] a1_arrayPrimFloat) {
        this.a1_arrayPrimFloat = a1_arrayPrimFloat;
    }

    public double[] getA1_arrayPrimDouble() {
        return a1_arrayPrimDouble;
    }

    public void setA1_arrayPrimDouble(double[] a1_arrayPrimDouble) {
        this.a1_arrayPrimDouble = a1_arrayPrimDouble;
    }

    public Boolean[] getA1_arrayBoolean() {
        return a1_arrayBoolean;
    }

    public void setA1_arrayBoolean(Boolean[] a1_arrayBoolean) {
        this.a1_arrayBoolean = a1_arrayBoolean;
    }

    public Character[] getA1_arrayChar() {
        return a1_arrayChar;
    }

    public void setA1_arrayChar(Character[] a1_arrayChar) {
        this.a1_arrayChar = a1_arrayChar;
    }

    public Byte[] getA1_arrayByte() {
        return a1_arrayByte;
    }

    public void setA1_arrayByte(Byte[] a1_arrayByte) {
        this.a1_arrayByte = a1_arrayByte;
    }

    public Short[] getA1_arrayShort() {
        return a1_arrayShort;
    }

    public void setA1_arrayShort(Short[] a1_arrayShort) {
        this.a1_arrayShort = a1_arrayShort;
    }

    public Integer[] getA1_arrayInteger() {
        return a1_arrayInteger;
    }

    public void setA1_arrayInteger(Integer[] a1_arrayInteger) {
        this.a1_arrayInteger = a1_arrayInteger;
    }

    public Long[] getA1_arrayLong() {
        return a1_arrayLong;
    }

    public void setA1_arrayLong(Long[] a1_arrayLong) {
        this.a1_arrayLong = a1_arrayLong;
    }

    public Float[] getA1_arrayFloat() {
        return a1_arrayFloat;
    }

    public void setA1_arrayFloat(Float[] a1_arrayFloat) {
        this.a1_arrayFloat = a1_arrayFloat;
    }

    public Double[] getA1_arrayDouble() {
        return a1_arrayDouble;
    }

    public void setA1_arrayDouble(Double[] a1_arrayDouble) {
        this.a1_arrayDouble = a1_arrayDouble;
    }

    public String[] getA1_arrayString() {
        return a1_arrayString;
    }

    public void setA1_arrayString(String[] a1_arrayString) {
        this.a1_arrayString = a1_arrayString;
    }

    public BigInteger[] getA1_arrayBigInteger() {
        return a1_arrayBigInteger;
    }

    public void setA1_arrayBigInteger(BigInteger[] a1_arrayBigInteger) {
        this.a1_arrayBigInteger = a1_arrayBigInteger;
    }

    public BigDecimal[] getA1_arrayBigDecimal() {
        return a1_arrayBigDecimal;
    }

    public void setA1_arrayBigDecimal(BigDecimal[] a1_arrayBigDecimal) {
        this.a1_arrayBigDecimal = a1_arrayBigDecimal;
    }

    public LocalDate[] getA1_arrayLocalDate() {
        return a1_arrayLocalDate;
    }

    public void setA1_arrayLocalDate(LocalDate[] a1_arrayLocalDate) {
        this.a1_arrayLocalDate = a1_arrayLocalDate;
    }

    public LocalTime[] getA1_arrayLocalTime() {
        return a1_arrayLocalTime;
    }

    public void setA1_arrayLocalTime(LocalTime[] a1_arrayLocalTime) {
        this.a1_arrayLocalTime = a1_arrayLocalTime;
    }

    public LocalDateTime[] getA1_arrayLocalDateTime() {
        return a1_arrayLocalDateTime;
    }

    public void setA1_arrayLocalDateTime(LocalDateTime[] a1_arrayLocalDateTime) {
        this.a1_arrayLocalDateTime = a1_arrayLocalDateTime;
    }

    public ExampleEnum[] getA1_arrayEnm() {
        return a1_arrayEnm;
    }

    public void setA1_arrayEnm(ExampleEnum[] a1_arrayEnm) {
        this.a1_arrayEnm = a1_arrayEnm;
    }

    public ExampleConfigurationB1[] getA1_arrayB1() {
        return a1_arrayB1;
    }

    public void setA1_arrayB1(ExampleConfigurationB1[] a1_arrayB1) {
        this.a1_arrayB1 = a1_arrayB1;
    }

    public ExampleConfigurationB2[] getA1_arrayB2() {
        return a1_arrayB2;
    }

    public void setA1_arrayB2(ExampleConfigurationB2[] a1_arrayB2) {
        this.a1_arrayB2 = a1_arrayB2;
    }

    public Set<Boolean> getA1_setBoolean() {
        return a1_setBoolean;
    }

    public void setA1_setBoolean(Set<Boolean> a1_setBoolean) {
        this.a1_setBoolean = a1_setBoolean;
    }

    public Set<Character> getA1_setChar() {
        return a1_setChar;
    }

    public void setA1_setChar(Set<Character> a1_setChar) {
        this.a1_setChar = a1_setChar;
    }

    public Set<Byte> getA1_setByte() {
        return a1_setByte;
    }

    public void setA1_setByte(Set<Byte> a1_setByte) {
        this.a1_setByte = a1_setByte;
    }

    public Set<Short> getA1_setShort() {
        return a1_setShort;
    }

    public void setA1_setShort(Set<Short> a1_setShort) {
        this.a1_setShort = a1_setShort;
    }

    public Set<Integer> getA1_setInteger() {
        return a1_setInteger;
    }

    public void setA1_setInteger(Set<Integer> a1_setInteger) {
        this.a1_setInteger = a1_setInteger;
    }

    public Set<Long> getA1_setLong() {
        return a1_setLong;
    }

    public void setA1_setLong(Set<Long> a1_setLong) {
        this.a1_setLong = a1_setLong;
    }

    public Set<Float> getA1_setFloat() {
        return a1_setFloat;
    }

    public void setA1_setFloat(Set<Float> a1_setFloat) {
        this.a1_setFloat = a1_setFloat;
    }

    public Set<Double> getA1_setDouble() {
        return a1_setDouble;
    }

    public void setA1_setDouble(Set<Double> a1_setDouble) {
        this.a1_setDouble = a1_setDouble;
    }

    public Set<String> getA1_setString() {
        return a1_setString;
    }

    public void setA1_setString(Set<String> a1_setString) {
        this.a1_setString = a1_setString;
    }

    public Set<BigInteger> getA1_setBigInteger() {
        return a1_setBigInteger;
    }

    public void setA1_setBigInteger(Set<BigInteger> a1_setBigInteger) {
        this.a1_setBigInteger = a1_setBigInteger;
    }

    public Set<BigDecimal> getA1_setBigDecimal() {
        return a1_setBigDecimal;
    }

    public void setA1_setBigDecimal(Set<BigDecimal> a1_setBigDecimal) {
        this.a1_setBigDecimal = a1_setBigDecimal;
    }

    public Set<LocalDate> getA1_setLocalDate() {
        return a1_setLocalDate;
    }

    public void setA1_setLocalDate(Set<LocalDate> a1_setLocalDate) {
        this.a1_setLocalDate = a1_setLocalDate;
    }

    public Set<LocalTime> getA1_setLocalTime() {
        return a1_setLocalTime;
    }

    public void setA1_setLocalTime(Set<LocalTime> a1_setLocalTime) {
        this.a1_setLocalTime = a1_setLocalTime;
    }

    public Set<LocalDateTime> getA1_setLocalDateTime() {
        return a1_setLocalDateTime;
    }

    public void setA1_setLocalDateTime(Set<LocalDateTime> a1_setLocalDateTime) {
        this.a1_setLocalDateTime = a1_setLocalDateTime;
    }

    public Set<ExampleEnum> getA1_setEnm() {
        return a1_setEnm;
    }

    public void setA1_setEnm(Set<ExampleEnum> a1_setEnm) {
        this.a1_setEnm = a1_setEnm;
    }

    public Set<ExampleConfigurationB1> getA1_setB1() {
        return a1_setB1;
    }

    public void setA1_setB1(Set<ExampleConfigurationB1> a1_setB1) {
        this.a1_setB1 = a1_setB1;
    }

    public Set<ExampleConfigurationB2> getA1_setB2() {
        return a1_setB2;
    }

    public void setA1_setB2(Set<ExampleConfigurationB2> a1_setB2) {
        this.a1_setB2 = a1_setB2;
    }

    public Map<Boolean, Boolean> getA1_mapBooleanBoolean() {
        return a1_mapBooleanBoolean;
    }

    public void setA1_mapBooleanBoolean(Map<Boolean, Boolean> a1_mapBooleanBoolean) {
        this.a1_mapBooleanBoolean = a1_mapBooleanBoolean;
    }

    public Map<Character, Character> getA1_mapCharChar() {
        return a1_mapCharChar;
    }

    public void setA1_mapCharChar(Map<Character, Character> a1_mapCharChar) {
        this.a1_mapCharChar = a1_mapCharChar;
    }

    public Map<Byte, Byte> getA1_mapByteByte() {
        return a1_mapByteByte;
    }

    public void setA1_mapByteByte(Map<Byte, Byte> a1_mapByteByte) {
        this.a1_mapByteByte = a1_mapByteByte;
    }

    public Map<Short, Short> getA1_mapShortShort() {
        return a1_mapShortShort;
    }

    public void setA1_mapShortShort(Map<Short, Short> a1_mapShortShort) {
        this.a1_mapShortShort = a1_mapShortShort;
    }

    public Map<Integer, Integer> getA1_mapIntegerInteger() {
        return a1_mapIntegerInteger;
    }

    public void setA1_mapIntegerInteger(Map<Integer, Integer> a1_mapIntegerInteger) {
        this.a1_mapIntegerInteger = a1_mapIntegerInteger;
    }

    public Map<Long, Long> getA1_mapLongLong() {
        return a1_mapLongLong;
    }

    public void setA1_mapLongLong(Map<Long, Long> a1_mapLongLong) {
        this.a1_mapLongLong = a1_mapLongLong;
    }

    public Map<Float, Float> getA1_mapFloatFloat() {
        return a1_mapFloatFloat;
    }

    public void setA1_mapFloatFloat(Map<Float, Float> a1_mapFloatFloat) {
        this.a1_mapFloatFloat = a1_mapFloatFloat;
    }

    public Map<Double, Double> getA1_mapDoubleDouble() {
        return a1_mapDoubleDouble;
    }

    public void setA1_mapDoubleDouble(Map<Double, Double> a1_mapDoubleDouble) {
        this.a1_mapDoubleDouble = a1_mapDoubleDouble;
    }

    public Map<String, String> getA1_mapStringString() {
        return a1_mapStringString;
    }

    public void setA1_mapStringString(Map<String, String> a1_mapStringString) {
        this.a1_mapStringString = a1_mapStringString;
    }

    public Map<BigInteger, BigInteger> getA1_mapBigIntegerBigInteger() {
        return a1_mapBigIntegerBigInteger;
    }

    public void setA1_mapBigIntegerBigInteger(Map<BigInteger, BigInteger> a1_mapBigIntegerBigInteger) {
        this.a1_mapBigIntegerBigInteger = a1_mapBigIntegerBigInteger;
    }

    public Map<BigDecimal, BigDecimal> getA1_mapBigDecimalBigDecimal() {
        return a1_mapBigDecimalBigDecimal;
    }

    public void setA1_mapBigDecimalBigDecimal(Map<BigDecimal, BigDecimal> a1_mapBigDecimalBigDecimal) {
        this.a1_mapBigDecimalBigDecimal = a1_mapBigDecimalBigDecimal;
    }

    public Map<LocalDate, LocalDate> getA1_mapLocalDateLocalDate() {
        return a1_mapLocalDateLocalDate;
    }

    public void setA1_mapLocalDateLocalDate(Map<LocalDate, LocalDate> a1_mapLocalDateLocalDate) {
        this.a1_mapLocalDateLocalDate = a1_mapLocalDateLocalDate;
    }

    public Map<LocalTime, LocalTime> getA1_mapLocalTimeLocalTime() {
        return a1_mapLocalTimeLocalTime;
    }

    public void setA1_mapLocalTimeLocalTime(Map<LocalTime, LocalTime> a1_mapLocalTimeLocalTime) {
        this.a1_mapLocalTimeLocalTime = a1_mapLocalTimeLocalTime;
    }

    public Map<LocalDateTime, LocalDateTime> getA1_mapLocalDateTimeLocalDateTime() {
        return a1_mapLocalDateTimeLocalDateTime;
    }

    public void setA1_mapLocalDateTimeLocalDateTime(Map<LocalDateTime, LocalDateTime> a1_mapLocalDateTimeLocalDateTime) {
        this.a1_mapLocalDateTimeLocalDateTime = a1_mapLocalDateTimeLocalDateTime;
    }

    public Map<ExampleEnum, ExampleEnum> getA1_mapEnmEnm() {
        return a1_mapEnmEnm;
    }

    public void setA1_mapEnmEnm(Map<ExampleEnum, ExampleEnum> a1_mapEnmEnm) {
        this.a1_mapEnmEnm = a1_mapEnmEnm;
    }

    public Map<Integer, ExampleConfigurationB1> getA1_mapIntegerB1() {
        return a1_mapIntegerB1;
    }

    public void setA1_mapIntegerB1(Map<Integer, ExampleConfigurationB1> a1_mapIntegerB1) {
        this.a1_mapIntegerB1 = a1_mapIntegerB1;
    }

    public Map<ExampleEnum, ExampleConfigurationB2> getA1_mapEnmB2() {
        return a1_mapEnmB2;
    }

    public void setA1_mapEnmB2(Map<ExampleEnum, ExampleConfigurationB2> a1_mapEnmB2) {
        this.a1_mapEnmB2 = a1_mapEnmB2;
    }

    public List<Boolean> getA1_listEmpty() {
        return a1_listEmpty;
    }

    public void setA1_listEmpty(List<Boolean> a1_listEmpty) {
        this.a1_listEmpty = a1_listEmpty;
    }

    public Integer[] getA1_arrayEmpty() {
        return a1_arrayEmpty;
    }

    public void setA1_arrayEmpty(Integer[] a1_arrayEmpty) {
        this.a1_arrayEmpty = a1_arrayEmpty;
    }

    public Set<Double> getA1_setEmpty() {
        return a1_setEmpty;
    }

    public void setA1_setEmpty(Set<Double> a1_setEmpty) {
        this.a1_setEmpty = a1_setEmpty;
    }

    public Map<ExampleEnum, ExampleConfigurationB1> getA1_mapEmpty() {
        return a1_mapEmpty;
    }

    public void setA1_mapEmpty(Map<ExampleEnum, ExampleConfigurationB1> a1_mapEmpty) {
        this.a1_mapEmpty = a1_mapEmpty;
    }

    public List<List<Byte>> getA1_listListByte() {
        return a1_listListByte;
    }

    public void setA1_listListByte(List<List<Byte>> a1_listListByte) {
        this.a1_listListByte = a1_listListByte;
    }

    public List<Float[]> getA1_listArrayFloat() {
        return a1_listArrayFloat;
    }

    public void setA1_listArrayFloat(List<Float[]> a1_listArrayFloat) {
        this.a1_listArrayFloat = a1_listArrayFloat;
    }

    public List<Set<String>> getA1_listSetString() {
        return a1_listSetString;
    }

    public void setA1_listSetString(List<Set<String>> a1_listSetString) {
        this.a1_listSetString = a1_listSetString;
    }

    public List<Map<ExampleEnum, LocalDate>> getA1_listMapEnmLocalDate() {
        return a1_listMapEnmLocalDate;
    }

    public void setA1_listMapEnmLocalDate(List<Map<ExampleEnum, LocalDate>> a1_listMapEnmLocalDate) {
        this.a1_listMapEnmLocalDate = a1_listMapEnmLocalDate;
    }

    public Set<Set<Short>> getA1_setSetShort() {
        return a1_setSetShort;
    }

    public void setA1_setSetShort(Set<Set<Short>> a1_setSetShort) {
        this.a1_setSetShort = a1_setSetShort;
    }

    public Set<Double[]> getA1_setArrayDouble() {
        return a1_setArrayDouble;
    }

    public void setA1_setArrayDouble(Set<Double[]> a1_setArrayDouble) {
        this.a1_setArrayDouble = a1_setArrayDouble;
    }

    public Set<List<String>> getA1_setListString() {
        return a1_setListString;
    }

    public void setA1_setListString(Set<List<String>> a1_setListString) {
        this.a1_setListString = a1_setListString;
    }

    public Set<Map<ExampleEnum, LocalTime>> getA1_setMapEnmLocalTime() {
        return a1_setMapEnmLocalTime;
    }

    public void setA1_setMapEnmLocalTime(Set<Map<ExampleEnum, LocalTime>> a1_setMapEnmLocalTime) {
        this.a1_setMapEnmLocalTime = a1_setMapEnmLocalTime;
    }

    public Map<Integer, Map<Long, Boolean>> getA1_mapIntegerMapLongBoolean() {
        return a1_mapIntegerMapLongBoolean;
    }

    public void setA1_mapIntegerMapLongBoolean(Map<Integer, Map<Long, Boolean>> a1_mapIntegerMapLongBoolean) {
        this.a1_mapIntegerMapLongBoolean = a1_mapIntegerMapLongBoolean;
    }

    public Map<String, List<ExampleConfigurationB1>> getA1_mapStringListB1() {
        return a1_mapStringListB1;
    }

    public void setA1_mapStringListB1(Map<String, List<ExampleConfigurationB1>> a1_mapStringListB1) {
        this.a1_mapStringListB1 = a1_mapStringListB1;
    }

    public Map<BigInteger, BigDecimal[]> getA1_mapBigIntegerArrayBigDecimal() {
        return a1_mapBigIntegerArrayBigDecimal;
    }

    public void setA1_mapBigIntegerArrayBigDecimal(Map<BigInteger, BigDecimal[]> a1_mapBigIntegerArrayBigDecimal) {
        this.a1_mapBigIntegerArrayBigDecimal = a1_mapBigIntegerArrayBigDecimal;
    }

    public Map<ExampleEnum, Set<ExampleConfigurationB2>> getA1_mapEnmSetB2() {
        return a1_mapEnmSetB2;
    }

    public void setA1_mapEnmSetB2(Map<ExampleEnum, Set<ExampleConfigurationB2>> a1_mapEnmSetB2) {
        this.a1_mapEnmSetB2 = a1_mapEnmSetB2;
    }

    public Map<Integer, List<Map<Short, Set<ExampleConfigurationB2>>>> getA1_mapIntegerListMapShortSetB2() {
        return a1_mapIntegerListMapShortSetB2;
    }

    public void setA1_mapIntegerListMapShortSetB2(Map<Integer, List<Map<Short, Set<ExampleConfigurationB2>>>> a1_mapIntegerListMapShortSetB2) {
        this.a1_mapIntegerListMapShortSetB2 = a1_mapIntegerListMapShortSetB2;
    }

    public boolean[][] getA1_arrayArrayPrimBoolean() {
        return a1_arrayArrayPrimBoolean;
    }

    public void setA1_arrayArrayPrimBoolean(boolean[][] a1_arrayArrayPrimBoolean) {
        this.a1_arrayArrayPrimBoolean = a1_arrayArrayPrimBoolean;
    }

    public char[][] getA1_arrayArrayPrimChar() {
        return a1_arrayArrayPrimChar;
    }

    public void setA1_arrayArrayPrimChar(char[][] a1_arrayArrayPrimChar) {
        this.a1_arrayArrayPrimChar = a1_arrayArrayPrimChar;
    }

    public byte[][] getA1_arrayArrayPrimByte() {
        return a1_arrayArrayPrimByte;
    }

    public void setA1_arrayArrayPrimByte(byte[][] a1_arrayArrayPrimByte) {
        this.a1_arrayArrayPrimByte = a1_arrayArrayPrimByte;
    }

    public short[][] getA1_arrayArrayPrimShort() {
        return a1_arrayArrayPrimShort;
    }

    public void setA1_arrayArrayPrimShort(short[][] a1_arrayArrayPrimShort) {
        this.a1_arrayArrayPrimShort = a1_arrayArrayPrimShort;
    }

    public int[][] getA1_arrayArrayPrimInteger() {
        return a1_arrayArrayPrimInteger;
    }

    public void setA1_arrayArrayPrimInteger(int[][] a1_arrayArrayPrimInteger) {
        this.a1_arrayArrayPrimInteger = a1_arrayArrayPrimInteger;
    }

    public long[][] getA1_arrayArrayPrimLong() {
        return a1_arrayArrayPrimLong;
    }

    public void setA1_arrayArrayPrimLong(long[][] a1_arrayArrayPrimLong) {
        this.a1_arrayArrayPrimLong = a1_arrayArrayPrimLong;
    }

    public float[][] getA1_arrayArrayPrimFloat() {
        return a1_arrayArrayPrimFloat;
    }

    public void setA1_arrayArrayPrimFloat(float[][] a1_arrayArrayPrimFloat) {
        this.a1_arrayArrayPrimFloat = a1_arrayArrayPrimFloat;
    }

    public double[][] getA1_arrayArrayPrimDouble() {
        return a1_arrayArrayPrimDouble;
    }

    public void setA1_arrayArrayPrimDouble(double[][] a1_arrayArrayPrimDouble) {
        this.a1_arrayArrayPrimDouble = a1_arrayArrayPrimDouble;
    }

    public Boolean[][] getA1_arrayArrayBoolean() {
        return a1_arrayArrayBoolean;
    }

    public void setA1_arrayArrayBoolean(Boolean[][] a1_arrayArrayBoolean) {
        this.a1_arrayArrayBoolean = a1_arrayArrayBoolean;
    }

    public Character[][] getA1_arrayArrayChar() {
        return a1_arrayArrayChar;
    }

    public void setA1_arrayArrayChar(Character[][] a1_arrayArrayChar) {
        this.a1_arrayArrayChar = a1_arrayArrayChar;
    }

    public Byte[][] getA1_arrayArrayByte() {
        return a1_arrayArrayByte;
    }

    public void setA1_arrayArrayByte(Byte[][] a1_arrayArrayByte) {
        this.a1_arrayArrayByte = a1_arrayArrayByte;
    }

    public Short[][] getA1_arrayArrayShort() {
        return a1_arrayArrayShort;
    }

    public void setA1_arrayArrayShort(Short[][] a1_arrayArrayShort) {
        this.a1_arrayArrayShort = a1_arrayArrayShort;
    }

    public Integer[][] getA1_arrayArrayInteger() {
        return a1_arrayArrayInteger;
    }

    public void setA1_arrayArrayInteger(Integer[][] a1_arrayArrayInteger) {
        this.a1_arrayArrayInteger = a1_arrayArrayInteger;
    }

    public Long[][] getA1_arrayArrayLong() {
        return a1_arrayArrayLong;
    }

    public void setA1_arrayArrayLong(Long[][] a1_arrayArrayLong) {
        this.a1_arrayArrayLong = a1_arrayArrayLong;
    }

    public Float[][] getA1_arrayArrayFloat() {
        return a1_arrayArrayFloat;
    }

    public void setA1_arrayArrayFloat(Float[][] a1_arrayArrayFloat) {
        this.a1_arrayArrayFloat = a1_arrayArrayFloat;
    }

    public Double[][] getA1_arrayArrayDouble() {
        return a1_arrayArrayDouble;
    }

    public void setA1_arrayArrayDouble(Double[][] a1_arrayArrayDouble) {
        this.a1_arrayArrayDouble = a1_arrayArrayDouble;
    }

    public String[][] getA1_arrayArrayString() {
        return a1_arrayArrayString;
    }

    public void setA1_arrayArrayString(String[][] a1_arrayArrayString) {
        this.a1_arrayArrayString = a1_arrayArrayString;
    }

    public BigInteger[][] getA1_arrayArrayBigInteger() {
        return a1_arrayArrayBigInteger;
    }

    public void setA1_arrayArrayBigInteger(BigInteger[][] a1_arrayArrayBigInteger) {
        this.a1_arrayArrayBigInteger = a1_arrayArrayBigInteger;
    }

    public BigDecimal[][] getA1_arrayArrayBigDecimal() {
        return a1_arrayArrayBigDecimal;
    }

    public void setA1_arrayArrayBigDecimal(BigDecimal[][] a1_arrayArrayBigDecimal) {
        this.a1_arrayArrayBigDecimal = a1_arrayArrayBigDecimal;
    }

    public LocalDate[][] getA1_arrayArrayLocalDate() {
        return a1_arrayArrayLocalDate;
    }

    public void setA1_arrayArrayLocalDate(LocalDate[][] a1_arrayArrayLocalDate) {
        this.a1_arrayArrayLocalDate = a1_arrayArrayLocalDate;
    }

    public LocalTime[][] getA1_arrayArrayLocalTime() {
        return a1_arrayArrayLocalTime;
    }

    public void setA1_arrayArrayLocalTime(LocalTime[][] a1_arrayArrayLocalTime) {
        this.a1_arrayArrayLocalTime = a1_arrayArrayLocalTime;
    }

    public LocalDateTime[][] getA1_arrayArrayLocalDateTime() {
        return a1_arrayArrayLocalDateTime;
    }

    public void setA1_arrayArrayLocalDateTime(LocalDateTime[][] a1_arrayArrayLocalDateTime) {
        this.a1_arrayArrayLocalDateTime = a1_arrayArrayLocalDateTime;
    }

    public ExampleEnum[][] getA1_arrayArrayEnm() {
        return a1_arrayArrayEnm;
    }

    public void setA1_arrayArrayEnm(ExampleEnum[][] a1_arrayArrayEnm) {
        this.a1_arrayArrayEnm = a1_arrayArrayEnm;
    }

    public ExampleConfigurationB1[][] getA1_arrayArrayB1() {
        return a1_arrayArrayB1;
    }

    public void setA1_arrayArrayB1(ExampleConfigurationB1[][] a1_arrayArrayB1) {
        this.a1_arrayArrayB1 = a1_arrayArrayB1;
    }

    public ExampleConfigurationB2[][] getA1_arrayArrayB2() {
        return a1_arrayArrayB2;
    }

    public void setA1_arrayArrayB2(ExampleConfigurationB2[][] a1_arrayArrayB2) {
        this.a1_arrayArrayB2 = a1_arrayArrayB2;
    }

    public Point getA1_point() {
        return a1_point;
    }

    public void setA1_point(Point a1_point) {
        this.a1_point = a1_point;
    }

    public List<Point> getA1_listPoint() {
        return a1_listPoint;
    }

    public void setA1_listPoint(List<Point> a1_listPoint) {
        this.a1_listPoint = a1_listPoint;
    }

    public Point[] getA1_arrayPoint() {
        return a1_arrayPoint;
    }

    public void setA1_arrayPoint(Point[] a1_arrayPoint) {
        this.a1_arrayPoint = a1_arrayPoint;
    }

    public Set<Point> getA1_setPoint() {
        return a1_setPoint;
    }

    public void setA1_setPoint(Set<Point> a1_setPoint) {
        this.a1_setPoint = a1_setPoint;
    }

    public Map<ExampleEnum, List<Point>> getA1_mapEnmListPoint() {
        return a1_mapEnmListPoint;
    }

    public void setA1_mapEnmListPoint(Map<ExampleEnum, List<Point>> a1_mapEnmListPoint) {
        this.a1_mapEnmListPoint = a1_mapEnmListPoint;
    }
}
