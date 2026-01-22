import javax.swing.*;

public class Flight {
    private String flightNumber;
    private String departure;
    private String destination;
    private String daysOfWeek;
    private String departureTime;
    private double ticketPrice;

    public Flight(String flightNumber, String departure, String destination,
                  String daysOfWeek, String departureTime, double ticketPrice) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.daysOfWeek = daysOfWeek;
        this.departureTime = departureTime;
        this.ticketPrice = ticketPrice;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public double getPrice() {
        return ticketPrice;
    }

    public static Flight fromString(String s) {
        if (s == null) return null;
        try {
            // Ожидаемый вид:
            // Рейс №123 | Астана → Алматы | Дни: Пн, Ср | Вылет: 12:30 | Цена: 55000.00 тенге

            String[] parts = s.split("\\|");
            if (parts.length != 5) {
                // Неправильный формат
                System.err.println("Неправильный формат строки рейса: " + s);
                return null;
            }

            // 0: "Рейс №123"
            String num = parts[0].replace("Рейс №", "").trim();

            // 1: "Астана → Алматы"
            String[] cities = parts[1].split("→");
            if (cities.length != 2) {
                System.err.println("Не могу распарсить города: " + parts[1]);
                return null;
            }
            String departure = cities[0].trim();
            String destination = cities[1].trim();

            // 2: "Дни: Пн, Ср"
            String days = parts[2].replace("Дни:", "").trim();

            // 3: "Вылет: 12:30"
            String time = parts[3].replace("Вылет:", "").trim();

            // 4: "Цена: 55000.00 тенге"
            String pricePart = parts[4].replace("Цена:", "").replace("тенге", "").trim();
            // убираем лишние пробелы и non-digit кроме . и ,
            pricePart = pricePart.replace("\u00A0", ""); // NBSP если есть
            pricePart = pricePart.replaceAll("[^0-9,\\.]", "");
            pricePart = pricePart.replace(',', '.'); // на всякий случай

            if (pricePart.isEmpty()) {
                System.err.println("Не найдена цена в строке: " + s);
                return null;
            }

            double price = Double.parseDouble(pricePart);

            return new Flight(num, departure, destination, days, time, price);

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге рейса: " + s + " -> " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("Рейс №%s | %s → %s | Дни: %s | Вылет: %s | Цена: %.2f тенге",
                flightNumber, departure, destination, daysOfWeek, departureTime, ticketPrice);
    }
}
