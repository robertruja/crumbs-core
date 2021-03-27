package test;

import org.crumbs.core.context.ConfigLoader;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TestConfRef {

    @Test
    public void TestConfRef() {
        Map<String, String> props = ConfigLoader.loadProperties();

        Assert.assertEquals("prefix_some-test_suffix", props.get("test.ref"));
    }
}
