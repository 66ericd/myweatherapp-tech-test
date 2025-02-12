package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.lang.reflect.Field;

@Controller
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  @GetMapping("/forecast/{city}")
  public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {

    CityInfo ci = weatherService.forecastByCity(city);

    return ResponseEntity.ok(ci);
  }

  @GetMapping("/compare-daylight")
  public ResponseEntity<String> compareDaylight(@RequestParam String city1, @RequestParam String city2) {
    // Check both cities are different (Case insensitive)
    if (city1.toLowerCase().equals(city2.toLowerCase())) {
      return ResponseEntity.ok("The two cities provided must be different");
    }
    try {
      // Fetch weather data for both cities
      CityInfo cityInfo1 = weatherService.forecastByCity(city1);
      CityInfo cityInfo2 = weatherService.forecastByCity(city2);

      // Handle null weather data
      if (cityInfo1 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No weather data found for " + city1);
      }
      if (cityInfo2 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No weather data found for " + city2);
      }

      // Use reflection to access current weather condition of city 1
      Field conditionField1 = cityInfo1.getClass().getDeclaredField("currentConditions");
      conditionField1.setAccessible(true);
      Object conditions1 = conditionField1.get(cityInfo1);

      // Use reflection to access current weather condition of city 2
      Field conditionField2 = cityInfo2.getClass().getDeclaredField("currentConditions");
      conditionField2.setAccessible(true);
      Object conditions2 = conditionField2.get(cityInfo2);

      // Use reflection to access sunrise and sunset of city 1
      Field sunriseField1 = conditions1.getClass().getDeclaredField("sunrise");
      sunriseField1.setAccessible(true);
      String citySunriseText1 = (String) sunriseField1.get(conditions1);
      Field sunsetField1 = conditions1.getClass().getDeclaredField("sunset");
      sunsetField1.setAccessible(true);
      String citySunsetText1 = (String) sunsetField1.get(conditions1);

      // Use reflection to access sunrise and sunset of city 1
      Field sunriseField2 = conditions2.getClass().getDeclaredField("sunrise");
      sunriseField2.setAccessible(true);
      String citySunriseText2 = (String) sunriseField2.get(conditions2);
      Field sunsetField2 = conditions2.getClass().getDeclaredField("sunset");
      sunsetField2.setAccessible(true);
      String citySunsetText2 = (String) sunsetField2.get(conditions2);

      // Parse sunrise and sunset times for each city in LocalTime objects
      LocalTime citySunrise1 = LocalTime.parse(citySunriseText1);
      LocalTime citySunrise2 = LocalTime.parse(citySunriseText2);
      LocalTime citySunset1 = LocalTime.parse(citySunsetText1);
      LocalTime citySunset2 = LocalTime.parse(citySunsetText2);

      // Work out daylight hours for each city (Sunset - Sunrise)
      long daylightTime1 = ChronoUnit.SECONDS.between(citySunrise1, citySunset1);
      long daylightTime2 = ChronoUnit.SECONDS.between(citySunrise2, citySunset2);

      // Compare daylight hours and output result
      if (daylightTime1 > daylightTime2) {
        return ResponseEntity.ok(city1);
      } else if (daylightTime1 < daylightTime2) {
        return ResponseEntity.ok(city2);
      } else {
        return ResponseEntity.ok(city1 + ", " + city2);
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while comparing daylight hours " + e.getMessage());
    }
  }

  @GetMapping("/rain-check")
  public ResponseEntity<String> rainCheck(@RequestParam String city1, @RequestParam String city2) {
    // Check both cities are different (Case insensitive)
    if (city1.toLowerCase().equals(city2.toLowerCase())) {
      return ResponseEntity.ok("The two cities provided must be different");
    }
    try {
      // Fetch weather data for both cities
      CityInfo cityInfo1 = weatherService.forecastByCity(city1);
      CityInfo cityInfo2 = weatherService.forecastByCity(city2);

      // Handle null weather data
      if (cityInfo1 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No weather data found for " + city1);
      }
      if (cityInfo2 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No weather data found for " + city2);
      }

      // Use reflection to access current weather condition of city 1
      Field conditionField1 = cityInfo1.getClass().getDeclaredField("currentConditions");
      conditionField1.setAccessible(true);
      Object conditions1 = conditionField1.get(cityInfo1);

      // Use reflection to access current weather condition of city 2
      Field conditionField2 = cityInfo2.getClass().getDeclaredField("currentConditions");
      conditionField2.setAccessible(true);
      Object conditions2 = conditionField2.get(cityInfo2);

      // Use reflection to check for rain in city 1
      Field weatherField1 = conditions1.getClass().getDeclaredField("conditions");
      weatherField1.setAccessible(true);
      String weatherText1 = (String) weatherField1.get(conditions1);
      Boolean cityRain1 = weatherText1.toLowerCase().contains("rain");

      // Use reflection to check for rain in city 2
      Field weatherField2 = conditions2.getClass().getDeclaredField("conditions");
      weatherField2.setAccessible(true);
      String weatherText2 = (String) weatherField2.get(conditions2);
      Boolean cityRain2 = weatherText2.toLowerCase().contains("rain");

      // Return output based on which cities raining
      if (cityRain1 && cityRain2) {
        return ResponseEntity.ok(city1 + ", " + city2);
      } else if (cityRain1) {
        return ResponseEntity.ok(city1);
      } else if (cityRain2) {
        return ResponseEntity.ok(city2);
      } else {
        return ResponseEntity.ok("Neither");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while checking rain " + e.getMessage());
    }
  }
}
