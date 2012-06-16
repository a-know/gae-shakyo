package com.a_know.shakyo.controller.cron;

import org.slim3.tester.ControllerTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class UpdateMemoCountControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.start("/cron/updateMemoCount");
        UpdateMemoCountController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));
    }
}
