package de.exlll.configlib.configurations;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ExampleRecord1(
        int i,
        Double d,
        ExampleEnum enm,
        List<UUID> listUuid,
        float[][] arrayArrayFloat,
        ExampleConfigurationB1 b1
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExampleRecord1 that = (ExampleRecord1) o;
        return i == that.i &&
               Objects.equals(d, that.d) &&
               enm == that.enm &&
               Objects.equals(listUuid, that.listUuid) &&
               Arrays.deepEquals(arrayArrayFloat, that.arrayArrayFloat) &&
               Objects.equals(b1, that.b1);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(i, d, enm, listUuid, b1);
        result = 31 * result + Arrays.deepHashCode(arrayArrayFloat);
        return result;
    }
}
