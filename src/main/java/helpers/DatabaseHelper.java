package helpers;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);
    private final ConfigContainer config = ConfigContainer.getInstance();

    private Connection getConnection() throws SQLException {
        String dbUrl = config.getConfigParameter("DB_URL");
        String dbUser = config.getConfigParameter("DB_USER");
        String dbPassword = config.getConfigParameter("DB_PASSWORD");

        logger.info("Подключение к БД: {}", dbUrl);
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Получить объявление из БД по ID
     */
    @SneakyThrows
    public Map<String, Object> getAnnouncementById(Long id) {
        // Таблица называется announcement, а не announcements
        logger.info("Поиск объявления с ID: {}", id);
        String query = "SELECT id, title, price, description, user_id, date_of_publication FROM announcement WHERE id = ?";
        logger.info("SQL: {}", query);

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> announcement = new HashMap<>();
                announcement.put("id", rs.getLong("id"));
                announcement.put("title", rs.getString("title"));
                announcement.put("price", rs.getInt("price"));
                announcement.put("description", rs.getString("description"));
                announcement.put("user_id", rs.getLong("user_id"));
                announcement.put("date_of_publication", rs.getTimestamp("date_of_publication"));
                return announcement;
            }
        }
        return null;
    }

    @SneakyThrows
    public Map<String, Object> getAnnouncementByIdWithRetry(Long id, int maxRetries, int delaySeconds) {
        for (int i = 0; i < maxRetries; i++) {
            Map<String, Object> result = getAnnouncementById(id);
            if (result != null) {
                logger.info("Запись найдена после {} попытки", i + 1);
                return result;
            }
            logger.info("Запись с ID {} не найдена, попытка {}/{}", id, i + 1, maxRetries);
            Thread.sleep(delaySeconds * 1000L);
        }
        return null;
    }

    /**
     * Проверить, существует ли объявление в БД
     */
    @SneakyThrows
    public boolean announcementExists(Long id) {
        String query = "SELECT 1 FROM announcement WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    /**
     * Удалить объявление из БД (для очистки после тестов)
     */
    @SneakyThrows
    public void deleteAnnouncement(Long id) {
        String query = "DELETE FROM announcement WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setLong(1, id);
            int rows = stmt.executeUpdate();
            logger.info("Удалено объявление с ID {} ({} строк)", id, rows);
        }
    }
}