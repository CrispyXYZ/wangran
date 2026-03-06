package io.github.crispyxyz.wangran.component;

import io.github.crispyxyz.wangran.exception.BusinessException;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.model.excel.UserExcelData;
import io.github.crispyxyz.wangran.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserExcelListener implements ReadListener<UserExcelData> {

    private static final int CACHE_LIMIT = 50;
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final List<User> cache = new ArrayList<>(CACHE_LIMIT);

    @Override
    public void invoke(UserExcelData data, AnalysisContext context) {
        Set<ConstraintViolation<UserExcelData>> violations = VALIDATOR.validate(data);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                                   .map(ConstraintViolation::getMessage)
                                   .collect(Collectors.joining("; "));
            throw new BusinessException("Excel 数据校验失败：" + msg);
        }
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
