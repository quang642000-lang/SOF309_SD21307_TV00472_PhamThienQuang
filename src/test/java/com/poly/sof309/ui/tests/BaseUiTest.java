package com.poly.sof309.ui.tests;

import com.poly.sof309.ui.utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * =========================================================================
 * [Y4 - P2] BASE TEST CHO TESTNG + SELENIUM WEBDRIVER UI AUTOMATION
 * Quản lý vòng đời ChromeDriver qua DriverManager, tự động phát hiện cổng
 * Tomcat local (8080/8081) và skip an toàn qua SkipException khi server chưa bật.
 * =========================================================================
 */
public abstract class BaseUiTest {
    protected static final String ADMIN_USER = System.getProperty("test.admin.user", "admin");
    protected static final String ADMIN_PASS = System.getProperty("test.admin.pass", "12345678");
    protected static final String STAFF_USER = System.getProperty("test.staff.user", "staff");
    protected static final String STAFF_PASS = System.getProperty("test.staff.pass", "12345678");
    protected static final String CUSTOMER_USER = System.getProperty("test.customer.user", "khach_vip@gmail.com");
    protected static final String CUSTOMER_PASS = System.getProperty("test.customer.pass", "12345678");

    private static volatile boolean baseUrlResolved = false;
    private static String cachedBaseUrl;

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeSuite(alwaysRun = true)
    public static void initBaseUrl() {
        resolveBaseUrl();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpDriver() {
        baseUrl = resolveBaseUrl();
        if (baseUrl == null) {
            throw new SkipException(
                    "Bỏ qua UI Selenium Test vì máy chủ Tea POS local chưa chạy (hãy bật Tomcat hoặc truyền -Dapp.baseUrl=...).");
        }

        DriverManager.initDriver();
        driver = DriverManager.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownDriver() {
        DriverManager.quitDriver();
        driver = null;
    }

    private static synchronized String resolveBaseUrl() {
        if (baseUrlResolved) {
            return cachedBaseUrl;
        }

        String configured = System.getProperty("app.baseUrl");
        if (configured == null || configured.trim().isEmpty()) {
            configured = System.getenv("APP_BASE_URL");
        }

        if (configured != null && !configured.trim().isEmpty()) {
            String normalized = trimTrailingSlash(configured.trim());
            cachedBaseUrl = isReachable(normalized + "/login") ? normalized : null;
            baseUrlResolved = true;
            return cachedBaseUrl;
        }

        List<String> candidates = Arrays.asList(
                "http://localhost:8080/SOF309_SD21307_TV00472_PhamThienQuang_war",
                "http://localhost:8080/SOF309_SD21307_TV00472_PhamThienQuang_war_exploded",
                "http://localhost:8081/SOF309_SD21307_TV00472_PhamThienQuang_war_exploded",
                "http://localhost:8081/SOF309_SD21307_TV00472_PhamThienQuang_war",
                "http://localhost:8080/SOF309_SD21307_TV00472_PhamThienQuang",
                "http://localhost:8080"
        );

        for (String candidate : candidates) {
            if (isReachable(candidate + "/login")) {
                cachedBaseUrl = candidate;
                break;
            }
        }
        baseUrlResolved = true;
        return cachedBaseUrl;
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static boolean isReachable(String probeUrl) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) URI.create(probeUrl).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(1500);
            int code = conn.getResponseCode();
            return code >= 200 && code < 400;
        } catch (IOException e) {
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
