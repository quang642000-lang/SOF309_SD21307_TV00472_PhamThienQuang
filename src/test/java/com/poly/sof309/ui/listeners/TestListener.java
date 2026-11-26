package com.poly.sof309.ui.listeners;

import com.poly.sof309.ui.utils.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * =========================================================================
 * [Y4 - P2] TESTNG LISTENER TỰ ĐỘNG CHỤP ẢNH MÀN HÌNH KHI TEST FAIL
 * Lưu ảnh PNG trực tiếp vào thư mục screenshots/ phục vụ gỡ lỗi & CI Artifacts.
 * =========================================================================
 */
public class TestListener implements ITestListener {
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[TestNG START] " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TestNG PASS] " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TestNG SKIP] " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        System.err.println("[TestNG FAIL] " + methodName);

        WebDriver driver = DriverManager.getDriver();
        if (driver instanceof TakesScreenshot) {
            try {
                Path screenshotDir = Paths.get("screenshots");
                Files.createDirectories(screenshotDir);

                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String fileName = methodName + "_" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".png";
                Path destPath = screenshotDir.resolve(fileName);

                Files.copy(src.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                System.err.println("[SCREENSHOT SAVED] " + destPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("[SCREENSHOT ERROR] Không thể lưu ảnh lỗi: " + e.getMessage());
            }
        }
    }
}
