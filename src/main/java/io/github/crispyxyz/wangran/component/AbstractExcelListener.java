package io.github.crispyxyz.wangran.component;

import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractExcelListener<D> implements ReadListener<D> {
    private static final int BATCH_SIZE = 50;
    private final List<D> cache = new ArrayList<>();
    private final Consumer<List<D>> batchProcessor;

    public AbstractExcelListener(Consumer<List<D>> batchProcessor) {
        this.batchProcessor = batchProcessor;
    }

    @Override
    public void invoke(D data, AnalysisContext context) {
        cache.add(data);
        if (cache.size() >= BATCH_SIZE) {
            saveData();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }

    private void saveData() {
        if (!cache.isEmpty()) {
            batchProcessor.accept(new ArrayList<>(cache));
            cache.clear();
        }
    }
}