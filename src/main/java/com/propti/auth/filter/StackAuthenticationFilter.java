package com.propti.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propti.auth.model.StackUser;
import com.propti.auth.model.UserPrincipal;
import com.propti.auth.service.StackAuthClient;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@Order(1)
@RequiredArgsConstructor
public class StackAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_COOKIE_NAME = "stack-access";
    private static final String REFRESH_COOKIE_PREFIX = "stack-refresh-";
    private static final String HOST_REFRESH_COOKIE_PREFIX = "__Host-stack-refresh-";

    private final StackAuthClient stackAuthClient;
    private final ObjectMapper objectMapper;

    @PostConstruct
    void init() {
        log.info("StackAuthenticationFilter initialized");
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("StackAuthenticationFilter invoked for path {}", request.getRequestURI());
        }

        final TokenPair tokens = extractTokens(request);
        if (!tokens.hasAccessToken()) {
            filterChain.doFilter(request, response);
            return;
        }

        final Optional<StackUser> user = stackAuthClient.fetchUser(tokens.accessToken(), tokens.refreshToken());
        if (user.isEmpty()) {
            clearAuthCookies(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final StackUser stackUser = user.get();
        request.setAttribute(UserPrincipal.REQUEST_ATTRIBUTE, new UserPrincipal(
                stackUser.id(),
                stackUser.role(),
                stackUser.resolvedEmail(),
                stackUser.resolvedName()
        ));
        filterChain.doFilter(request, response);
    }

    private TokenPair extractTokens(final HttpServletRequest request) {
        final TokenPair headerTokens = fromHeaders(request);
        if (headerTokens.hasAccessToken()) {
            return headerTokens;
        }

        final TokenPair cookieTokens = fromCookies(request);
        if (cookieTokens.hasAccessToken()) {
            return cookieTokens;
        }

        return TokenPair.empty();
    }

    private TokenPair fromHeaders(final HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            final String access = authHeader.substring(7).trim();
            return new TokenPair(access, headerRefreshToken(request));
        }

        final String accessHeader = request.getHeader("X-Stack-Access-Token");
        if (StringUtils.hasText(accessHeader)) {
            return new TokenPair(accessHeader.trim(), headerRefreshToken(request));
        }

        return TokenPair.empty();
    }

    private TokenPair fromCookies(final HttpServletRequest request) {
        final Map<String, String> cookieMap = Optional.ofNullable(request.getCookies())
                .map(cookies -> Arrays.stream(cookies).collect(Collectors.toMap(Cookie::getName, Cookie::getValue)))
                .orElse(Collections.emptyMap());

        final TokenPair fromAccessCookie = parseAccessCookie(cookieMap);
        if (fromAccessCookie.hasAccessToken()) {
            return fromAccessCookie;
        }

        return TokenPair.empty();
    }

    private TokenPair parseAccessCookie(final Map<String, String> cookieMap) {
        final String raw = cookieMap.get(ACCESS_COOKIE_NAME);
        if (!StringUtils.hasText(raw)) {
            return TokenPair.empty();
        }

        try {
            final String decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8);
            final String[] payload = objectMapper.readValue(decoded, String[].class);
            if (payload.length >= 2 && StringUtils.hasText(payload[1])) {
                final String refresh = StringUtils.hasText(payload[0]) ? payload[0] : extractRefreshCookie(cookieMap);
                return new TokenPair(payload[1], refresh);
            }
        } catch (final Exception ex) {
            log.debug("Unable to parse stack-access cookie", ex);
        }

        return TokenPair.empty();
    }

    private String headerRefreshToken(final HttpServletRequest request) {
        final String refresh = request.getHeader("X-Stack-Refresh-Token");
        return StringUtils.hasText(refresh) ? refresh.trim() : null;
    }

    private String extractRefreshCookie(final Map<String, String> cookieMap) {
        for (final Map.Entry<String, String> entry : cookieMap.entrySet()) {
            final String name = entry.getKey();
            if (name.startsWith(REFRESH_COOKIE_PREFIX) || name.startsWith(HOST_REFRESH_COOKIE_PREFIX)) {
                final String decoded = URLDecoder.decode(entry.getValue(), StandardCharsets.UTF_8);
                if (!StringUtils.hasText(decoded)) {
                    continue;
                }
                if (decoded.trim().startsWith("{")) {
                    try {
                        final Map<?, ?> payload = objectMapper.readValue(decoded, Map.class);
                        final Object refreshToken = payload.get("refresh_token");
                        if (refreshToken instanceof String rt && StringUtils.hasText(rt)) {
                            return rt;
                        }
                    } catch (final Exception ex) {
                        log.debug("Unable to parse Stack refresh cookie payload", ex);
                    }
                } else {
                    return decoded;
                }
            }
        }
        return null;
    }

    private void clearAuthCookies(final HttpServletRequest request, final HttpServletResponse response) {
        Optional.ofNullable(request.getCookies())
                .ifPresent(cookies -> Arrays.stream(cookies)
                        .map(Cookie::getName)
                        .filter(name -> ACCESS_COOKIE_NAME.equals(name)
                                || name.startsWith(REFRESH_COOKIE_PREFIX)
                                || name.startsWith(HOST_REFRESH_COOKIE_PREFIX))
                        .forEach(name -> {
                            final Cookie cookie = new Cookie(name, "");
                            cookie.setPath("/");
                            cookie.setHttpOnly(true);
                            cookie.setSecure(true);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                }));
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String path = request.getRequestURI();
        return path == null || !path.startsWith("/api/");
    }

    private record TokenPair(String accessToken, String refreshToken) {
        static TokenPair empty() {
            return new TokenPair(null, null);
        }

        boolean hasAccessToken() {
            return StringUtils.hasText(accessToken);
        }
    }
}
