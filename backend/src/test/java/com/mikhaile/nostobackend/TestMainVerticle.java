package com.mikhaile.nostobackend;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
    private static WebClient webClient;

    private final CurrencyRate[] fakeRates = new CurrencyRate[]{
        new CurrencyRate("USD", "EUR", BigDecimal.valueOf(0.85)),
        new CurrencyRate("USD", "GBP", BigDecimal.valueOf(0.75)),
        new CurrencyRate("EUR", "GBP", BigDecimal.valueOf(0.88)),
        new CurrencyRate("EUR", "USD", BigDecimal.valueOf(1.18)),
        new CurrencyRate("GBP", "USD", BigDecimal.valueOf(1.33))
    };

    @BeforeAll
    @DisplayName("Setup http client")
    static void setup(Vertx vertx) {
        webClient = WebClient.create(vertx, new WebClientOptions()
            .setDefaultPort(MainVerticle.getPort())
            .setDefaultHost("localhost")
        );
    }

    @BeforeEach
    @DisplayName("Setup new server application for each test")
    void deployVerticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(MainVerticle.Build(new CurrencyRateProviderMock(fakeRates)))
            .onComplete(testContext.succeeding(id -> testContext.completeNow()));
    }

    @AfterEach
    @DisplayName("Check that the verticle is still there and turn off the server")
    void lastChecks(Vertx vertx) {
        assertFalse(vertx.deploymentIDs().isEmpty());
        assertEquals(1, vertx.deploymentIDs().size());
        vertx.undeploy(vertx.deploymentIDs().iterator().next());
    }

    @Test
    void verticleDeployed(VertxTestContext testContext) throws Throwable {
        testContext.completeNow();
    }

    @Test
    void shouldConvertCurrencySuccessfully(VertxTestContext testContext) {
        webClient.get("/api/convert/USD/EUR")
            .addQueryParam("amount", "100")
            .send(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(200, response.statusCode());
                var json = response.bodyAsJsonObject();
                assertNotNull(json);
                assertEquals("USD", json.getString("baseCurrency"));
                assertEquals("EUR", json.getString("quoteCurrency"));
                assertEquals(BigDecimal.valueOf(100), new BigDecimal(json.getString("baseAmount")));
                assertEquals(BigDecimal.valueOf(85.0f), new BigDecimal(json.getString("quoteAmount")));
                testContext.completeNow();
            })));
    }

    @Test
    void shouldHandleInvalidBaseCurrency(VertxTestContext testContext) {
        webClient.get("/api/convert/XYZ/EUR")
            .addQueryParam("amount", "100")
            .send(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(400, response.statusCode());
                var json = response.bodyAsJsonObject();
                assertNotNull(json);
                assertEquals("Bad Request", json.getString("error"));
                assertEquals("EXCHANGE_RATE_NOT_FOUNT", json.getString("code"));
                testContext.completeNow();
            })));
    }

    @Test
    void shouldHandleNonExistentExchangeRate(VertxTestContext testContext) {
        webClient.get("/api/convert/USD/AUD")
            .addQueryParam("amount", "50")
            .send(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(400, response.statusCode());
                var json = response.bodyAsJsonObject();
                assertNotNull(json);
                assertEquals("Bad Request", json.getString("error"));
                assertEquals("EXCHANGE_RATE_NOT_FOUNT", json.getString("code"));
                testContext.completeNow();
            })));
    }

    @Test
    void shouldHandleMissingAmountParameter(VertxTestContext testContext) {
        webClient.get("/api/convert/USD/EUR")
            .send(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(400, response.statusCode());
                var json = response.bodyAsJsonObject();
                assertNotNull(json);
                assertEquals("Bad Request", json.getString("error"));
                testContext.completeNow();
            })));
    }

    @Test
    void shouldHandleInvalidAmountValue(VertxTestContext testContext) {
        webClient.get("/api/convert/USD/EUR")
            .addQueryParam("amount", "-100")
            .send(testContext.succeeding(response -> testContext.verify(() -> {
                assertEquals(400, response.statusCode());
                var json = response.bodyAsJsonObject();
                assertNotNull(json);
                assertEquals("Bad Request", json.getString("error"));
                testContext.completeNow();
            })));
    }
}
