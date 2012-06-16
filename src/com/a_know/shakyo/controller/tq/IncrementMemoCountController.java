package com.a_know.shakyo.controller.tq;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.a_know.shakyo.service.MinutesService;

public class IncrementMemoCountController extends Controller {

    @Override
    public Navigation run() throws Exception {
        MinutesService.incrementMemoCount(asKey("minutesKey"));
        return null;
    }
}
