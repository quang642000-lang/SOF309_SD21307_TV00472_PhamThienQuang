package com.poly.sof309.ui.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * =========================================================================
 * [Y4 - P2] DRIVER MANAGER (THREAD-SAFE WEBDRIVER CONTAINER)
 * Quản lý vòng đời ChromeDriver bằng ThreadLocal giúp TestListener
 * truy cập WebDriver để chụp ảnh màn hình ngay khi kịch bản thất bại.
 * =========================================================================
 */
public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }

    public static void setDriver(WebDriver driver) {
        DRIVER_THREAD_LOCAL.set(driver);
    }

    public static void initDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        boolean isHeadless = Boolean.parseBoolean(System.getenv("CI"))
                || Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        WebDriver driver = new ChromeDriver(options);
        if (!isHeadless) {
            driver.manage().window().maximize();
        }
        DRIVER_THREAD_LOCAL.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            driver.quit();
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}
