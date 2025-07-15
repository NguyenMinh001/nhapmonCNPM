if (!TaskValidator.isValid(title, dueDateStr, priority)) return null;

if (TaskChecker.isDuplicate(tasks, title, dueDateStr)) { ... }