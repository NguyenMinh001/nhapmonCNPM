JSONArray tasks = TaskStorage.loadTasks();
TaskStorage.saveTasks(tasks);

JSONObject task = TaskFactory.createTask(...);
