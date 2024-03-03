package com.kachinga.hr.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Objects;

public final class CurrentUser {
    public static Long userId(ServerRequest request) {
        HttpHeaders headers = request.headers().asHttpHeaders();
        String userId = headers.getFirst("X-auth-user-id");
        if (userId != null) {
            return Long.valueOf(userId);
        } else {
            return 0L;
        }
    }

    public static Long companyId(ServerRequest request) {
        HttpHeaders headers = request.headers().asHttpHeaders();
        String companyId = headers.getFirst("X-auth-company-id");
        if (companyId != null) {
            return Long.valueOf(companyId);
        } else {
            return 0L;
        }
    }

    public static Long financialYearId(ServerRequest request) {
        HttpHeaders headers = request.headers().asHttpHeaders();
        String financialYearId = headers.getFirst("X-auth-financial-year-id");
        if (financialYearId != null) {
            return Long.valueOf(financialYearId);
        } else {
            return 0L;
        }
    }

    public static String companyNumber(ServerRequest request) {
        HttpHeaders headers = request.headers().asHttpHeaders();
        String number = headers.getFirst("X-auth-company-number");
        return Objects.requireNonNullElse(number, "");
    }

    public static String companyName(ServerRequest request) {
        HttpHeaders headers = request.headers().asHttpHeaders();
        String name = headers.getFirst("X-auth-company-name");
        return Objects.requireNonNullElse(name, "");
    }

    public static String company(ServerRequest request) {
        return companyNumber(request).concat(" - ").concat(companyName(request));
    }
}
