package com.example.weather;

import com.example.weather.exception.ApiKeyException;
import com.example.weather.exception.CityNotFoundException;
import com.example.weather.exception.RateLimitException;
import com.example.weather.exception.WeatherSdkException;

/**
 * Пример использования Weather SDK
 */
public class Example {
    public static void main(String[] args) {
        // Реальный API ключ от OpenWeatherMap
        String apiKey = "d4a1884fcaf212591a51926e31ab6efe";

        System.out.println("=== Weather SDK Example ===\n");

        // Пример 1: ON_DEMAND режим
        System.out.println("1. ON_DEMAND режим:");
        try {
            WeatherSDK sdk = WeatherSDK.getInstance("main_instance", apiKey, WeatherSDK.Mode.ON_DEMAND);

            String weather = sdk.getWeather("London");
            System.out.println("Погода в London:");
            System.out.println(weather);

            // Второй запрос - будет использовать кэш
            String weatherCached = sdk.getWeather("London");
            System.out.println("\nПовторный запрос (из кэша):");
            System.out.println(weatherCached);

            WeatherSDK.deleteInstance("main_instance");
            System.out.println("\nSDK экземпляр удален");

        } catch (ApiKeyException e) {
            System.err.println("Ошибка API ключа: " + e.getMessage());
        } catch (CityNotFoundException e) {
            System.err.println("Город не найден: " + e.getMessage());
        } catch (RateLimitException e) {
            System.err.println("Превышен лимит запросов: " + e.getMessage());
        } catch (WeatherSdkException e) {
            System.err.println("Общая ошибка SDK: " + e.getMessage());
        }

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Пример 2: POLLING режим
        System.out.println("2. POLLING режим:");
        try {
            WeatherSDK pollingSdk = WeatherSDK.getInstance("polling_instance", apiKey, WeatherSDK.Mode.POLLING);

            // Добавляем несколько городов в кэш
            String moscow = pollingSdk.getWeather("Moscow");
            System.out.println("Погода в Moscow:");
            System.out.println(moscow);

            String paris = pollingSdk.getWeather("Paris");
            System.out.println("\nПогода в Paris:");
            System.out.println(paris);

            System.out.println("\nSDK в polling режиме будет автоматически обновлять кэш каждые 5 минут...");
            System.out.println("Для демонстрации ждем 3 секунды...");

            Thread.sleep(3000);

            // Запросы будут использовать кэш
            String moscowCached = pollingSdk.getWeather("Moscow");
            System.out.println("\nПовторный запрос Moscow (из кэша):");
            System.out.println(moscowCached);

            WeatherSDK.deleteInstance("polling_instance");
            System.out.println("\nPolling SDK экземпляр удален, polling остановлен");

        } catch (InterruptedException e) {
            System.err.println("Ошибка в polling режиме: " + e.getMessage());
        }

        System.out.println("\n=== Пример завершен ===");
    }
}
