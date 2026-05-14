package com.elie.hka.controller;

import com.elie.hka.entity.Hochschule;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.springframework.http.HttpStatus.CREATED;

@Tag("rest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@EnabledForJreRange(min = JRE.JAVA_21)
@ExtendWith(SoftAssertionsExtension.class)
@DisplayName("REST-Schnittstelle fuer Hochschulen testen")
class HochschuleControllerTest {

    private static final String ID_VORHANDEN =
            "6e9e2ec1-7a51-4e8f-ac70-7a855815438d";

    private static final String ID_NICHT_VORHANDEN =
            "ffffffff-ffff-ffff-ffff-ffffffffffff";

    private static final String NAME_VORHANDEN = "Karlsruhe";

    private final String baseUrl;

    private final RestClient restClient;

    @InjectSoftAssertions
    private SoftAssertions softly;

    HochschuleControllerTest(
            @LocalServerPort final int port,
            final ApplicationContext ctx
    ) {
        final var sslBundles = ctx.getBean(SslBundles.class);
        final var sslBundle = sslBundles.getBundle("microservice");
        final var sslContext = sslBundle.createSslContext();

        final var factory =
                new org.springframework.http.client.JdkClientHttpRequestFactory(
                        java.net.http.HttpClient.newBuilder()
                                .sslContext(sslContext)
                                .build()
                );

        baseUrl = "https://localhost:" + port;

        restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(baseUrl)
                .build();
    }

    @Nested
    @DisplayName("Suche anhand der ID")
    class FindById {

        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
        @ValueSource(strings = {ID_VORHANDEN})
        @DisplayName("Suche mit vorhandener ID")
        void findById(final String id) {
            // when
            final var hochschule = restClient.get()
                    .uri("/hochschulen/" + id)
                    .retrieve()
                    .body(Hochschule.class);

            // then
            assertThat(hochschule).isNotNull();
            softly.assertThat(hochschule.getId()).isNotNull();
            softly.assertThat(hochschule.getName()).isNotNull();
        }
        @ParameterizedTest(name = "[{index}] Suche mit nicht-vorhandener ID: id={0}")
        @ValueSource(strings = {ID_NICHT_VORHANDEN})
        @DisplayName("Suche mit nicht-vorhandener ID")
        void findByIdNichtVorhanden(final String id) {
            // when & then
            final var ex = catchThrowableOfType(
                    RestClientResponseException.class,
                    () -> restClient.get()
                            .uri("/hochschulen/" + id)
                            .retrieve()
                            .body(Hochschule.class)
            );

            assertThat(ex).isNotNull();
            assertThat(ex.getStatusCode().value()).isEqualTo(404);
        }
    }

    @Nested
    @DisplayName("Suche mit Suchparametern")
    class FindByName {

        @ParameterizedTest(name = "[{index}] Suche mit Name: name={0}")
        @ValueSource(strings = {NAME_VORHANDEN})
        @DisplayName("Suche mit vorhandenem Namen")
        void findByName(final String name) {
            // when
            final var hochschulen = restClient.get()
                    .uri("/hochschulen/search?name=" + name)
                    .retrieve()
                    .body(List.class);

            // then
            softly.assertThat(hochschulen).isNotNull();
            softly.assertThat(hochschulen).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Neuanlegen")
    class Create {

        @ParameterizedTest(name = "[{index}] Neu: name={0}")
        @ValueSource(strings = {"Neue Hochschule Test"})
        @DisplayName("Neue Hochschule anlegen")
        void post(final String name) {
            // given
            final var dto = new HochschuleDTO(
                    name,
                    "Teststrasse 1",
                    "Karlsruhe",
                    List.of("Informatik")
            );

            // when
            final var response = restClient.post()
                    .uri("/hochschulen")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();

            // then
            softly.assertThat(response.getStatusCode()).isEqualTo(CREATED);
            final var location = response.getHeaders().getLocation();
            assertThat(location).isNotNull().isInstanceOf(URI.class);
        }
    }
}