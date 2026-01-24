import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AirportSystemGUI extends JFrame {
    private List<Flight> flights = new ArrayList<>();
    private DefaultListModel<Flight> flightModel = new DefaultListModel<>();
    private JList<Flight> flightList = new JList<>(flightModel);

    // Поля ввода
    private JTextField tfNumber = new JTextField(10);
    private JTextField tfDeparture = new JTextField(10);
    private JTextField tfDestination = new JTextField(10);
    private JTextField tfDay = new JTextField(10);
    private JTextField tfTime = new JTextField(10);
    private JTextField tfPrice = new JTextField(10);
    private JTextField tfSearch = new JTextField(10);
    private String role; // admin или user

    // Тема
    private JComboBox<String> cbTheme;


    private final java.util.Map<String, Theme> themes = new java.util.HashMap<>();

    public AirportSystemGUI(String role) {
        super("Airport");
        this.role = role;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));
        // Определяем темы
        themes.put("Light", new Theme(
                Color.WHITE, new Color(245,245,245), Color.BLACK, new Color(220,220,220),
                new Color(255,255,255), new Color(240,248,255), new Color(173,216,230)
        ));
        themes.put("Dark", new Theme(
                new Color(34,34,34), new Color(51,51,51), new Color(197, 191, 191), new Color(85,85,85),
                new Color(48,48,48), new Color(60,60,60), new Color(70,130,180)
        ));
        themes.put("Blue", new Theme(
                new Color(225,238,255), new Color(200,225,255), new Color(10,30,60), new Color(100,150,220),
                new Color(235,245,255), new Color(215,235,255), new Color(30,144,255)
        ));
        themes.put("Green", new Theme(
                new Color(235,255,240), new Color(210,245,210), new Color(10,50,10), new Color(140,200,140),
                new Color(245,255,245), new Color(225,245,225), new Color(60,179,113)
        ));

        // Верхняя панель для добавления рейса
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Добавить рейс"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; topPanel.add(new JLabel("Номер:"), gbc);
        gbc.gridx = 1; topPanel.add(tfNumber, gbc);

        gbc.gridx = 2; topPanel.add(new JLabel("Пункт отправления:"), gbc);
        gbc.gridx = 3; topPanel.add(tfDeparture, gbc);

        gbc.gridx = 4; topPanel.add(new JLabel("Пункт назначения:"), gbc);
        gbc.gridx = 5; topPanel.add(tfDestination, gbc);

        gbc.gridx = 0; gbc.gridy = 1; topPanel.add(new JLabel("День:"), gbc);
        gbc.gridx = 1; topPanel.add(tfDay, gbc);

        gbc.gridx = 2; topPanel.add(new JLabel("Время:"), gbc);
        gbc.gridx = 3; topPanel.add(tfTime, gbc);

        gbc.gridx = 4; topPanel.add(new JLabel("Цена:"), gbc);
        gbc.gridx = 5; topPanel.add(tfPrice, gbc);

        JButton btnAdd = new JButton("Добавить");
        btnAdd.addActionListener(e -> addFlight());
        gbc.gridx = 6; gbc.gridy = 0; gbc.gridheight = 2;
        topPanel.add(btnAdd, gbc);

        // Тема выбора
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cbTheme = new JComboBox<>(new String[]{"Light","Dark","Blue","Green"});
        cbTheme.addActionListener(e -> applyTheme((String)cbTheme.getSelectedItem()));
        themePanel.add(new JLabel("Тема:"));
        themePanel.add(cbTheme);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(topPanel, BorderLayout.CENTER);
        northWrapper.add(themePanel, BorderLayout.SOUTH);
        add(northWrapper, BorderLayout.NORTH);

        // Центр — список рейсов
        flightList.setCellRenderer(new FlightCellRenderer());
        flightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(flightList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Список рейсов"));
        add(scrollPane, BorderLayout.CENTER);

        // Нижняя панель — поиск и управление
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Поиск рейса"));
        bottomPanel.add(new JLabel("Фильтрация по пункту отправления или пункту назначения:"));
        bottomPanel.add(tfSearch);
        JButton btnFiltration = new JButton("Найти");
        JButton btnShowAll = new JButton("Показать все");
        JButton btnDelete = new JButton("Удалить выделенный");
        JButton btnDeleteAll = new JButton("Удалить все");
        JButton btnSortByPrice = new JButton(("Сортировать по цене"));
        JButton btnExit = new JButton("Выход");
        btnFiltration.addActionListener(e -> filtrationFlights());
        btnShowAll.addActionListener(e -> showAllFlights());
        btnDelete.addActionListener(e -> deleteSelected());
        btnDeleteAll.addActionListener(e -> deleteAll());
        btnSortByPrice.addActionListener(e -> sortByPrice());
        btnExit.addActionListener(e -> System.exit(0));
        bottomPanel.add(btnFiltration);
        bottomPanel.add(btnShowAll);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnDeleteAll);
        bottomPanel.add(btnSortByPrice);
        bottomPanel.add(btnExit);
        // ------ Ограничения для пользователя ------
        if (role.equals("user")) {
            // скрытие верхней панели
            topPanel.setVisible(false);
            // в верхней панели оставляем тему
            themePanel.setVisible(true);
            // скрытие кнопок удаления
            btnDelete.setVisible(false);
            btnDeleteAll.setVisible(false);
            // можно также защитить от кликов, если кто-то из кода обратится
            btnAdd.setEnabled(false);
        }

        add(bottomPanel, BorderLayout.SOUTH);

        // Инициализация темы
        cbTheme.setSelectedItem("Light");
        applyTheme("Light");

        loadFlightsFromFile();
    }

    private void addFlight() {
        try {
            String num = tfNumber.getText().trim();
            String dep = tfDeparture.getText().trim();
            String dest = tfDestination.getText().trim();
            String days = tfDay.getText().trim();
            String time = tfTime.getText().trim();
            String priceStr = tfPrice.getText().trim();

            if (num.isEmpty() || dep.isEmpty() || dest.isEmpty() || days.isEmpty() || time.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!");
                return;
            }

            // Проверка номера рейса: только 3 цифры
            if (!num.matches("\\d{3}")) {
                JOptionPane.showMessageDialog(this, "Номер рейса должен состоять из трёх цифр! Пример: 123");
                return;
            }

            // Проверка, что пункт отправление — это город
            if (!dep.matches("[А-Яа-яA-Za-z]+([ -][А-Яа-яA-Za-z]+)*")) {
                JOptionPane.showMessageDialog(this,
                        "Поле 'Пункт отправление' должно содержать название города! Пример: Астана, Алматы");
                return;
            }

            // Проверка, что Пункт назначение — это город
            if (!dest.matches("[А-Яа-яA-Za-z]+([ -][А-Яа-яA-Za-z]+)*")) {
                JOptionPane.showMessageDialog(this,
                        "Поле 'Пункт назначение' должно содержать название города. Пример: Шымкент, Ташкент");
                return;
            }

            // Проверка дней недели на кириллице через запятую (разрешаем: Пн, Вт, Ср, Чт, Пт, Сб, Вс)
            if (!days.matches("([А-Яа-я]{1,3})(, *[А-Яа-я]{1,3})*")) {
                JOptionPane.showMessageDialog(this,
                        "Введите дни недели через запятую (кириллица). Пример: Пн, Ср, Пт");
                return;
            }

            // Проверка времени (HH:MM)
            if (!time.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
                JOptionPane.showMessageDialog(this, "Введите корректное время в формате HH:MM!");
                return;
            }

            // Проверка цены
            Double price = Double.parseDouble(priceStr);
            if (price < 30000.0 || price > 200000.0) {
                JOptionPane.showMessageDialog(this,
                        "Цена должна варьироваться от 30000 тенге до 200000 тенге!");
                return;
            }

            Flight f = new Flight(num, dep, dest, days, time, price);
            flights.add(f);
            flightModel.addElement(f);
            saveFlightsToFile();


            JOptionPane.showMessageDialog(this, "Рейс успешно добавлен!");
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: цена должна быть числом!");
        }
    }

    private static final String FLIGHTS_FILE = "flights.txt";

    private void saveFlightsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FLIGHTS_FILE))) {
            for (Flight f : flights) {
                writer.println(f.toString());   // Сохраняем ровно в формате toString()
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении файла!");
        }
    }

    private void loadFlightsFromFile() {
        File file = new File(FLIGHTS_FILE);
        if (!file.exists()) return;

        flights.clear();
        flightModel.clear();

        try (Scanner sc = new Scanner(file, "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                Flight f = Flight.fromString(line);
                if (f != null) {
                    flights.add(f);
                    flightModel.addElement(f);
                } else {
                    // пропускаем некорректную строку, но логируем
                    System.err.println("Пропущена строка при загрузке: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке рейсов из файла: " + e.getMessage());
        }
    }


    private void clearFields() {
        tfNumber.setText("");
        tfDeparture.setText("");
        tfDestination.setText("");
        tfDay.setText("");
        tfTime.setText("");
        tfPrice.setText("");
    }

    private void showAllFlights() {
        flightModel.clear();
        for (Flight f : flights) flightModel.addElement(f);
    }

    private void filtrationFlights() {
        String query = tfSearch.getText().trim().toLowerCase();
        flightModel.clear();
        for (Flight f : flights) {
            if (f.getDeparture().toLowerCase().contains(query)
                    || f.getDestination().toLowerCase().contains(query)) {
                flightModel.addElement(f);
            }
        }
        if (flightModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Рейсов не найдено.");
        }
    }

    private void deleteSelected() {
        Flight sel = flightList.getSelectedValue();
        if (sel != null) {
            int idx = flightList.getSelectedIndex();
            flights.remove(sel);
            flightModel.remove(idx);
            saveFlightsToFile();
        } else {
            JOptionPane.showMessageDialog(this, "Выберите рейс для удаления.");
        }
    }
    private void deleteAll(){
        flights.removeAll(flights);
        flightModel.removeAllElements();
        saveFlightsToFile();
    }

    private void sortByPrice() {
        // Сортируем список рейсов по цене
        flights.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));

        // Обновляем модель списка
        flightModel.clear();
        for (Flight f : flights) {
            flightModel.addElement(f);
        }
    }

    // Применение темы к компонентам
    private void applyTheme(String name) {
        Theme t = themes.getOrDefault(name, themes.get("Light"));

        getContentPane().setBackground(t.background);

        // Применение к верхним/нижним панелям — рекурсивно
        applyToPanel(this.getContentPane(), t);

        // Перерисовать список
        flightList.repaint();
    }

    private void applyToPanel(Container c, Theme t) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                p.setBackground(t.panel);
                // если рамка с заголовком - перекрасим текст заголовка
                if (p.getBorder() instanceof TitledBorder) {
                    ((TitledBorder) p.getBorder()).setTitleColor(t.text);
                }
                applyToPanel(p, t);
            } else if (comp instanceof JLabel) {
                comp.setForeground(t.text);
            } else if (comp instanceof JButton) {
                JButton b = (JButton) comp;
                b.setBackground(t.button);
                b.setForeground(t.text);
            } else if (comp instanceof JTextField) {
                comp.setBackground(Color.WHITE); // поле ввода всегда белое для читаемости
                comp.setForeground(Color.BLACK);
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(t.panel);
                applyToPanel((Container) comp, t);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(t.panel);
                comp.setForeground(t.text);
            }
        }
    }

    // Кастомный рендерер для списка — чередование строк и подсветка
    private class FlightCellRenderer extends JLabel implements ListCellRenderer<Flight> {
        FlightCellRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Flight> list, Flight value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Theme t = themes.getOrDefault((String)cbTheme.getSelectedItem(), themes.get("Light"));

            if (value == null) {
                setText("");
            } else {
                setText(value.toString());
            }

            Color bg = (index % 2 == 0) ? t.listAlt1 : t.listAlt2;
            Color fg = t.text;

            if (isSelected) {
                bg = t.listSelection;
                fg = Color.white;
            }

            setBackground(bg);
            setForeground(fg);

            return this;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Authentication().setVisible(true));
    }
}


