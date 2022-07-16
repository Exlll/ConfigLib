package de.exlll.configlib;

import java.util.List;

/**
 * Holds the comments of a field or record component as well as a list of element names.
 * The list of element names contains the names of all fields or record components which led
 * to the current element starting from the root of the configuration object.
 * <p>
 * For example, for the following situation, if an instance of {@code A} is our root, the
 * {@code CommentNode} of the field {@code fn2} would hold {@code comments} and
 * {@code elementNames} lists that contain the values {@code ["Hello","World"]} and
 * {@code ["fn0","fn1","fn2"]}, respectively.
 *
 * <pre>
 * class A {
 *     B fn0 = new B();
 * }
 *
 * record B(C fn1) {}
 *
 * class C {
 *     {@code @Comment({"Hello", "World"})}
 *     int fn2;
 * }
 *  </pre>
 *
 * @param comments
 * @param elementNames
 */
record CommentNode(List<String> comments, List<String> elementNames) {}

