import java.time.LocalDateTime;

public class Duke {
    public static void main(String[] args) {
        boolean running = true;
        TaskStorage taskStorage = new TaskStorage();
        TaskList taskList;

        // Load tasks from TaskStorage
        try {
            taskList = new TaskList(taskStorage);
        } catch (DukeException e) {
            System.out.printf("[!] %s\n", e.getMessage());
            return;
        }

        Ui ui = new Ui("Pong");
        ui.init();

        while (running) {
            try {
                Parser parser = ui.getParsedInput();
                running = Duke.handleInput(parser, taskList, ui);
            } catch (DukeException e) {
                ui.printException(e);
            }
        }

        ui.exit();
    }

    private static boolean handleInput(Parser parser, TaskList taskList, Ui ui) throws DukeException {
        Task task;
        switch (parser.getCommand()) {
        case "bye":
            return false;
        case "list":
            ui.listTasks(taskList.getTasks());
            break;
        case "mark":
            task = taskList.markTask(parser.getArgAsInt());
            ui.markTask(task);
            break;
        case "unmark":
            task = taskList.unmarkTask(parser.getArgAsInt());
            ui.unmarkTask(task);
            break;
        case "delete":
            task = taskList.deleteTask(parser.getArgAsInt());
            ui.deleteTask(task);
            break;
        case "todo":
            String todoName = parser.getArg();
            if (todoName == null || todoName.equals("")) {
                throw new DukeException("Todo name cannot be empty");
            }
            task = new Todo(todoName);
            taskList.addTask(task);
            ui.addTask(task);
            break;
        case "deadline":
            String deadlineName = parser.getArg();
            if (deadlineName == null || deadlineName.equals("")) {
                throw new DukeException("Deadline name cannot be empty");
            }

            LocalDateTime deadline;
            deadline = parser.getOptArgAsDateTime("by");

            if (deadline == null) {
                throw new DukeException("Use /by to specify deadline date");
            }

            task = new Deadline(deadlineName, deadline);
            taskList.addTask(task);
            ui.addTask(task);
            break;
        case "event":
            String eventName = parser.getArg();
            if (eventName == null || eventName.equals("")) {
                throw new DukeException("Deadline name cannot be empty");
            }

            LocalDateTime from, to;
            from = parser.getOptArgAsDateTime("from");
            to = parser.getOptArgAsDateTime("to");

            if (from == null || to == null) {
                throw new DukeException("Use /from and /to to specify event duration");
            }

            task = new Event(eventName, from, to);
            taskList.addTask(task);
            ui.addTask(task);
            break;
        }

        return true;
    }
}
