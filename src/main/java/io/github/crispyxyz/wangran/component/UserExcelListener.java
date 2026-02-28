package io.github.crispyxyz.wangran.component;

import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.model.excel.UserExcelData;
import io.github.crispyxyz.wangran.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserExcelListener implements ReadListener<UserExcelData> {

    private static final int CACHE_LIMIT = 50;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final List<User> cache = new ArrayList<>(CACHE_LIMIT);

    @Override
    public void invoke(UserExcelData data, AnalysisContext context) {
        User user = modelMapper.map(data, User.class);
        cache.add(user);

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
            userService.saveBatch(cache);
            cache.clear();
        }
    }
}
