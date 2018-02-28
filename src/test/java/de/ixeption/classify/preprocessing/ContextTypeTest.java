package de.ixeption.classify.preprocessing;

import de.ixeption.classify.features.FeatureUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextTypeTest {
    @Test
    public void testNoCollision() {
        int[] ints = Arrays.stream(ContextType.values()).mapToInt(contextType -> FeatureUtils.hash32(contextType.name().getBytes(), contextType.name().length(), 1337)).toArray();
        assertThat((long) ints.length).isEqualTo(Arrays.stream(ints).distinct().count());
    }

}