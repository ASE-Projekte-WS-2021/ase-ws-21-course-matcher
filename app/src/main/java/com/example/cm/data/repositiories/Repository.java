package com.example.cm.data.repositiories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Repository {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
}
