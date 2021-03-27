package test;

import org.crumbs.core.context.ConfigLoader;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TestConfRef {

    @Test
    public void TestConfRef() {
        Map<String, String> props = ConfigLoader.loadProperties();

        Assert.assertEquals("some-test", props.get("test.ref"));
    }
}
