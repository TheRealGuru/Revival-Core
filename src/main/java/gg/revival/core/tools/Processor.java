package gg.revival.core.tools;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Processor {

    @Getter static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

}
