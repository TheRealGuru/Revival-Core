package gg.revival.core.tools;

import gg.revival.core.Revival;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Processor {

    @Getter private Revival revival;

    public Processor(Revival revival) {
        this.revival = revival;
    }

    @Getter public ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

}
