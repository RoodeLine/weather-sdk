package com.example.weather;

/**
 * Простой тест для проверки polling режима
 */
public class SimpleTest {
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        String apiKey = "d4a1884fcaf212591a51926e31ab6efe";

        try {
            System.out.println("Тестируем POLLING режим...");
            WeatherSDK pollingSdk = WeatherSDK.getInstance("test_polling", apiKey, WeatherSDK.Mode.POLLING);

            String moscow = pollingSdk.getWeather("Moscow");
            System.out.println("Moscow weather:");
            System.out.println(moscow);

            WeatherSDK.deleteInstance("test_polling");
            System.out.println("Polling остановлен");

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
