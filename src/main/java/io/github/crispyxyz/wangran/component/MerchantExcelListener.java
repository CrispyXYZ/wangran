package io.github.crispyxyz.wangran.component;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.excel.MerchantExcelData;
import io.github.crispyxyz.wangran.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MerchantExcelListener implements ReadListener<MerchantExcelData> {

    private static final int CACHE_LIMIT = 50;
    private final ModelMapper modelMapper;
    private final MerchantService merchantService;
    private final List<Merchant> cache = new ArrayList<>(CACHE_LIMIT);

    @Override
    public void invoke(MerchantExcelData data, AnalysisContext context) {
        Merchant merchant = modelMapper.map(data, Merchant.class);
        cache.add(merchant);

        if (cache.size() >= CACHE_LIMIT) {
            saveData();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }

    public void saveData() {
        if (!cache.isEmpty()) {
            merchantService.saveBatch(cache);
            cache.clear();
        }
    }
}
