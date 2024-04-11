package com.mycompany.weatherapplication;
 
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherApp {
    
    public static JSONObject getWeatherData(String locationName) throws IOException {
        JSONArray locationData = getLocationData(locationName);
        
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Localização não encontrada");
            return null;
        }
        
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");
        
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FSao_Paulo";
        
        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            
            if (conn.getResponseCode() != 200) {
                System.out.println("Erro: não foi possível conectar à API");
                return null;
            }
            
            StringBuilder resultJSON =  new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJSON.append(scanner.nextLine());
            }
            
            scanner.close();
            conn.disconnect();
            
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJSON.toString());
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);
            
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);
            
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));
            
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);
            
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);
            
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("wind_speed", windSpeed);
            
            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static JSONArray getLocationData(String locationName) throws IOException {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        
        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            
            if (conn.getResponseCode() != 200) {
                System.out.println("Erro: não foi possível conectar à API");
                return null;
            }
            
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            
            scanner.close();
            conn.disconnect();
            
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());
            return (JSONArray) resultJsonObj.get("results");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static HttpURLConnection fetchApiResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn;
    }
    
    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        
        return 0;
    }
    
    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
        return currentDateTime.format(formatter);
    }
    
    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if (weatherCode <= 3L && weatherCode > 0L) {
            weatherCondition = "Cloudy";
        } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
