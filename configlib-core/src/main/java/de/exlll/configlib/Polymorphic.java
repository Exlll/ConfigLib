package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type is polymorphic. Serializers for polymorphic types are not
 * selected based on the compile-time types of configuration elements, but instead are chosen at
 * runtime based on the actual types of their values. This enables adding instances of subclasses /
 * implementations of a polymorphic type to collections. The subtypes must be valid configurations.
 * <p>
 * For correct deserialization, if an instance of polymorphic type (or one of its implementations /
 * subclasses) is serialized, an additional property that holds type information is added to its
 * serialization.
 *
 * <pre>
 * {@code
 * // Example 1
 * @Polymorphic
 * @Configuration
 * static abstract class A { ... }
 *
 * static final class Impl1 extends A { ... }
 * static final class Impl2 extends A { ... }
 *
 * List<A> as = List.of(new Impl1(...), new Impl2(...), ...);
 *
 * // Example 2
 * @Polymorphic
 * interface B { ... }
 *
 * record Impl1() implements B { ... }
 *
 * @Configuration
 * static final class Impl2 implements B { ... }
 *
 * List<B> bs = List.of(new Impl1(...), new Impl2(...), ...);
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SerializeWith(serializer = PolymorphicSerializer.class)
public @interface Polymorphic {
    /**
     * The default name of the property that holds the type information.
     */
    String DEFAULT_PROPERTY = "type";

    /**
     * Returns the name of the property that holds the type information.
     * <p>
     * The property returned by this method must neither be blank nor be the
     * name of a configuration element.
     *
     * @return name of the property that holds the type information
     * @see String#isBlank()
     */
    String property() default DEFAULT_PROPERTY;
}
