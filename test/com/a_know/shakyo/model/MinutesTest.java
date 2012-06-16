package com.a_know.shakyo.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class MinutesTest extends AppEngineTestCase {

    private Minutes model = new Minutes();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
