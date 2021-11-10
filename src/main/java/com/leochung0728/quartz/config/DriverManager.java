package com.leochung0728.quartz.config;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.github.bonigarcia.wdm.WebDriverManager;

@Component
public class DriverManager {

	@Value("${selenium.browser:chrome}")
	private String browser;

	@Bean
	public WebDriver getDriver() {
		WebDriver driver;
		switch (browser.toLowerCase()) {
			case "chrome":
				driver = getChromeDriver();
				break;
	
			case "edge":
				driver = getEdgeDriver();
				break;
	
			default:
				driver = getChromeDriver();
				break;
		}
		return driver;
	}

	@Bean
	@ConditionalOnProperty(name = "selenium.browser", havingValue = "Chrome")
	public WebDriver getChromeDriver() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
        options.addArguments("test-type");
        options.addArguments("--start-maximized");
        options.addArguments("disable-extensions");
        options.addArguments("--disable-infobars");
        options.setExperimentalOption("useAutomationExtension", false);
        
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "Nexus 5");
        options.setExperimentalOption("mobileEmulation", mobileEmulation);
		
		return new ChromeDriver();
	}

	@Bean
	@ConditionalOnProperty(name = "selenium.browser", havingValue = "Edge")
	public WebDriver getEdgeDriver() {
		WebDriverManager.firefoxdriver().setup();
		return new FirefoxDriver();
	}
}
