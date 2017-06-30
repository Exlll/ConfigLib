import de.exlll.configlib.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigurationTest.class,
        ConfigReadWriteTest.class,
        CommentAdderTest.class,
        CommentsTest.class,
        ConfigListTest.class,
        ConfigSetTest.class,
        ConfigMapTest.class,
        FieldFilterTest.class,
        FieldMapperTest.class,
        FilteredFieldsTest.class,
        ReflectTest.class,
        TypeConverterTest.class,
        YamlSerializerTest.class
})
public class ConfigLibTestSuite {
}
