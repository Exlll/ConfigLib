import de.exlll.configlib.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigurationTest.class,
        ConfigReadWriteTest.class,
        CommentAdderTest.class,
        CommentsTest.class,
        FieldFilterTest.class,
        FieldMapperTest.class,
        FilteredFieldsTest.class,
        YamlSerializerTest.class
})
public class ConfigLibTestSuite {
}
