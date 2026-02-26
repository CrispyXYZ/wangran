package io.github.crispyxyz.wangran.component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ModelMapperHelper {
    private final ModelMapper modelMapper;

    public <S, D> Page<D> mapPage(IPage<S> userPage, Class<D> destinationClass) {
        List<D> destinationList = userPage.getRecords()
                                          .stream()
                                          .map(
                                          user -> modelMapper.map(user, destinationClass)
                                          )
                                          .toList();

        Page<D> destinationPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        destinationPage.setRecords(destinationList);
        return destinationPage;
    }
}
