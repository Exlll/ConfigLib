package de.exlll.configlib.configurations;

import de.exlll.configlib.Ignore;

import java.awt.Point;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public final class ExampleConfigurationA2 extends ExampleConfigurationA1 {
    /* IGNORED FIELDS */
    private static final int a2_staticFinalInt = 1;
    private static int a2_staticInt = 2;
    private final int a2_finalInt = 3;
    private transient int a2_transientInt = 4;
    @Ignore
    private int a2_ignoredInt = 5;
    @Ignore
    private String a2_ignoredString = "ignoredString";
    @Ignore
    private List<String> a2_ignoredListString = List.of("ignored", "list", "string");

    /* PRIMITIVE TYPES */
    private boolean a2_primBool;
    private char a2_primChar;
    private byte a2_primByte;
    private short a2_primShort;
    private int a2_primInt;
    private long a2_primLong;
    private float a2_primFloat;
    private double a2_primDouble;

    /* WRAPPER TYPES */
    private Boolean a2_refBool;
    private Character a2_refChar;
    private Byte a2_refByte;
    private Short a2_refShort;
    private Integer a2_refInt;
    private Long a2_refLong;
    private Float a2_refFloat;
    private Double a2_refDouble;

    /* OTHER TYPES */
    private String a2_string;
    private BigInteger a2_bigInteger;
    private BigDecimal a2_bigDecimal;
    private LocalDate a2_localDate;
    private LocalTime a2_localTime;
    private LocalDateTime a2_localDateTime;
    private Instant a2_instant;
    private UUID a2_uuid;
    private File a2_file;
    private Path a2_path;
    private URL a2_url;
    private URI a2_uri;
    private ExampleEnum a2_Enm;

    /* OTHER CONFIGURATIONS */
    private ExampleConfigurationB1 a2_b1;
    private ExampleConfigurationB2 a2_b2;
    private ExampleRecord1 a2_r1;
    private ExampleRecord2 a2_r2;

    /* COLLECTIONS: Lists */
    private List<Boolean> a2_listBoolean;
    private List<Character> a2_listChar;
    private List<Byte> a2_listByte;
    private List<Short> a2_listShort;
    private List<Integer> a2_listInteger;
    private List<Long> a2_listLong;
    private List<Float> a2_listFloat;
    private List<Double> a2_listDouble;
    private List<String> a2_listString;
    private List<BigInteger> a2_listBigInteger;
    private List<BigDecimal> a2_listBigDecimal;
    private List<LocalDate> a2_listLocalDate;
    private List<LocalTime> a2_listLocalTime;
    private List<LocalDateTime> a2_listLocalDateTime;
    private List<Instant> a2_listInstant;
    private List<UUID> a2_listUuid;
    private List<File> a2_listFile;
    private List<Path> a2_listPath;
    private List<URL> a2_listUrl;
    private List<URI> a2_listUri;
    private List<ExampleEnum> a2_listEnm;
    private List<ExampleConfigurationB1> a2_listB1;
    private List<ExampleConfigurationB2> a2_listB2;
    private List<ExampleRecord1> a2_listR1;
    private List<ExampleRecord2> a2_listR2;

    /* COLLECTIONS: Arrays */
    private boolean[] a2_arrayPrimBoolean;
    private char[] a2_arrayPrimChar;
    private byte[] a2_arrayPrimByte;
    private short[] a2_arrayPrimShort;
    private int[] a2_arrayPrimInteger;
    private long[] a2_arrayPrimLong;
    private float[] a2_arrayPrimFloat;
    private double[] a2_arrayPrimDouble;
    private Boolean[] a2_arrayBoolean;
    private Character[] a2_arrayChar;
    private Byte[] a2_arrayByte;
    private Short[] a2_arrayShort;
    private Integer[] a2_arrayInteger;
    private Long[] a2_arrayLong;
    private Float[] a2_arrayFloat;
    private Double[] a2_arrayDouble;
    private String[] a2_arrayString;
    private BigInteger[] a2_arrayBigInteger;
    private BigDecimal[] a2_arrayBigDecimal;
    private LocalDate[] a2_arrayLocalDate;
    private LocalTime[] a2_arrayLocalTime;
    private LocalDateTime[] a2_arrayLocalDateTime;
    private UUID[] a2_arrayUuid;
    private ExampleEnum[] a2_arrayEnm;
    private ExampleConfigurationB1[] a2_arrayB1;
    private ExampleConfigurationB2[] a2_arrayB2;
    private ExampleRecord1[] a2_arrayR1;
    private ExampleRecord2[] a2_arrayR2;

    /* COLLECTIONS: Sets */
    private Set<Boolean> a2_setBoolean;
    private Set<Character> a2_setChar;
    private Set<Byte> a2_setByte;
    private Set<Short> a2_setShort;
    private Set<Integer> a2_setInteger;
    private Set<Long> a2_setLong;
    private Set<Float> a2_setFloat;
    private Set<Double> a2_setDouble;
    private Set<String> a2_setString;
    private Set<BigInteger> a2_setBigInteger;
    private Set<BigDecimal> a2_setBigDecimal;
    private Set<LocalDate> a2_setLocalDate;
    private Set<LocalTime> a2_setLocalTime;
    private Set<LocalDateTime> a2_setLocalDateTime;
    private Set<UUID> a2_setUuid;
    private Set<ExampleEnum> a2_setEnm;
    private Set<ExampleConfigurationB1> a2_setB1;
    private Set<ExampleConfigurationB2> a2_setB2;
    private Set<ExampleRecord1> a2_setR1;
    private Set<ExampleRecord2> a2_setR2;

    /* COLLECTIONS: Maps */
    private Map<Boolean, Boolean> a2_mapBooleanBoolean;
    private Map<Character, Character> a2_mapCharChar;
    private Map<Byte, Byte> a2_mapByteByte;
    private Map<Short, Short> a2_mapShortShort;
    private Map<Integer, Integer> a2_mapIntegerInteger;
    private Map<Long, Long> a2_mapLongLong;
    private Map<Float, Float> a2_mapFloatFloat;
    private Map<Double, Double> a2_mapDoubleDouble;
    private Map<String, String> a2_mapStringString;
    private Map<BigInteger, BigInteger> a2_mapBigIntegerBigInteger;
    private Map<BigDecimal, BigDecimal> a2_mapBigDecimalBigDecimal;
    private Map<LocalDate, LocalDate> a2_mapLocalDateLocalDate;
    private Map<LocalTime, LocalTime> a2_mapLocalTimeLocalTime;
    private Map<LocalDateTime, LocalDateTime> a2_mapLocalDateTimeLocalDateTime;
    private Map<UUID, UUID> a2_mapUuidUuid;
    private Map<ExampleEnum, ExampleEnum> a2_mapEnmEnm;

    private Map<Integer, ExampleConfigurationB1> a2_mapIntegerB1;
    private Map<ExampleEnum, ExampleConfigurationB2> a2_mapEnmB2;
    private Map<String, ExampleRecord1> a2_mapStringR1;
    private Map<String, ExampleRecord2> a2_mapStringR2;

    /* COLLECTIONS: Empty */
    private List<Boolean> a2_listEmpty;
    private Integer[] a2_arrayEmpty;
    private Set<Double> a2_setEmpty;
    private Map<ExampleEnum, ExampleConfigurationB1> a2_mapEmpty;

    /* COLLECTIONS: Nested */
    private List<List<Byte>> a2_listListByte;
    private List<Float[]> a2_listArrayFloat;
    private List<Set<String>> a2_listSetString;
    private List<Map<ExampleEnum, LocalDate>> a2_listMapEnmLocalDate;

    private Set<Set<Short>> a2_setSetShort;
    private Set<Double[]> a2_setArrayDouble;
    private Set<List<String>> a2_setListString;
    private Set<Map<ExampleEnum, LocalTime>> a2_setMapEnmLocalTime;

    private Map<Integer, Map<Long, Boolean>> a2_mapIntegerMapLongBoolean;
    private Map<String, List<ExampleConfigurationB1>> a2_mapStringListB1;
    private Map<BigInteger, BigDecimal[]> a2_mapBigIntegerArrayBigDecimal;
    private Map<ExampleEnum, Set<ExampleConfigurationB2>> a2_mapEnmSetB2;

    private Map<Integer, List<Map<Short, Set<ExampleConfigurationB2>>>>
            a2_mapIntegerListMapShortSetB2;

    private boolean[][] a2_arrayArrayPrimBoolean;
    private char[][] a2_arrayArrayPrimChar;
    private byte[][] a2_arrayArrayPrimByte;
    private short[][] a2_arrayArrayPrimShort;
    private int[][] a2_arrayArrayPrimInteger;
    private long[][] a2_arrayArrayPrimLong;
    private float[][] a2_arrayArrayPrimFloat;
    private double[][] a2_arrayArrayPrimDouble;
    private Boolean[][] a2_arrayArrayBoolean;
    private Character[][] a2_arrayArrayChar;
    private Byte[][] a2_arrayArrayByte;
    private Short[][] a2_arrayArrayShort;
    private Integer[][] a2_arrayArrayInteger;
    private Long[][] a2_arrayArrayLong;
    private Float[][] a2_arrayArrayFloat;
    private Double[][] a2_arrayArrayDouble;
    private String[][] a2_arrayArrayString;
    private BigInteger[][] a2_arrayArrayBigInteger;
    private BigDecimal[][] a2_arrayArrayBigDecimal;
    private LocalDate[][] a2_arrayArrayLocalDate;
    private LocalTime[][] a2_arrayArrayLocalTime;
    private LocalDateTime[][] a2_arrayArrayLocalDateTime;
    private UUID[][] a2_arrayArrayUuid;
    private ExampleEnum[][] a2_arrayArrayEnm;
    private ExampleConfigurationB1[][] a2_arrayArrayB1;
    private ExampleConfigurationB2[][] a2_arrayArrayB2;
    private ExampleRecord1[][] a2_arrayArrayR1;
    private ExampleRecord2[][] a2_arrayArrayR2;

    /* CUSTOM CONVERTERS */
    private Point a2_point;
    private List<Point> a2_listPoint;
    private Point[] a2_arrayPoint;
    private Set<Point> a2_setPoint;
    private Map<ExampleEnum, List<Point>> a2_mapEnmListPoint;

    /* UNSUPPORTED TYPES */
//    private Map<Point, String> unsupported;        // invalid map key
//    private Map<List<String>, String> unsupported; // invalid map key
//    private Box<String> unsupported;               // custom parameterized type
//    private List<? extends String> unsupported;    // wildcard type
//    private List<?> unsupported;                   // wildcard type
//    private List<?>[] unsupported;                 // wildcard type
//    private T unsupported;                         // type variable
//    private List unsupported;                      // raw type
//    private List[] unsupported;                    // raw type
//    private List<String>[] unsupported;            // generic array type
//    private Set<Integer>[] unsupported;            // generic array type
//    private Map<Byte, Byte>[] unsupported;         // generic array type

    public static int getA2_staticFinalInt() {
        return a2_staticFinalInt;
    }

    public static int getA2_staticInt() {
        return a2_staticInt;
    }

    public static void setA2_staticInt(int a2_staticInt) {
        ExampleConfigurationA2.a2_staticInt = a2_staticInt;
    }

    public int getA2_finalInt() {
        return a2_finalInt;
    }

    public int getA2_transientInt() {
        return a2_transientInt;
    }

    public void setA2_transientInt(int a2_transientInt) {
        this.a2_transientInt = a2_transientInt;
    }

    public int getA2_ignoredInt() {
        return a2_ignoredInt;
    }

    public void setA2_ignoredInt(int a2_ignoredInt) {
        this.a2_ignoredInt = a2_ignoredInt;
    }

    public String getA2_ignoredString() {
        return a2_ignoredString;
    }

    public void setA2_ignoredString(String a2_ignoredString) {
        this.a2_ignoredString = a2_ignoredString;
    }

    public List<String> getA2_ignoredListString() {
        return a2_ignoredListString;
    }

    public void setA2_ignoredListString(List<String> a2_ignoredListString) {
        this.a2_ignoredListString = a2_ignoredListString;
    }

    public boolean isA2_primBool() {
        return a2_primBool;
    }

    public void setA2_primBool(boolean a2_primBool) {
        this.a2_primBool = a2_primBool;
    }

    public char getA2_primChar() {
        return a2_primChar;
    }

    public void setA2_primChar(char a2_primChar) {
        this.a2_primChar = a2_primChar;
    }

    public byte getA2_primByte() {
        return a2_primByte;
    }

    public void setA2_primByte(byte a2_primByte) {
        this.a2_primByte = a2_primByte;
    }

    public short getA2_primShort() {
        return a2_primShort;
    }

    public void setA2_primShort(short a2_primShort) {
        this.a2_primShort = a2_primShort;
    }

    public int getA2_primInt() {
        return a2_primInt;
    }

    public void setA2_primInt(int a2_primInt) {
        this.a2_primInt = a2_primInt;
    }

    public long getA2_primLong() {
        return a2_primLong;
    }

    public void setA2_primLong(long a2_primLong) {
        this.a2_primLong = a2_primLong;
    }

    public float getA2_primFloat() {
        return a2_primFloat;
    }

    public void setA2_primFloat(float a2_primFloat) {
        this.a2_primFloat = a2_primFloat;
    }

    public double getA2_primDouble() {
        return a2_primDouble;
    }

    public void setA2_primDouble(double a2_primDouble) {
        this.a2_primDouble = a2_primDouble;
    }

    public Boolean getA2_refBool() {
        return a2_refBool;
    }

    public void setA2_refBool(Boolean a2_refBool) {
        this.a2_refBool = a2_refBool;
    }

    public Character getA2_refChar() {
        return a2_refChar;
    }

    public void setA2_refChar(Character a2_refChar) {
        this.a2_refChar = a2_refChar;
    }

    public Byte getA2_refByte() {
        return a2_refByte;
    }

    public void setA2_refByte(Byte a2_refByte) {
        this.a2_refByte = a2_refByte;
    }

    public Short getA2_refShort() {
        return a2_refShort;
    }

    public void setA2_refShort(Short a2_refShort) {
        this.a2_refShort = a2_refShort;
    }

    public Integer getA2_refInt() {
        return a2_refInt;
    }

    public void setA2_refInt(Integer a2_refInt) {
        this.a2_refInt = a2_refInt;
    }

    public Long getA2_refLong() {
        return a2_refLong;
    }

    public void setA2_refLong(Long a2_refLong) {
        this.a2_refLong = a2_refLong;
    }

    public Float getA2_refFloat() {
        return a2_refFloat;
    }

    public void setA2_refFloat(Float a2_refFloat) {
        this.a2_refFloat = a2_refFloat;
    }

    public Double getA2_refDouble() {
        return a2_refDouble;
    }

    public void setA2_refDouble(Double a2_refDouble) {
        this.a2_refDouble = a2_refDouble;
    }

    public String getA2_string() {
        return a2_string;
    }

    public void setA2_string(String a2_string) {
        this.a2_string = a2_string;
    }

    public BigInteger getA2_bigInteger() {
        return a2_bigInteger;
    }

    public void setA2_bigInteger(BigInteger a2_bigInteger) {
        this.a2_bigInteger = a2_bigInteger;
    }

    public BigDecimal getA2_bigDecimal() {
        return a2_bigDecimal;
    }

    public void setA2_bigDecimal(BigDecimal a2_bigDecimal) {
        this.a2_bigDecimal = a2_bigDecimal;
    }

    public LocalDate getA2_localDate() {
        return a2_localDate;
    }

    public void setA2_localDate(LocalDate a2_localDate) {
        this.a2_localDate = a2_localDate;
    }

    public LocalTime getA2_localTime() {
        return a2_localTime;
    }

    public void setA2_localTime(LocalTime a2_localTime) {
        this.a2_localTime = a2_localTime;
    }

    public LocalDateTime getA2_localDateTime() {
        return a2_localDateTime;
    }

    public void setA2_localDateTime(LocalDateTime a2_localDateTime) {
        this.a2_localDateTime = a2_localDateTime;
    }

    public ExampleEnum getA2_Enm() {
        return a2_Enm;
    }

    public void setA2_Enm(ExampleEnum a2_Enm) {
        this.a2_Enm = a2_Enm;
    }

    public ExampleConfigurationB1 getA2_b1() {
        return a2_b1;
    }

    public void setA2_b1(ExampleConfigurationB1 a2_b1) {
        this.a2_b1 = a2_b1;
    }

    public ExampleConfigurationB2 getA2_b2() {
        return a2_b2;
    }

    public void setA2_b2(ExampleConfigurationB2 a2_b2) {
        this.a2_b2 = a2_b2;
    }

    public List<Boolean> getA2_listBoolean() {
        return a2_listBoolean;
    }

    public void setA2_listBoolean(List<Boolean> a2_listBoolean) {
        this.a2_listBoolean = a2_listBoolean;
    }

    public List<Character> getA2_listChar() {
        return a2_listChar;
    }

    public void setA2_listChar(List<Character> a2_listChar) {
        this.a2_listChar = a2_listChar;
    }

    public List<Byte> getA2_listByte() {
        return a2_listByte;
    }

    public void setA2_listByte(List<Byte> a2_listByte) {
        this.a2_listByte = a2_listByte;
    }

    public List<Short> getA2_listShort() {
        return a2_listShort;
    }

    public void setA2_listShort(List<Short> a2_listShort) {
        this.a2_listShort = a2_listShort;
    }

    public List<Integer> getA2_listInteger() {
        return a2_listInteger;
    }

    public void setA2_listInteger(List<Integer> a2_listInteger) {
        this.a2_listInteger = a2_listInteger;
    }

    public List<Long> getA2_listLong() {
        return a2_listLong;
    }

    public void setA2_listLong(List<Long> a2_listLong) {
        this.a2_listLong = a2_listLong;
    }

    public List<Float> getA2_listFloat() {
        return a2_listFloat;
    }

    public void setA2_listFloat(List<Float> a2_listFloat) {
        this.a2_listFloat = a2_listFloat;
    }

    public List<Double> getA2_listDouble() {
        return a2_listDouble;
    }

    public void setA2_listDouble(List<Double> a2_listDouble) {
        this.a2_listDouble = a2_listDouble;
    }

    public List<String> getA2_listString() {
        return a2_listString;
    }

    public void setA2_listString(List<String> a2_listString) {
        this.a2_listString = a2_listString;
    }

    public List<BigInteger> getA2_listBigInteger() {
        return a2_listBigInteger;
    }

    public void setA2_listBigInteger(List<BigInteger> a2_listBigInteger) {
        this.a2_listBigInteger = a2_listBigInteger;
    }

    public List<BigDecimal> getA2_listBigDecimal() {
        return a2_listBigDecimal;
    }

    public void setA2_listBigDecimal(List<BigDecimal> a2_listBigDecimal) {
        this.a2_listBigDecimal = a2_listBigDecimal;
    }

    public List<LocalDate> getA2_listLocalDate() {
        return a2_listLocalDate;
    }

    public void setA2_listLocalDate(List<LocalDate> a2_listLocalDate) {
        this.a2_listLocalDate = a2_listLocalDate;
    }

    public List<LocalTime> getA2_listLocalTime() {
        return a2_listLocalTime;
    }

    public void setA2_listLocalTime(List<LocalTime> a2_listLocalTime) {
        this.a2_listLocalTime = a2_listLocalTime;
    }

    public List<LocalDateTime> getA2_listLocalDateTime() {
        return a2_listLocalDateTime;
    }

    public void setA2_listLocalDateTime(List<LocalDateTime> a2_listLocalDateTime) {
        this.a2_listLocalDateTime = a2_listLocalDateTime;
    }

    public List<ExampleEnum> getA2_listEnm() {
        return a2_listEnm;
    }

    public void setA2_listEnm(List<ExampleEnum> a2_listEnm) {
        this.a2_listEnm = a2_listEnm;
    }

    public List<ExampleConfigurationB1> getA2_listB1() {
        return a2_listB1;
    }

    public void setA2_listB1(List<ExampleConfigurationB1> a2_listB1) {
        this.a2_listB1 = a2_listB1;
    }

    public List<ExampleConfigurationB2> getA2_listB2() {
        return a2_listB2;
    }

    public void setA2_listB2(List<ExampleConfigurationB2> a2_listB2) {
        this.a2_listB2 = a2_listB2;
    }

    public boolean[] getA2_arrayPrimBoolean() {
        return a2_arrayPrimBoolean;
    }

    public void setA2_arrayPrimBoolean(boolean[] a2_arrayPrimBoolean) {
        this.a2_arrayPrimBoolean = a2_arrayPrimBoolean;
    }

    public char[] getA2_arrayPrimChar() {
        return a2_arrayPrimChar;
    }

    public void setA2_arrayPrimChar(char[] a2_arrayPrimChar) {
        this.a2_arrayPrimChar = a2_arrayPrimChar;
    }

    public byte[] getA2_arrayPrimByte() {
        return a2_arrayPrimByte;
    }

    public void setA2_arrayPrimByte(byte[] a2_arrayPrimByte) {
        this.a2_arrayPrimByte = a2_arrayPrimByte;
    }

    public short[] getA2_arrayPrimShort() {
        return a2_arrayPrimShort;
    }

    public void setA2_arrayPrimShort(short[] a2_arrayPrimShort) {
        this.a2_arrayPrimShort = a2_arrayPrimShort;
    }

    public int[] getA2_arrayPrimInteger() {
        return a2_arrayPrimInteger;
    }

    public void setA2_arrayPrimInteger(int[] a2_arrayPrimInteger) {
        this.a2_arrayPrimInteger = a2_arrayPrimInteger;
    }

    public long[] getA2_arrayPrimLong() {
        return a2_arrayPrimLong;
    }

    public void setA2_arrayPrimLong(long[] a2_arrayPrimLong) {
        this.a2_arrayPrimLong = a2_arrayPrimLong;
    }

    public float[] getA2_arrayPrimFloat() {
        return a2_arrayPrimFloat;
    }

    public void setA2_arrayPrimFloat(float[] a2_arrayPrimFloat) {
        this.a2_arrayPrimFloat = a2_arrayPrimFloat;
    }

    public double[] getA2_arrayPrimDouble() {
        return a2_arrayPrimDouble;
    }

    public void setA2_arrayPrimDouble(double[] a2_arrayPrimDouble) {
        this.a2_arrayPrimDouble = a2_arrayPrimDouble;
    }

    public Boolean[] getA2_arrayBoolean() {
        return a2_arrayBoolean;
    }

    public void setA2_arrayBoolean(Boolean[] a2_arrayBoolean) {
        this.a2_arrayBoolean = a2_arrayBoolean;
    }

    public Character[] getA2_arrayChar() {
        return a2_arrayChar;
    }

    public void setA2_arrayChar(Character[] a2_arrayChar) {
        this.a2_arrayChar = a2_arrayChar;
    }

    public Byte[] getA2_arrayByte() {
        return a2_arrayByte;
    }

    public void setA2_arrayByte(Byte[] a2_arrayByte) {
        this.a2_arrayByte = a2_arrayByte;
    }

    public Short[] getA2_arrayShort() {
        return a2_arrayShort;
    }

    public void setA2_arrayShort(Short[] a2_arrayShort) {
        this.a2_arrayShort = a2_arrayShort;
    }

    public Integer[] getA2_arrayInteger() {
        return a2_arrayInteger;
    }

    public void setA2_arrayInteger(Integer[] a2_arrayInteger) {
        this.a2_arrayInteger = a2_arrayInteger;
    }

    public Long[] getA2_arrayLong() {
        return a2_arrayLong;
    }

    public void setA2_arrayLong(Long[] a2_arrayLong) {
        this.a2_arrayLong = a2_arrayLong;
    }

    public Float[] getA2_arrayFloat() {
        return a2_arrayFloat;
    }

    public void setA2_arrayFloat(Float[] a2_arrayFloat) {
        this.a2_arrayFloat = a2_arrayFloat;
    }

    public Double[] getA2_arrayDouble() {
        return a2_arrayDouble;
    }

    public void setA2_arrayDouble(Double[] a2_arrayDouble) {
        this.a2_arrayDouble = a2_arrayDouble;
    }

    public String[] getA2_arrayString() {
        return a2_arrayString;
    }

    public void setA2_arrayString(String[] a2_arrayString) {
        this.a2_arrayString = a2_arrayString;
    }

    public BigInteger[] getA2_arrayBigInteger() {
        return a2_arrayBigInteger;
    }

    public void setA2_arrayBigInteger(BigInteger[] a2_arrayBigInteger) {
        this.a2_arrayBigInteger = a2_arrayBigInteger;
    }

    public BigDecimal[] getA2_arrayBigDecimal() {
        return a2_arrayBigDecimal;
    }

    public void setA2_arrayBigDecimal(BigDecimal[] a2_arrayBigDecimal) {
        this.a2_arrayBigDecimal = a2_arrayBigDecimal;
    }

    public LocalDate[] getA2_arrayLocalDate() {
        return a2_arrayLocalDate;
    }

    public void setA2_arrayLocalDate(LocalDate[] a2_arrayLocalDate) {
        this.a2_arrayLocalDate = a2_arrayLocalDate;
    }

    public LocalTime[] getA2_arrayLocalTime() {
        return a2_arrayLocalTime;
    }

    public void setA2_arrayLocalTime(LocalTime[] a2_arrayLocalTime) {
        this.a2_arrayLocalTime = a2_arrayLocalTime;
    }

    public LocalDateTime[] getA2_arrayLocalDateTime() {
        return a2_arrayLocalDateTime;
    }

    public void setA2_arrayLocalDateTime(LocalDateTime[] a2_arrayLocalDateTime) {
        this.a2_arrayLocalDateTime = a2_arrayLocalDateTime;
    }

    public ExampleEnum[] getA2_arrayEnm() {
        return a2_arrayEnm;
    }

    public void setA2_arrayEnm(ExampleEnum[] a2_arrayEnm) {
        this.a2_arrayEnm = a2_arrayEnm;
    }

    public ExampleConfigurationB1[] getA2_arrayB1() {
        return a2_arrayB1;
    }

    public void setA2_arrayB1(ExampleConfigurationB1[] a2_arrayB1) {
        this.a2_arrayB1 = a2_arrayB1;
    }

    public ExampleConfigurationB2[] getA2_arrayB2() {
        return a2_arrayB2;
    }

    public void setA2_arrayB2(ExampleConfigurationB2[] a2_arrayB2) {
        this.a2_arrayB2 = a2_arrayB2;
    }

    public Set<Boolean> getA2_setBoolean() {
        return a2_setBoolean;
    }

    public void setA2_setBoolean(Set<Boolean> a2_setBoolean) {
        this.a2_setBoolean = a2_setBoolean;
    }

    public Set<Character> getA2_setChar() {
        return a2_setChar;
    }

    public void setA2_setChar(Set<Character> a2_setChar) {
        this.a2_setChar = a2_setChar;
    }

    public Set<Byte> getA2_setByte() {
        return a2_setByte;
    }

    public void setA2_setByte(Set<Byte> a2_setByte) {
        this.a2_setByte = a2_setByte;
    }

    public Set<Short> getA2_setShort() {
        return a2_setShort;
    }

    public void setA2_setShort(Set<Short> a2_setShort) {
        this.a2_setShort = a2_setShort;
    }

    public Set<Integer> getA2_setInteger() {
        return a2_setInteger;
    }

    public void setA2_setInteger(Set<Integer> a2_setInteger) {
        this.a2_setInteger = a2_setInteger;
    }

    public Set<Long> getA2_setLong() {
        return a2_setLong;
    }

    public void setA2_setLong(Set<Long> a2_setLong) {
        this.a2_setLong = a2_setLong;
    }

    public Set<Float> getA2_setFloat() {
        return a2_setFloat;
    }

    public void setA2_setFloat(Set<Float> a2_setFloat) {
        this.a2_setFloat = a2_setFloat;
    }

    public Set<Double> getA2_setDouble() {
        return a2_setDouble;
    }

    public void setA2_setDouble(Set<Double> a2_setDouble) {
        this.a2_setDouble = a2_setDouble;
    }

    public Set<String> getA2_setString() {
        return a2_setString;
    }

    public void setA2_setString(Set<String> a2_setString) {
        this.a2_setString = a2_setString;
    }

    public Set<BigInteger> getA2_setBigInteger() {
        return a2_setBigInteger;
    }

    public void setA2_setBigInteger(Set<BigInteger> a2_setBigInteger) {
        this.a2_setBigInteger = a2_setBigInteger;
    }

    public Set<BigDecimal> getA2_setBigDecimal() {
        return a2_setBigDecimal;
    }

    public void setA2_setBigDecimal(Set<BigDecimal> a2_setBigDecimal) {
        this.a2_setBigDecimal = a2_setBigDecimal;
    }

    public Set<LocalDate> getA2_setLocalDate() {
        return a2_setLocalDate;
    }

    public void setA2_setLocalDate(Set<LocalDate> a2_setLocalDate) {
        this.a2_setLocalDate = a2_setLocalDate;
    }

    public Set<LocalTime> getA2_setLocalTime() {
        return a2_setLocalTime;
    }

    public void setA2_setLocalTime(Set<LocalTime> a2_setLocalTime) {
        this.a2_setLocalTime = a2_setLocalTime;
    }

    public Set<LocalDateTime> getA2_setLocalDateTime() {
        return a2_setLocalDateTime;
    }

    public void setA2_setLocalDateTime(Set<LocalDateTime> a2_setLocalDateTime) {
        this.a2_setLocalDateTime = a2_setLocalDateTime;
    }

    public Set<ExampleEnum> getA2_setEnm() {
        return a2_setEnm;
    }

    public void setA2_setEnm(Set<ExampleEnum> a2_setEnm) {
        this.a2_setEnm = a2_setEnm;
    }

    public Set<ExampleConfigurationB1> getA2_setB1() {
        return a2_setB1;
    }

    public void setA2_setB1(Set<ExampleConfigurationB1> a2_setB1) {
        this.a2_setB1 = a2_setB1;
    }

    public Set<ExampleConfigurationB2> getA2_setB2() {
        return a2_setB2;
    }

    public void setA2_setB2(Set<ExampleConfigurationB2> a2_setB2) {
        this.a2_setB2 = a2_setB2;
    }

    public Map<Boolean, Boolean> getA2_mapBooleanBoolean() {
        return a2_mapBooleanBoolean;
    }

    public void setA2_mapBooleanBoolean(Map<Boolean, Boolean> a2_mapBooleanBoolean) {
        this.a2_mapBooleanBoolean = a2_mapBooleanBoolean;
    }

    public Map<Character, Character> getA2_mapCharChar() {
        return a2_mapCharChar;
    }

    public void setA2_mapCharChar(Map<Character, Character> a2_mapCharChar) {
        this.a2_mapCharChar = a2_mapCharChar;
    }

    public Map<Byte, Byte> getA2_mapByteByte() {
        return a2_mapByteByte;
    }

    public void setA2_mapByteByte(Map<Byte, Byte> a2_mapByteByte) {
        this.a2_mapByteByte = a2_mapByteByte;
    }

    public Map<Short, Short> getA2_mapShortShort() {
        return a2_mapShortShort;
    }

    public void setA2_mapShortShort(Map<Short, Short> a2_mapShortShort) {
        this.a2_mapShortShort = a2_mapShortShort;
    }

    public Map<Integer, Integer> getA2_mapIntegerInteger() {
        return a2_mapIntegerInteger;
    }

    public void setA2_mapIntegerInteger(Map<Integer, Integer> a2_mapIntegerInteger) {
        this.a2_mapIntegerInteger = a2_mapIntegerInteger;
    }

    public Map<Long, Long> getA2_mapLongLong() {
        return a2_mapLongLong;
    }

    public void setA2_mapLongLong(Map<Long, Long> a2_mapLongLong) {
        this.a2_mapLongLong = a2_mapLongLong;
    }

    public Map<Float, Float> getA2_mapFloatFloat() {
        return a2_mapFloatFloat;
    }

    public void setA2_mapFloatFloat(Map<Float, Float> a2_mapFloatFloat) {
        this.a2_mapFloatFloat = a2_mapFloatFloat;
    }

    public Map<Double, Double> getA2_mapDoubleDouble() {
        return a2_mapDoubleDouble;
    }

    public void setA2_mapDoubleDouble(Map<Double, Double> a2_mapDoubleDouble) {
        this.a2_mapDoubleDouble = a2_mapDoubleDouble;
    }

    public Map<String, String> getA2_mapStringString() {
        return a2_mapStringString;
    }

    public void setA2_mapStringString(Map<String, String> a2_mapStringString) {
        this.a2_mapStringString = a2_mapStringString;
    }

    public Map<BigInteger, BigInteger> getA2_mapBigIntegerBigInteger() {
        return a2_mapBigIntegerBigInteger;
    }

    public void setA2_mapBigIntegerBigInteger(Map<BigInteger, BigInteger> a2_mapBigIntegerBigInteger) {
        this.a2_mapBigIntegerBigInteger = a2_mapBigIntegerBigInteger;
    }

    public Map<BigDecimal, BigDecimal> getA2_mapBigDecimalBigDecimal() {
        return a2_mapBigDecimalBigDecimal;
    }

    public void setA2_mapBigDecimalBigDecimal(Map<BigDecimal, BigDecimal> a2_mapBigDecimalBigDecimal) {
        this.a2_mapBigDecimalBigDecimal = a2_mapBigDecimalBigDecimal;
    }

    public Map<LocalDate, LocalDate> getA2_mapLocalDateLocalDate() {
        return a2_mapLocalDateLocalDate;
    }

    public void setA2_mapLocalDateLocalDate(Map<LocalDate, LocalDate> a2_mapLocalDateLocalDate) {
        this.a2_mapLocalDateLocalDate = a2_mapLocalDateLocalDate;
    }

    public Map<LocalTime, LocalTime> getA2_mapLocalTimeLocalTime() {
        return a2_mapLocalTimeLocalTime;
    }

    public void setA2_mapLocalTimeLocalTime(Map<LocalTime, LocalTime> a2_mapLocalTimeLocalTime) {
        this.a2_mapLocalTimeLocalTime = a2_mapLocalTimeLocalTime;
    }

    public Map<LocalDateTime, LocalDateTime> getA2_mapLocalDateTimeLocalDateTime() {
        return a2_mapLocalDateTimeLocalDateTime;
    }

    public void setA2_mapLocalDateTimeLocalDateTime(Map<LocalDateTime, LocalDateTime> a2_mapLocalDateTimeLocalDateTime) {
        this.a2_mapLocalDateTimeLocalDateTime = a2_mapLocalDateTimeLocalDateTime;
    }

    public Map<ExampleEnum, ExampleEnum> getA2_mapEnmEnm() {
        return a2_mapEnmEnm;
    }

    public void setA2_mapEnmEnm(Map<ExampleEnum, ExampleEnum> a2_mapEnmEnm) {
        this.a2_mapEnmEnm = a2_mapEnmEnm;
    }

    public Map<Integer, ExampleConfigurationB1> getA2_mapIntegerB1() {
        return a2_mapIntegerB1;
    }

    public void setA2_mapIntegerB1(Map<Integer, ExampleConfigurationB1> a2_mapIntegerB1) {
        this.a2_mapIntegerB1 = a2_mapIntegerB1;
    }

    public Map<ExampleEnum, ExampleConfigurationB2> getA2_mapEnmB2() {
        return a2_mapEnmB2;
    }

    public void setA2_mapEnmB2(Map<ExampleEnum, ExampleConfigurationB2> a2_mapEnmB2) {
        this.a2_mapEnmB2 = a2_mapEnmB2;
    }

    public List<Boolean> getA2_listEmpty() {
        return a2_listEmpty;
    }

    public void setA2_listEmpty(List<Boolean> a2_listEmpty) {
        this.a2_listEmpty = a2_listEmpty;
    }

    public Integer[] getA2_arrayEmpty() {
        return a2_arrayEmpty;
    }

    public void setA2_arrayEmpty(Integer[] a2_arrayEmpty) {
        this.a2_arrayEmpty = a2_arrayEmpty;
    }

    public Set<Double> getA2_setEmpty() {
        return a2_setEmpty;
    }

    public void setA2_setEmpty(Set<Double> a2_setEmpty) {
        this.a2_setEmpty = a2_setEmpty;
    }

    public Map<ExampleEnum, ExampleConfigurationB1> getA2_mapEmpty() {
        return a2_mapEmpty;
    }

    public void setA2_mapEmpty(Map<ExampleEnum, ExampleConfigurationB1> a2_mapEmpty) {
        this.a2_mapEmpty = a2_mapEmpty;
    }

    public List<List<Byte>> getA2_listListByte() {
        return a2_listListByte;
    }

    public void setA2_listListByte(List<List<Byte>> a2_listListByte) {
        this.a2_listListByte = a2_listListByte;
    }

    public List<Float[]> getA2_listArrayFloat() {
        return a2_listArrayFloat;
    }

    public void setA2_listArrayFloat(List<Float[]> a2_listArrayFloat) {
        this.a2_listArrayFloat = a2_listArrayFloat;
    }

    public List<Set<String>> getA2_listSetString() {
        return a2_listSetString;
    }

    public void setA2_listSetString(List<Set<String>> a2_listSetString) {
        this.a2_listSetString = a2_listSetString;
    }

    public List<Map<ExampleEnum, LocalDate>> getA2_listMapEnmLocalDate() {
        return a2_listMapEnmLocalDate;
    }

    public void setA2_listMapEnmLocalDate(List<Map<ExampleEnum, LocalDate>> a2_listMapEnmLocalDate) {
        this.a2_listMapEnmLocalDate = a2_listMapEnmLocalDate;
    }

    public Set<Set<Short>> getA2_setSetShort() {
        return a2_setSetShort;
    }

    public void setA2_setSetShort(Set<Set<Short>> a2_setSetShort) {
        this.a2_setSetShort = a2_setSetShort;
    }

    public Set<Double[]> getA2_setArrayDouble() {
        return a2_setArrayDouble;
    }

    public void setA2_setArrayDouble(Set<Double[]> a2_setArrayDouble) {
        this.a2_setArrayDouble = a2_setArrayDouble;
    }

    public Set<List<String>> getA2_setListString() {
        return a2_setListString;
    }

    public void setA2_setListString(Set<List<String>> a2_setListString) {
        this.a2_setListString = a2_setListString;
    }

    public Set<Map<ExampleEnum, LocalTime>> getA2_setMapEnmLocalTime() {
        return a2_setMapEnmLocalTime;
    }

    public void setA2_setMapEnmLocalTime(Set<Map<ExampleEnum, LocalTime>> a2_setMapEnmLocalTime) {
        this.a2_setMapEnmLocalTime = a2_setMapEnmLocalTime;
    }

    public Map<Integer, Map<Long, Boolean>> getA2_mapIntegerMapLongBoolean() {
        return a2_mapIntegerMapLongBoolean;
    }

    public void setA2_mapIntegerMapLongBoolean(Map<Integer, Map<Long, Boolean>> a2_mapIntegerMapLongBoolean) {
        this.a2_mapIntegerMapLongBoolean = a2_mapIntegerMapLongBoolean;
    }

    public Map<String, List<ExampleConfigurationB1>> getA2_mapStringListB1() {
        return a2_mapStringListB1;
    }

    public void setA2_mapStringListB1(Map<String, List<ExampleConfigurationB1>> a2_mapStringListB1) {
        this.a2_mapStringListB1 = a2_mapStringListB1;
    }

    public Map<BigInteger, BigDecimal[]> getA2_mapBigIntegerArrayBigDecimal() {
        return a2_mapBigIntegerArrayBigDecimal;
    }

    public void setA2_mapBigIntegerArrayBigDecimal(Map<BigInteger, BigDecimal[]> a2_mapBigIntegerArrayBigDecimal) {
        this.a2_mapBigIntegerArrayBigDecimal = a2_mapBigIntegerArrayBigDecimal;
    }

    public Map<ExampleEnum, Set<ExampleConfigurationB2>> getA2_mapEnmSetB2() {
        return a2_mapEnmSetB2;
    }

    public void setA2_mapEnmSetB2(Map<ExampleEnum, Set<ExampleConfigurationB2>> a2_mapEnmSetB2) {
        this.a2_mapEnmSetB2 = a2_mapEnmSetB2;
    }

    public Map<Integer, List<Map<Short, Set<ExampleConfigurationB2>>>> getA2_mapIntegerListMapShortSetB2() {
        return a2_mapIntegerListMapShortSetB2;
    }

    public void setA2_mapIntegerListMapShortSetB2(Map<Integer, List<Map<Short, Set<ExampleConfigurationB2>>>> a2_mapIntegerListMapShortSetB2) {
        this.a2_mapIntegerListMapShortSetB2 = a2_mapIntegerListMapShortSetB2;
    }

    public boolean[][] getA2_arrayArrayPrimBoolean() {
        return a2_arrayArrayPrimBoolean;
    }

    public void setA2_arrayArrayPrimBoolean(boolean[][] a2_arrayArrayPrimBoolean) {
        this.a2_arrayArrayPrimBoolean = a2_arrayArrayPrimBoolean;
    }

    public char[][] getA2_arrayArrayPrimChar() {
        return a2_arrayArrayPrimChar;
    }

    public void setA2_arrayArrayPrimChar(char[][] a2_arrayArrayPrimChar) {
        this.a2_arrayArrayPrimChar = a2_arrayArrayPrimChar;
    }

    public byte[][] getA2_arrayArrayPrimByte() {
        return a2_arrayArrayPrimByte;
    }

    public void setA2_arrayArrayPrimByte(byte[][] a2_arrayArrayPrimByte) {
        this.a2_arrayArrayPrimByte = a2_arrayArrayPrimByte;
    }

    public short[][] getA2_arrayArrayPrimShort() {
        return a2_arrayArrayPrimShort;
    }

    public void setA2_arrayArrayPrimShort(short[][] a2_arrayArrayPrimShort) {
        this.a2_arrayArrayPrimShort = a2_arrayArrayPrimShort;
    }

    public int[][] getA2_arrayArrayPrimInteger() {
        return a2_arrayArrayPrimInteger;
    }

    public void setA2_arrayArrayPrimInteger(int[][] a2_arrayArrayPrimInteger) {
        this.a2_arrayArrayPrimInteger = a2_arrayArrayPrimInteger;
    }

    public long[][] getA2_arrayArrayPrimLong() {
        return a2_arrayArrayPrimLong;
    }

    public void setA2_arrayArrayPrimLong(long[][] a2_arrayArrayPrimLong) {
        this.a2_arrayArrayPrimLong = a2_arrayArrayPrimLong;
    }

    public float[][] getA2_arrayArrayPrimFloat() {
        return a2_arrayArrayPrimFloat;
    }

    public void setA2_arrayArrayPrimFloat(float[][] a2_arrayArrayPrimFloat) {
        this.a2_arrayArrayPrimFloat = a2_arrayArrayPrimFloat;
    }

    public double[][] getA2_arrayArrayPrimDouble() {
        return a2_arrayArrayPrimDouble;
    }

    public void setA2_arrayArrayPrimDouble(double[][] a2_arrayArrayPrimDouble) {
        this.a2_arrayArrayPrimDouble = a2_arrayArrayPrimDouble;
    }

    public Boolean[][] getA2_arrayArrayBoolean() {
        return a2_arrayArrayBoolean;
    }

    public void setA2_arrayArrayBoolean(Boolean[][] a2_arrayArrayBoolean) {
        this.a2_arrayArrayBoolean = a2_arrayArrayBoolean;
    }

    public Character[][] getA2_arrayArrayChar() {
        return a2_arrayArrayChar;
    }

    public void setA2_arrayArrayChar(Character[][] a2_arrayArrayChar) {
        this.a2_arrayArrayChar = a2_arrayArrayChar;
    }

    public Byte[][] getA2_arrayArrayByte() {
        return a2_arrayArrayByte;
    }

    public void setA2_arrayArrayByte(Byte[][] a2_arrayArrayByte) {
        this.a2_arrayArrayByte = a2_arrayArrayByte;
    }

    public Short[][] getA2_arrayArrayShort() {
        return a2_arrayArrayShort;
    }

    public void setA2_arrayArrayShort(Short[][] a2_arrayArrayShort) {
        this.a2_arrayArrayShort = a2_arrayArrayShort;
    }

    public Integer[][] getA2_arrayArrayInteger() {
        return a2_arrayArrayInteger;
    }

    public void setA2_arrayArrayInteger(Integer[][] a2_arrayArrayInteger) {
        this.a2_arrayArrayInteger = a2_arrayArrayInteger;
    }

    public Long[][] getA2_arrayArrayLong() {
        return a2_arrayArrayLong;
    }

    public void setA2_arrayArrayLong(Long[][] a2_arrayArrayLong) {
        this.a2_arrayArrayLong = a2_arrayArrayLong;
    }

    public Float[][] getA2_arrayArrayFloat() {
        return a2_arrayArrayFloat;
    }

    public void setA2_arrayArrayFloat(Float[][] a2_arrayArrayFloat) {
        this.a2_arrayArrayFloat = a2_arrayArrayFloat;
    }

    public Double[][] getA2_arrayArrayDouble() {
        return a2_arrayArrayDouble;
    }

    public void setA2_arrayArrayDouble(Double[][] a2_arrayArrayDouble) {
        this.a2_arrayArrayDouble = a2_arrayArrayDouble;
    }

    public String[][] getA2_arrayArrayString() {
        return a2_arrayArrayString;
    }

    public void setA2_arrayArrayString(String[][] a2_arrayArrayString) {
        this.a2_arrayArrayString = a2_arrayArrayString;
    }

    public BigInteger[][] getA2_arrayArrayBigInteger() {
        return a2_arrayArrayBigInteger;
    }

    public void setA2_arrayArrayBigInteger(BigInteger[][] a2_arrayArrayBigInteger) {
        this.a2_arrayArrayBigInteger = a2_arrayArrayBigInteger;
    }

    public BigDecimal[][] getA2_arrayArrayBigDecimal() {
        return a2_arrayArrayBigDecimal;
    }

    public void setA2_arrayArrayBigDecimal(BigDecimal[][] a2_arrayArrayBigDecimal) {
        this.a2_arrayArrayBigDecimal = a2_arrayArrayBigDecimal;
    }

    public LocalDate[][] getA2_arrayArrayLocalDate() {
        return a2_arrayArrayLocalDate;
    }

    public void setA2_arrayArrayLocalDate(LocalDate[][] a2_arrayArrayLocalDate) {
        this.a2_arrayArrayLocalDate = a2_arrayArrayLocalDate;
    }

    public LocalTime[][] getA2_arrayArrayLocalTime() {
        return a2_arrayArrayLocalTime;
    }

    public void setA2_arrayArrayLocalTime(LocalTime[][] a2_arrayArrayLocalTime) {
        this.a2_arrayArrayLocalTime = a2_arrayArrayLocalTime;
    }

    public LocalDateTime[][] getA2_arrayArrayLocalDateTime() {
        return a2_arrayArrayLocalDateTime;
    }

    public void setA2_arrayArrayLocalDateTime(LocalDateTime[][] a2_arrayArrayLocalDateTime) {
        this.a2_arrayArrayLocalDateTime = a2_arrayArrayLocalDateTime;
    }

    public ExampleEnum[][] getA2_arrayArrayEnm() {
        return a2_arrayArrayEnm;
    }

    public void setA2_arrayArrayEnm(ExampleEnum[][] a2_arrayArrayEnm) {
        this.a2_arrayArrayEnm = a2_arrayArrayEnm;
    }

    public ExampleConfigurationB1[][] getA2_arrayArrayB1() {
        return a2_arrayArrayB1;
    }

    public void setA2_arrayArrayB1(ExampleConfigurationB1[][] a2_arrayArrayB1) {
        this.a2_arrayArrayB1 = a2_arrayArrayB1;
    }

    public ExampleConfigurationB2[][] getA2_arrayArrayB2() {
        return a2_arrayArrayB2;
    }

    public void setA2_arrayArrayB2(ExampleConfigurationB2[][] a2_arrayArrayB2) {
        this.a2_arrayArrayB2 = a2_arrayArrayB2;
    }

    public Point getA2_point() {
        return a2_point;
    }

    public void setA2_point(Point a2_point) {
        this.a2_point = a2_point;
    }

    public List<Point> getA2_listPoint() {
        return a2_listPoint;
    }

    public void setA2_listPoint(List<Point> a2_listPoint) {
        this.a2_listPoint = a2_listPoint;
    }

    public Point[] getA2_arrayPoint() {
        return a2_arrayPoint;
    }

    public void setA2_arrayPoint(Point[] a2_arrayPoint) {
        this.a2_arrayPoint = a2_arrayPoint;
    }

    public Set<Point> getA2_setPoint() {
        return a2_setPoint;
    }

    public void setA2_setPoint(Set<Point> a2_setPoint) {
        this.a2_setPoint = a2_setPoint;
    }

    public Map<ExampleEnum, List<Point>> getA2_mapEnmListPoint() {
        return a2_mapEnmListPoint;
    }

    public void setA2_mapEnmListPoint(Map<ExampleEnum, List<Point>> a2_mapEnmListPoint) {
        this.a2_mapEnmListPoint = a2_mapEnmListPoint;
    }

    public UUID getA2_uuid() {
        return a2_uuid;
    }

    public void setA2_uuid(UUID a2_uuid) {
        this.a2_uuid = a2_uuid;
    }

    public List<UUID> getA2_listUuid() {
        return a2_listUuid;
    }

    public void setA2_listUuid(List<UUID> a2_listUuid) {
        this.a2_listUuid = a2_listUuid;
    }

    public UUID[] getA2_arrayUuid() {
        return a2_arrayUuid;
    }

    public void setA2_arrayUuid(UUID[] a2_arrayUuid) {
        this.a2_arrayUuid = a2_arrayUuid;
    }

    public Set<UUID> getA2_setUuid() {
        return a2_setUuid;
    }

    public void setA2_setUuid(Set<UUID> a2_setUuid) {
        this.a2_setUuid = a2_setUuid;
    }

    public Map<UUID, UUID> getA2_mapUuidUuid() {
        return a2_mapUuidUuid;
    }

    public void setA2_mapUuidUuid(Map<UUID, UUID> a2_mapUuidUuid) {
        this.a2_mapUuidUuid = a2_mapUuidUuid;
    }

    public UUID[][] getA2_arrayArrayUuid() {
        return a2_arrayArrayUuid;
    }

    public void setA2_arrayArrayUuid(UUID[][] a2_arrayArrayUuid) {
        this.a2_arrayArrayUuid = a2_arrayArrayUuid;
    }

    public Instant getA2_instant() {
        return a2_instant;
    }

    public void setA2_instant(Instant a2_instant) {
        this.a2_instant = a2_instant;
    }

    public File getA2_file() {
        return a2_file;
    }

    public void setA2_file(File a2_file) {
        this.a2_file = a2_file;
    }

    public Path getA2_path() {
        return a2_path;
    }

    public void setA2_path(Path a2_path) {
        this.a2_path = a2_path;
    }

    public URL getA2_url() {
        return a2_url;
    }

    public void setA2_url(URL a2_url) {
        this.a2_url = a2_url;
    }

    public URI getA2_uri() {
        return a2_uri;
    }

    public void setA2_uri(URI a2_uri) {
        this.a2_uri = a2_uri;
    }

    public List<Instant> getA2_listInstant() {
        return a2_listInstant;
    }

    public void setA2_listInstant(List<Instant> a2_listInstant) {
        this.a2_listInstant = a2_listInstant;
    }

    public List<File> getA2_listFile() {
        return a2_listFile;
    }

    public void setA2_listFile(List<File> a2_listFile) {
        this.a2_listFile = a2_listFile;
    }

    public List<Path> getA2_listPath() {
        return a2_listPath;
    }

    public void setA2_listPath(List<Path> a2_listPath) {
        this.a2_listPath = a2_listPath;
    }

    public List<URL> getA2_listUrl() {
        return a2_listUrl;
    }

    public void setA2_listUrl(List<URL> a2_listUrl) {
        this.a2_listUrl = a2_listUrl;
    }

    public List<URI> getA2_listUri() {
        return a2_listUri;
    }

    public void setA2_listUri(List<URI> a2_listUri) {
        this.a2_listUri = a2_listUri;
    }

    public ExampleRecord1 getA2_r1() {
        return a2_r1;
    }

    public void setA2_r1(ExampleRecord1 a2_r1) {
        this.a2_r1 = a2_r1;
    }

    public ExampleRecord2 getA2_r2() {
        return a2_r2;
    }

    public void setA2_r2(ExampleRecord2 a2_r2) {
        this.a2_r2 = a2_r2;
    }

    public List<ExampleRecord1> getA2_listR1() {
        return a2_listR1;
    }

    public void setA2_listR1(List<ExampleRecord1> a2_listR1) {
        this.a2_listR1 = a2_listR1;
    }

    public List<ExampleRecord2> getA2_listR2() {
        return a2_listR2;
    }

    public void setA2_listR2(List<ExampleRecord2> a2_listR2) {
        this.a2_listR2 = a2_listR2;
    }

    public ExampleRecord1[] getA2_arrayR1() {
        return a2_arrayR1;
    }

    public void setA2_arrayR1(ExampleRecord1[] a2_arrayR1) {
        this.a2_arrayR1 = a2_arrayR1;
    }

    public ExampleRecord2[] getA2_arrayR2() {
        return a2_arrayR2;
    }

    public void setA2_arrayR2(ExampleRecord2[] a2_arrayR2) {
        this.a2_arrayR2 = a2_arrayR2;
    }

    public Set<ExampleRecord1> getA2_setR1() {
        return a2_setR1;
    }

    public void setA2_setR1(Set<ExampleRecord1> a2_setR1) {
        this.a2_setR1 = a2_setR1;
    }

    public Set<ExampleRecord2> getA2_setR2() {
        return a2_setR2;
    }

    public void setA2_setR2(Set<ExampleRecord2> a2_setR2) {
        this.a2_setR2 = a2_setR2;
    }

    public Map<String, ExampleRecord1> getA2_mapStringR1() {
        return a2_mapStringR1;
    }

    public void setA2_mapStringR1(Map<String, ExampleRecord1> a2_mapStringR1) {
        this.a2_mapStringR1 = a2_mapStringR1;
    }

    public Map<String, ExampleRecord2> getA2_mapStringR2() {
        return a2_mapStringR2;
    }

    public void setA2_mapStringR2(Map<String, ExampleRecord2> a2_mapStringR2) {
        this.a2_mapStringR2 = a2_mapStringR2;
    }

    public ExampleRecord1[][] getA2_arrayArrayR1() {
        return a2_arrayArrayR1;
    }

    public void setA2_arrayArrayR1(ExampleRecord1[][] a2_arrayArrayR1) {
        this.a2_arrayArrayR1 = a2_arrayArrayR1;
    }

    public ExampleRecord2[][] getA2_arrayArrayR2() {
        return a2_arrayArrayR2;
    }

    public void setA2_arrayArrayR2(ExampleRecord2[][] a2_arrayArrayR2) {
        this.a2_arrayArrayR2 = a2_arrayArrayR2;
    }
}
