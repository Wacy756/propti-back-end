package com.propti.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.propti.auth.model.StackUser;
import com.propti.config.StackAuthProperties;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
public class StackAuthClient {

    private final StackAuthProperties properties;
    private final RestClient restClient;

    public StackAuthClient(final StackAuthProperties properties, final Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    /**
     * Validates the provided Stack access token against the Stack Auth REST API.
     *
     * @return a StackUser when the token is valid; empty if the token is invalid or missing
     */
    public Optional<StackUser> fetchUser(final String accessToken, final String refreshToken) {
        if (!StringUtils.hasText(accessToken)) {
            return Optional.empty();
        }

        try {
            final ResponseEntity<StackUserResponse> response = restClient.get()
                    .uri(properties.apiUrl() + "/api/v1/users/me")
                    .headers(headers -> {
                        headers.set("X-Stack-Override-Error-Status", "true");
                        headers.set("X-Stack-Project-Id", properties.getProjectId());
                        headers.set("X-Stack-Access-Type", "server");
                        headers.set("X-Stack-Client-Version", "propti-backend");
                        headers.set("X-Stack-Access-Token", accessToken);
                        headers.set("X-Stack-Allow-Anonymous-User", "false");
                        headers.set("X-Stack-Secret-Server-Key", properties.getSecretServerKey());
                        if (StringUtils.hasText(refreshToken)) {
                            headers.set("X-Stack-Refresh-Token", refreshToken);
                        }
                    })
                    .retrieve()
                    .toEntity(StackUserResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("Stack Auth returned non-OK status: {}", response.getStatusCode());
                return Optional.empty();
            }

            final StackUserResponse body = response.getBody();
            return Optional.of(new StackUser(body.id(), body.clientMetadata(), body.serverMetadata()));
        } catch (final RestClientException ex) {
            log.warn("Failed to validate Stack token", ex);
            return Optional.empty();
        }
    }

    private record StackUserResponse(
            @JsonProperty("id") String id,
            @JsonProperty("client_metadata") Map<String, Object> clientMetadata,
            @JsonProperty("server_metadata") Map<String, Object> serverMetadata
    ) { }
}
