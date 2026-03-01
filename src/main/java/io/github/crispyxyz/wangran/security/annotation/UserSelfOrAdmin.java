package io.github.crispyxyz.wangran.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') or (#id == authentication.principal.id and 'user' == authentication.principal.type)")
public @interface UserSelfOrAdmin {
}
