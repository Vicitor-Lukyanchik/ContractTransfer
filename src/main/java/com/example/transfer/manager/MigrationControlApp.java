package com.example.transfer.manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class MigrationControlApp {

    // Константы цветов
    private static final Color PRIMARY_COLOR = new Color(123, 85, 74);
    private static final Color SUCCESS_COLOR = new Color(85, 139, 82);
    private static final Color ERROR_COLOR = new Color(198, 70, 70);
    private static final Color WARNING_COLOR = new Color(218, 165, 32);
    private static final Color BACKGROUND_COLOR = new Color(253, 245, 230);
    private static final Color TABLE_HEADER_COLOR = new Color(230, 220, 200);
    private static final Color TABLE_SELECTION_COLOR = new Color(210, 195, 170);
    private static final Color DISABLED_COLOR = new Color(180, 170, 160);
    private static final Color HOVER_COLOR = new Color(200, 190, 180);

    private static String migrationDirectory = "C:/Migration/";
    private static int hoverRow = -1;
    private static int hoverCol = -1;

    public static void main(String[] args) {
        migrationDirectory = determineMigrationDirectory(args);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Table.selectionBackground", TABLE_SELECTION_COLOR);
            UIManager.put("Table.selectionForeground", Color.BLACK);
            UIManager.put("Table.rowHeight", 40);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

        private static String determineMigrationDirectory(String[] args) {
        if (args.length > 0) {
            String argPath = args[0].trim();
            File dir = new File(argPath);
            if (dir.exists() && dir.isDirectory()) {
                return argPath.endsWith(File.separator) ? argPath : argPath + File.separator;
            }
            System.out.println("Предупреждение: Указанный путь в аргументах не существует или не является директорией: " + argPath);
        }

        try {
            String appPath = MigrationControlApp.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File appFile = new File(appPath);
            File parentDir = appFile.getParentFile();

            if (parentDir != null && parentDir.getName().equalsIgnoreCase("Migration")) {
                return parentDir.getAbsolutePath() + File.separator;
            }

            File migrationDir = new File(parentDir, "Migration");
            if (migrationDir.exists() && migrationDir.isDirectory()) {
                return migrationDir.getAbsolutePath() + File.separator;
            }
        } catch (Exception e) {
            System.out.println("Не удалось определить расположение программы: " + e.getMessage());
        }

        return "C:" + File.separator + "Migration" + File.separator;
    }
//    private static String determineMigrationDirectory(String[] args) {
//        if (args.length > 0) {
//            String argPath = args[0].trim();
//            File dir = new File(argPath);
//            if (dir.exists() && dir.isDirectory()) {
//                return argPath.endsWith(File.separator) ? argPath : argPath + File.separator;
//            }
//            System.out.println("Предупреждение: Указанный путь в аргументах не существует или не является директорией: " + argPath);
//        }
//
//
//        String appPath = null;
//        try {
//            appPath = MigrationControlApp.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        File appFile = new File(appPath);
//        File parentDir = appFile.getParentFile();
//
//        return parentDir.getAbsolutePath() + File.separator;
//    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Панель управления миграциями - [" + migrationDirectory + "]");
        ImageIcon icon = new ImageIcon("L:\\W_USERS\\STREAMS\\jdk-11\\ico\\icons8-migration-24-_1_.ico"); // или .ico, но лучше .png
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 630);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Панель управления миграциями");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);


        JLabel subtitleLabel = new JLabel("Управление процессами миграции данных");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Таблица
        String[] columnNames = {"Миграция", "Статус", "Статус выполнения", "Управление", "Редактировать"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        configureTableAppearance(table);

        // Настройка рендерера с быстрым hover-эффектом
        table.setDefaultRenderer(Object.class, new HoverAwareTableCellRenderer(table));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        refreshTable(table);
        setupTableListeners(table, frame);
        startTableUpdateTimer(table);
    }

    private static void configureTableAppearance(JTable table) {
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(TABLE_SELECTION_COLOR);
        table.setSelectionForeground(Color.BLACK);

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(TABLE_HEADER_COLOR);
        table.getTableHeader().setForeground(Color.DARK_GRAY);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
    }

    private static class HoverAwareTableCellRenderer extends DefaultTableCellRenderer {
        private final JTable table;
        private final Map<Integer, Boolean> runningStatusCache = new HashMap<>();

        public HoverAwareTableCellRenderer(JTable table) {
            this.table = table;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));

            switch (column) {
                case 0:
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    label.setForeground(PRIMARY_COLOR);
                    break;
                case 1:
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setForeground("●".equals(value) ? SUCCESS_COLOR : Color.GRAY);
                    break;
                case 2:
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setForeground(value.toString().startsWith("✖") ? ERROR_COLOR : SUCCESS_COLOR);
                    break;
                case 3:
                    configureControlButton(label, value);
                    break;
                case 4:
                    configureEditButton(label, table, row);
                    break;
            }

            // Применяем hover-эффект для всех кликабельных ячеек
            if (row == hoverRow && column == hoverCol) {
                if (column == 3 || (column == 4 && !isMigrationRunning(table, row))) {
                    label.setBackground(HOVER_COLOR);
                }
            }

            return label;
        }

        private void configureControlButton(JLabel label, Object value) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
            if ("■ Остановить".equals(value)) {
                label.setForeground(ERROR_COLOR);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            } else if ("▶ Запуск".equals(value)) {
                label.setForeground(SUCCESS_COLOR);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            }
        }

        private void configureEditButton(JLabel label, JTable table, int row) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
            String migration = (String) table.getValueAt(row, 0);
            String migrationIdentifier = migration.split(" ")[0];
            boolean isRunning = ProcessMonitor.isProcessRunning("java.exe", migrationIdentifier);

            // Цвет зависит от состояния миграции
            label.setForeground(isRunning ? DISABLED_COLOR : WARNING_COLOR);
        }
    }

    private static void setupTableListeners(JTable table, JFrame frame) {
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int newRow = table.rowAtPoint(e.getPoint());
                int newCol = table.columnAtPoint(e.getPoint());

                if (newRow != hoverRow || newCol != hoverCol) {
                    repaintHoveredCells(table, newRow, newCol);
                    updateCursor(table, newRow, newCol);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick(table, frame, e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clearHoverState(table);
            }
        });

        table.setToolTipText("");

        table.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == 2 && row >= 0) { // Столбец "Статус выполнения"
                    Object value = table.getValueAt(row, col);
                    if (value instanceof MigrationControlApp.LogCellData) {
                        LogCellData cellData = (LogCellData) value;
                        if (cellData.hasError && cellData.tooltipText != null && !cellData.tooltipText.isEmpty()) {
                            table.setToolTipText("<html><pre>" + cellData.tooltipText + "</pre></html>");
                        } else {
                            table.setToolTipText("");
                        }
                    } else {
                        table.setToolTipText("");
                    }
                } else {
                    table.setToolTipText("");
                }
            }
        });
    }

    private static void repaintHoveredCells(JTable table, int newRow, int newCol) {
        if (hoverRow >= 0 && hoverCol >= 0) {
            table.repaint(table.getCellRect(hoverRow, hoverCol, false));
        }

        hoverRow = newRow;
        hoverCol = newCol;

        if (hoverRow >= 0 && hoverCol >= 0) {
            table.repaint(table.getCellRect(hoverRow, hoverCol, false));
        }
    }

    private static void updateCursor(JTable table, int row, int col) {
        if (row >= 0 && col >= 0 &&
                (col == 3 || (col == 4 && !isMigrationRunning(table, row)))) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            table.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static boolean isMigrationRunning(JTable table, int row) {
        String migration = (String) table.getValueAt(row, 0);
        String migrationIdentifier = migration.split(" ")[0];
        return ProcessMonitor.isProcessRunning("java.exe", migrationIdentifier);
    }

    private static void handleTableClick(JTable table, JFrame frame, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (row >= 0 && col >= 0) {
            String migration = (String) table.getValueAt(row, 0);
            String migrationIdentifier = migration.split(" ")[0];

            if (col == 3) {
                handleControlButtonClick(table, row, migrationIdentifier, frame);
            } else if (col == 4) {
                if (!isMigrationRunning(table, row)) {
                    openConfigEditor(migrationDirectory + migrationIdentifier + "/application.properties", frame);
                }
            }
        }
    }

    private static void handleControlButtonClick(JTable table, int row, String migrationIdentifier, JFrame frame) {
        if (ProcessMonitor.isProcessRunning("java.exe", migrationIdentifier)) {
            ProcessMonitor.stopProcess("java.exe", migrationIdentifier);
            updateRowStatus(table, row, false);
        } else {
            try {
                Runtime.getRuntime().exec("cmd /c start /min cmd /c " +
                        migrationDirectory + migrationIdentifier + "/transfer.bat");
                updateRowStatus(table, row, true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Ошибка запуска процесса", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
        table.repaint();
    }

    private static void clearHoverState(JTable table) {
        if (hoverRow >= 0 && hoverCol >= 0) {
            table.repaint(table.getCellRect(hoverRow, hoverCol, false));
        }
        hoverRow = -1;
        hoverCol = -1;
        table.setCursor(Cursor.getDefaultCursor());
    }

    private static void startTableUpdateTimer(JTable table) {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateTableData(table));
            }
        }, 0, 900);
    }

    private static void updateRowStatus(JTable table, int row, boolean isRunning) {
        table.setValueAt(isRunning ? "●" : "○", row, 1);
        table.setValueAt(isRunning ? "■ Остановить" : "▶ Запуск", row, 3);
        table.setValueAt("✎ Редактировать", row, 4);
    }

    private static void refreshTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        File migrationDir = new File(migrationDirectory);
        if (migrationDir.exists()) {
            for (File dir : migrationDir.listFiles()) {
                if (dir.isDirectory() &&
                        new File(dir, "transfer.bat").exists() &&
                        new File(dir, "transfer.jar").exists()) {
                    model.addRow(new Object[]{
                            dir.getName(),
                            "○",
                            new LogCellData("✓ (еще не запускалось)", "", false),
                            "▶ Запуск",
                            "✎ Редактировать"
                    });
                }
            }
        }
    }

    private static void updateTableData(JTable table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            String migration = (String) table.getValueAt(i, 0);
            String migrationIdentifier = migration.split(" ")[0];
            boolean isRunning = ProcessMonitor.isProcessRunning("java.exe", migrationIdentifier);

            table.setValueAt(isRunning ? "●" : "○", i, 1);
            table.setValueAt(isRunning ? "■ Остановить" : "▶ Запуск", i, 3);

            try {
                LogParser.LogInfo logInfo = LogParser.getLastLogInfo(
                        migrationDirectory + migrationIdentifier + "/info.log");
                String status = logInfo.hasError ? "✖" : "✓";
                table.setValueAt(new LogCellData(
                        status + " (" + logInfo.timestamp + ")",
                        logInfo.lastLog,
                        logInfo.hasError), i, 2);
            } catch (IOException ex) {
                table.setValueAt(new LogCellData(
                        "✖ Логи недоступны",
                        "Ошибка чтения файла логов: " + ex.getMessage(),
                        true), i, 2);
            }
        }
    }

    private static void openConfigEditor(String configPath, JFrame parent) {
        File configFile = new File(configPath);
        if (configFile.exists()) {
            JDialog editorDialog = new JDialog(parent, "Редактирование конфигурации", true);
            editorDialog.setSize(1100, 580);
            editorDialog.setLocationRelativeTo(parent);
            editorDialog.add(new ConfigEditor(configFile, editorDialog));
            editorDialog.setVisible(true);
        }
    }

    private static class LogCellData {
        private final String displayText;
        private final String tooltipText;
        private final boolean hasError;

        public LogCellData(String displayText, String tooltipText, boolean hasError) {
            this.displayText = displayText;
            this.tooltipText = tooltipText;
            this.hasError = hasError;
        }

        @Override
        public String toString() {
            return displayText;
        }

        public String getTooltipText() {
            return hasError ? tooltipText : null;
        }
    }


}

