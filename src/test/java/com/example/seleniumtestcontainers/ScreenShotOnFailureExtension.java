package com.example.seleniumtestcontainers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.testcontainers.containers.BrowserWebDriverContainer;

public class ScreenShotOnFailureExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    if (extensionContext.getExecutionException().isPresent()) {
      final Object testInstance = extensionContext.getRequiredTestInstance();
      final Field containerField = testInstance.getClass().getDeclaredField("container");
      containerField.setAccessible(true);

      BrowserWebDriverContainer browserContainer = (BrowserWebDriverContainer) containerField.get(testInstance);
      final byte[] screenshot = browserContainer.getWebDriver().getScreenshotAs(OutputType.BYTES);

      try {
        Path path = Paths
            .get("target/selenium-screenshots")
            .resolve(String.format("%s-%s-%s.png",
                LocalDateTime.now(),
                extensionContext.getRequiredTestClass().getName(),
                extensionContext.getRequiredTestMethod().getName()));
        Files.createDirectories(path.getParent());
        Files.write(path, screenshot);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
