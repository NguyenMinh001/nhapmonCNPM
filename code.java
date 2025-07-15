import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PersonalTaskManager {

    private static final String DB_FILE_PATH = "tasks_database.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    
    private JSONArray loadTasksFromDb() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(DB_FILE_PATH)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                return (JSONArray) obj;
            }
        } catch (IOException | ParseException e) {
            System.err.println("Lỗi khi đọc file database: " + e.getMessage());
        }
        return new JSONArray();
    }

    private void saveTasksToDb(JSONArray tasksData) {
        try (FileWriter file = new FileWriter(DB_FILE_PATH)) {
            file.write(tasksData.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi vào file database: " + e.getMessage());
        }
    }

    
    private boolean validateInputs(String title, String dueDateStr, String priority) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Lỗi: Tiêu đề không được để trống.");
            return false;
        }
        if (dueDateStr == null || dueDateStr.trim().isEmpty()) {
            System.out.println("Lỗi: Ngày đến hạn không được để trống.");
            return false;
        }
        try {
            LocalDate.parse(dueDateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Lỗi: Ngày đến hạn không hợp lệ. Định dạng đúng: YYYY-MM-DD.");
            return false;
        }

        String[] validPriorities = {"Thấp", "Trung bình", "Cao"};
        for (String p : validPriorities) {
            if (p.equals(priority)) return true;
        }
        System.out.println("Lỗi: Mức độ ưu tiên không hợp lệ. Chọn từ: Thấp, Trung bình, Cao.");
        return false;
    }

    
    private boolean isDuplicate(JSONArray tasks, String title, String dueDateStr) {
        for (Object obj : tasks) {
            JSONObject task = (JSONObject) obj;
            if (task.get("title").toString().equalsIgnoreCase(title)
                && task.get("due_date").toString().equals(dueDateStr)) {
                return true;
            }
        }
        return false;
    }

    
    private JSONObject createTask(String title, String description, String dueDateStr,
                                  String priority, boolean isRecurring) {
        JSONObject task = new JSONObject();
        task.put("id", UUID.randomUUID().toString());
        task.put("title", title);
        task.put("description", description);
        task.put("due_date", dueDateStr);
        task.put("priority", priority);
        task.put("status", "Chưa hoàn thành");
        task.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        task.put("last_updated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        task.put("is_recurring", isRecurring);
        return task;
    }

    
    public JSONObject addNewTask(String title, String description,
                                 String dueDateStr, String priority,
                                 boolean isRecurring) {
        if (!validateInputs(title, dueDateStr, priority)) return null;

        JSONArray tasks = loadTasksFromDb();
        if (isDuplicate(tasks, title, dueDateStr)) {
            System.out.printf("Lỗi: Nhiệm vụ '%s' đã tồn tại với cùng ngày đến hạn.%n", title);
            return null;
        }

        JSONObject task = createTask(title, description, dueDateStr, priority, isRecurring);
        tasks.add(task);
        saveTasksToDb(tasks);

        System.out.printf("✅ Đã thêm nhiệm vụ '%s' với ID: %s%n", title, task.get("id"));
        return task;
    }

    public static void main(String[] args) {
        PersonalTaskManager manager = new PersonalTaskManager();

        
        manager.addNewTask("Mua sách", "Sách Công nghệ phần mềm", "2025-07-20", "Cao", false);

        
        manager.addNewTask("Mua sách", "Sách Công nghệ phần mềm", "2025-07-20", "Cao", false);

        
        manager.addNewTask("Tập thể dục", "Tập gym 1 tiếng", "2025-07-21", "Trung bình", true);


        manager.addNewTask("", "Không có tiêu đề", "2025-07-22", "Thấp", false);
    }
}
