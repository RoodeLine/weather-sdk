package com.example.weather;

/**
 * Получение погоды в Казани
 */
public class KazanWeatherTest {
    public static void main(String[] args) {
        String apiKey = "d4a1884fcaf212591a51926e31ab6efe";

        try {
            System.out.println("🌤️ Получаем погоду в Казани...\n");

            WeatherSDK sdk = WeatherSDK.getInstance("kazan-test", apiKey, WeatherSDK.Mode.ON_DEMAND);

            String kazanWeather = sdk.getWeather("Kazan");

            System.out.println("📍 Погода в Казани:");
            System.out.println(kazanWeather);

            WeatherSDK.deleteInstance("kazan-test");

        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
        }
    }
}
