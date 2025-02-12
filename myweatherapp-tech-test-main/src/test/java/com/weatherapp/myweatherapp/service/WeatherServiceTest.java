package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.controller.WeatherController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeatherServiceTest {
    @Autowired
    WeatherController weatherController;

    @Test
    void DaylightTestRegular() {
        // Test the daylight function on regular input, expected answer hardcoded as true answer on day of testing on 12/02/2025
        // Phoenix, Arizona vs. Barrow, Alaska
        String actual = weatherController.compareDaylight("Barrow", "Phoenix").getBody();
        assertEquals("Phoenix", actual);
    }
    @Test
    void DaylightTestDuplicate() {
        // Test the daylight function on duplicate input, should display appropriate message.
        String actual = weatherController.compareDaylight("London", "London").getBody();
        assertEquals("The two cities provided must be different", actual);
    }

    @Test
    void DaylightTestEmpty() {
        // Test the daylight function with an empty input, should display appropriate message.
        String actual = weatherController.compareDaylight("London", "").getBody();
        assertEquals("An error occurred while comparing daylight hours, 400 : \"Bad API Request:A location must be specified\"", actual);
    }

    @Test
    void DaylightTestInvalid() {
        // Test the daylight function with an invalid input, should display appropriate message.
        String actual = weatherController.compareDaylight("London", "abc").getBody();
        assertEquals("An error occurred while comparing daylight hours, 400 : \"Bad API Request:Invalid location parameter value.\"", actual);
    }

    @Test
    void RainTestRegular() {
        // Test the rain check function on regular input, expected answer hardcoded as true answer on day of testing on 12/02/2025
        // Taipei City vs. London
        String actual = weatherController.rainCheck("Taipei City", "London").getBody();
        assertEquals("Taipei City", actual);
    }

    @Test
    void RainTestNeither() {
        // Test the rain check function where neither raining, expected answer hardcoded as true answer on day of testing on 12/02/2025
        // Taipei City vs. London
        String actual = weatherController.rainCheck("Birmingham", "London").getBody();
        assertEquals("Neither", actual);
    }
    @Test
    void RainTestDuplicate() {
        // Test the daylight function on duplicate input, should display appropriate message.
        String actual = weatherController.rainCheck("London", "London").getBody();
        assertEquals("The two cities provided must be different", actual);
    }

    @Test
    void RainTestEmpty() {
        // Test the daylight function with an empty input, should display appropriate message.
        String actual = weatherController.rainCheck("London", "").getBody();
        assertEquals("An error occurred while checking rain, 400 : \"Bad API Request:A location must be specified\"", actual);
    }

    @Test
    void RainTestInvalid() {
        // Test the daylight function with an invalid input, should display appropriate message.
        String actual = weatherController.rainCheck("London", "abc").getBody();
        assertEquals("An error occurred while checking rain, 400 : \"Bad API Request:Invalid location parameter value.\"", actual);
    }
}