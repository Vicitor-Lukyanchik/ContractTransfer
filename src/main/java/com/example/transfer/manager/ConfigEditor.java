package com.example.transfer.manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class ConfigEditor extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(123, 85, 74);
    private static final Color SECONDARY_COLOR = new Color(160, 120, 90);
    private static final Color BACKGROUND_COLOR = new Color(253, 245, 230);
    private static final Color PANEL_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(60, 50, 40);
    private static final Color DESCRIPTION_COLOR = new Color(120, 110, 100);

    private final Properties properties = new Properties();
    private final File configFile;
    private final Map<String, JTextField> fieldMap = new LinkedHashMap<>();
    private final Map<String, String> descriptions = new LinkedHashMap<>();
    private final JDialog parentDialog;

    public ConfigEditor(File configFile, JDialog parentDialog) {
        this.configFile = configFile;
        this.parentDialog = parentDialog;
        loadDescriptions();
        loadProperties();
        initializeUI();
    }

    private void loadDescriptions() {
        // Настройки программы
//        descriptions.put("scan.base.package", "Указывает пакет, в котором программа будет искать классы для работы");
        descriptions.put("dbf.default.source.path", "Путь к сетевой папке, где находятся исходные DBF-файлы");
        descriptions.put("local.cache.path", "Путь к локальной папке для временного хранения DBF-файлов");
        descriptions.put("server.port", "Порт сервера, на котором работает программа");
        descriptions.put("migration.interval.hours", "Как часто будет происходить миграция (1 = раз в час, 0.5 = раз в полчаса)");

        // Настройки базы данных
        descriptions.put("spring.datasource.url", "Строка подключения к базе данных Oracle");
        descriptions.put("spring.datasource.username", "Имя пользователя для подключения к базе данных");
        descriptions.put("spring.datasource.password", "Пароль для подключения к базе данных");

        // Настройки электронной почты
        descriptions.put("email.recipients", "Адреса электронной почты для уведомлений (через запятую)");
        descriptions.put("email.error.cooldown.hours", "Минимальное время между повторными уведомлениями об ошибке (в часах)");
        descriptions.put("email.sender", "Адрес отправителя");
        descriptions.put("email.smtp.host", "Адрес SMTP-сервера");
        descriptions.put("email.smtp.port", "Порт SMTP-сервера");

        // Настройки логирования
        descriptions.put("logging.file.path", "Путь к папке для хранения логов");
        descriptions.put("logging.file.name", "Имя файла лога");
        descriptions.put("logging.file.maxHistory", "Количество дней хранения старых логов");
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки файла конфигурации", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProperties() {
        // Сначала обновляем все свойства из полей ввода
        for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue().getText());
        }


        try (OutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, "Updated configuration");

            // Анимация успешного сохранения
            Timer timer = new Timer("SaveAnimation", true);
            TimerTask task = new TimerTask() {
                int count = 0;
                final Color originalColor = saveButton.getBackground();

                @Override
                public void run() {
                    if (count < 3) {
                        saveButton.setBackground(count % 2 == 0 ? new Color(46, 125, 50) : originalColor);
                        count++;
                    } else {
                        saveButton.setBackground(originalColor);
                        parentDialog.dispose(); // Закрываем окно после анимации
                        cancel();
                    }
                }
            };
            timer.schedule(task, 0, 200);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка сохранения файла конфигурации", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton saveButton;

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BACKGROUND_COLOR);

        // Заголовок
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Редактор конфигурации");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel fileLabel = new JLabel(configFile.getAbsolutePath());
        fileLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fileLabel.setForeground(DESCRIPTION_COLOR);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(fileLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Основное содержимое с вкладками
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Вкладка "Настройки программы"
        tabbedPane.addTab("Настройки программы", createSettingsPanel(
                  "dbf.default.source.path","migration.interval.hours","local.cache.path",
                "server.port"
        ));

        // Вкладка "Настройки БД"
        tabbedPane.addTab("Настройки базы данных", createSettingsPanel(
                "spring.datasource.url", "spring.datasource.username", "spring.datasource.password"
        ));

        // Вкладка "Настройки почты"
        tabbedPane.addTab("Настройки почты", createSettingsPanel(
                "email.recipients", "email.error.cooldown.hours", "email.sender", "email.smtp.host",
                "email.smtp.port"
        ));

        // Вкладка "Настройки логирования"
//        tabbedPane.addTab("Настройки логирования", createSettingsPanel(
//                "logging.file.path", "logging.file.name", "logging.file.maxHistory"
//        ));

        // Кнопка сохранения
        saveButton = new JButton("Сохранить изменения");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        saveButton.setBackground(SECONDARY_COLOR);
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        // Эффект при наведении
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(PRIMARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(SECONDARY_COLOR);
            }
        });

        saveButton.addActionListener(e -> {
            saveProperties(); // Вызываем метод сохранения свойств
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(15, 0, 5, 0));
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.add(saveButton);

        // Добавление компонентов
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSettingsPanel(String... propertyKeys) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setBackground(PANEL_BACKGROUND);

        for (String key : propertyKeys) {
            String value = properties.getProperty(key, "");
            String description = descriptions.getOrDefault(key, "");

            JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
            fieldPanel.setBorder(new EmptyBorder(5, 0, 15, 0));
            fieldPanel.setBackground(Color.WHITE);

            // Название параметра
            JLabel nameLabel = new JLabel(key);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setForeground(TEXT_COLOR);

            // Описание параметра
            JTextArea descArea = new JTextArea(description);
            descArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
            descArea.setForeground(DESCRIPTION_COLOR);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBackground(Color.WHITE);
            descArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            // Поле ввода
            JTextField textField = new JTextField(value);
            textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            textField.setBackground(Color.WHITE);
            textField.setForeground(TEXT_COLOR);
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));

            // Сохраняем ссылку на поле ввода
            fieldMap.put(key, textField);

            // Сохраняем изменения при редактировании
            textField.addActionListener(e -> properties.setProperty(key, textField.getText()));

            fieldPanel.add(nameLabel, BorderLayout.NORTH);
            fieldPanel.add(descArea, BorderLayout.CENTER);
            fieldPanel.add(textField, BorderLayout.SOUTH);
            panel.add(fieldPanel);
        }

        // Добавляем прокрутку
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(900, 580));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Увеличиваем шаг прокрутки
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Отложенная установка позиции скролла
        SwingUtilities.invokeLater(() -> {
            scrollPane.getViewport().setViewPosition(new Point(0, 0));
        });

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }
}
