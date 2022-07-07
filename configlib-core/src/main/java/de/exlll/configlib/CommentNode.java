package de.exlll.configlib;

import java.util.List;

/**
 * Holds the comments of a field as well as a list of field names. The list of
 * field names contains the names of all fields which led to this field from the
 * root of the configuration object. For example, for the following situation the
 * {@code CommentNode} of the field {@code fn2} would hold {@code comments} and
 * {@code fieldNames} lists that contain the values {@code ["Hello World"]} and
 * {@code ["fn0", "fn1", "fn2"]}, respectively.
 * <pre>
 * class A {
 *     B fn0 = new B();
 * }
 * class B {
 *     C fn1 = new C();
 * }
 * class C {
 *     {@code @Comment("Hello world")}
 *     int fn2;
 * }
 *  </pre>
 *
 * @param comments
 * @param fieldNames
 */
record CommentNode(List<String> comments, List<String> fieldNames) {}

