package com.example.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Простой тест для проверки API ключа
 */
public class TestApi {
    public static void main(String[] args) {
        String apiKey = "ca0a06e7b794b52158a850b6df4506e3";
        String city = "London";
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
            
            System.out.println("Тестируем URL: " + url);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status: " + response.statusCode());
            System.out.println("Response body:");
            System.out.println(response.body());
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
