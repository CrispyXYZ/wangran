package io.github.crispyxyz.wangran.service.factory;

public interface AccountFactory<T> {
    T create(String phoneNumber, byte[] passwordSha256, boolean autoApprove);
}
