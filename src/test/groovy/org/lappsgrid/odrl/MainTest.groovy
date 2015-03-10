package org.lappsgrid.odrl

import org.apache.jena.riot.Lang
import org.junit.*
import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
class MainTest {

    Main app

    @Before
    void setup() {
        app = new Main(Lang.TTL)
        app.parentDir = new File('src/test/resources')
    }

    @After
    void cleanup() {
        app = null
    }

    @Test
    void example1() {
        String odrl = load("/example1.odrl")
        app.run(odrl, [:])
    }

    @Test
    void example2() {
        String odrl = load("/example2.odrl")
        app.run(odrl, [:])
    }

    @Test
    void example3() {
        String odrl = load("/example3.odrl")
        app.run(odrl, [:])
    }

    @Test
    void example4() {
        String odrl = load("/example4.odrl")
        app.run(odrl, [:])
    }

    @Ignore
    void example4_1() {
        String odrl = load("/example4.1.odrl")
        app.run(odrl, [:])
    }

    @Ignore
    void example5() {
        String odrl = load("/example5.odrl")
        app.run(odrl, [:])
    }

    @Ignore
    void example6() {
        String odrl = load("/example6.odrl")
        app.run(odrl, [:])
    }

    String load(String filename) {
        InputStream stream = this.class.getResourceAsStream(filename)
        return stream.text
    }
}
