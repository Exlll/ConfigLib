import de.exlll.configlib.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CommentsTest.class,
        ConfigurationFieldFilterTest.class,
        ConfigurationReaderTest.class,
        ConfigurationTest.class,
        ConfigurationWriterTest.class,
        FieldMapperTest.class,
        FilteredFieldStreamSupplierTest.class,
        YamlSerializerTest.class
})
public class ConfigLibTestSuite {
}
