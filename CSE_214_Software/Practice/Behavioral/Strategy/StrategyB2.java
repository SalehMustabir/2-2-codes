
import java.util.*;

// ============================================================
// ENUM: Priority
// ============================================================

enum Priority {
    HIGH,
    MEDIUM,
    LOW
}


// ============================================================
// ENUM: Scheduling Policy
// ============================================================

enum SchedulingPolicy {
    FCFS,
    PRIORITY,
    SJF
}


// ============================================================
// TASK CLASS
// ============================================================

class Task {

    private String taskId;
    private int startTime;
    private int endTime;
    private Priority priority;

    public Task(String taskId, int startTime, int endTime,
                Priority priority) {

        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public Priority getPriority() {
        return priority;
    }

    // Execution Time = End Time - Start Time
    public int getExecutionTime() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return taskId +
               " [Start=" + startTime +
               ", End=" + endTime +
               ", Priority=" + priority +
               ", ExecutionTime=" + getExecutionTime() + "]";
    }
}


// ============================================================
// STRATEGY INTERFACE
// ============================================================

// Each scheduling policy implements this interface.
// The scheduler can switch between these strategies.
interface SchedulingStrategy {

    // Select one task from the waiting queue.
    Task selectTask(List<Task> tasks);

    // Return the name of the policy.
    String getPolicyName();
}


// ============================================================
// FCFS STRATEGY
// ============================================================

// First Come First Served:
// Select task having the earliest start time.
class FCFSStrategy implements SchedulingStrategy {

    @Override
    public Task selectTask(List<Task> tasks) {

        return Collections.min(
            tasks,
            Comparator.comparingInt(Task::getStartTime)
        );
    }

    @Override
    public String getPolicyName() {
        return "FCFS";
    }
}


// ============================================================
// PRIORITY STRATEGY
// ============================================================

// Priority order:
// HIGH > MEDIUM > LOW
//
// If priority is same, earliest start time wins.
class PriorityStrategy implements SchedulingStrategy {

    @Override
    public Task selectTask(List<Task> tasks) {

        return Collections.min(
            tasks,
            Comparator
                .comparingInt((Task t) ->
                    getPriorityValue(t.getPriority()))
                .thenComparingInt(Task::getStartTime)
        );
    }

    // Smaller value means higher priority.
    private int getPriorityValue(Priority priority) {

        switch (priority) {

            case HIGH:
                return 1;

            case MEDIUM:
                return 2;

            case LOW:
                return 3;

            default:
                return 4;
        }
    }

    @Override
    public String getPolicyName() {
        return "Priority Scheduling";
    }
}


// ============================================================
// SJF STRATEGY
// ============================================================

// Shortest Job First:
// Select task having the smallest execution time.
//
// If execution time is same,
// earliest start time wins.
class SJFStrategy implements SchedulingStrategy {

    @Override
    public Task selectTask(List<Task> tasks) {

        return Collections.min(
            tasks,
            Comparator
                .comparingInt(Task::getExecutionTime)
                .thenComparingInt(Task::getStartTime)
        );
    }

    @Override
    public String getPolicyName() {
        return "SJF";
    }
}


// ============================================================
// TASK SCHEDULER - CONTEXT
// ============================================================

class TaskScheduler {

    // Tasks currently waiting for execution.
    private List<Task> waitingTasks = new ArrayList<>();

    // User's preferred scheduling policy.
    private SchedulingPolicy preferredPolicy;

    // Strategy objects.
    private SchedulingStrategy fcfsStrategy =
            new FCFSStrategy();

    private SchedulingStrategy priorityStrategy =
            new PriorityStrategy();

    private SchedulingStrategy sjfStrategy =
            new SJFStrategy();


    // Constructor
    public TaskScheduler(SchedulingPolicy preferredPolicy) {

        this.preferredPolicy = preferredPolicy;
    }


    // ========================================================
    // ADD TASK
    // ========================================================

    public void addTask(Task task) {

        waitingTasks.add(task);

        System.out.println(
            "Added: " + task
        );
    }


    // ========================================================
    // CHANGE PREFERRED POLICY
    // ========================================================

    public void setPreferredPolicy(SchedulingPolicy policy) {

        this.preferredPolicy = policy;

        System.out.println(
            "Preferred policy changed to: " + policy
        );
    }


    // ========================================================
    // DETERMINE WHICH POLICY SHOULD BE USED
    // ========================================================

    private SchedulingStrategy determineStrategy() {

        // ----------------------------------------------------
        // RULE 1: URGENT WORKLOAD
        // ----------------------------------------------------
        // If at least one HIGH priority task exists,
        // Priority Scheduling must be used.
        //
        // This has the highest priority among the rules.

        for (Task task : waitingTasks) {

            if (task.getPriority() == Priority.HIGH) {

                return priorityStrategy;
            }
        }


        // ----------------------------------------------------
        // RULE 2: SHORT-TASK WORKLOAD
        // ----------------------------------------------------
        // If there is no HIGH task and at least 3 tasks
        // have execution time <= 3,
        // use SJF.

        int shortTaskCount = 0;

        for (Task task : waitingTasks) {

            if (task.getExecutionTime() <= 3) {

                shortTaskCount++;
            }
        }

        if (shortTaskCount >= 3) {

            return sjfStrategy;
        }


        // ----------------------------------------------------
        // RULE 3: NORMAL WORKLOAD
        // ----------------------------------------------------
        // Otherwise use the user's preferred policy.

        switch (preferredPolicy) {

            case FCFS:
                return fcfsStrategy;

            case PRIORITY:
                return priorityStrategy;

            case SJF:
                return sjfStrategy;

            default:
                return fcfsStrategy;
        }
    }


    // ========================================================
    // EXECUTE NEXT TASK
    // ========================================================

    public void executeNextTask() {

        // Check whether queue is empty.
        if (waitingTasks.isEmpty()) {

            System.out.println(
                "No tasks waiting."
            );

            return;
        }


        // IMPORTANT:
        // Determine the policy AGAIN before every task.
        //
        // This is required because workload conditions
        // may change after a task is removed.
        SchedulingStrategy strategy =
                determineStrategy();


        // Use the selected strategy to find a task.
        Task selectedTask =
                strategy.selectTask(waitingTasks);


        // Remove the selected task from waiting queue.
        waitingTasks.remove(selectedTask);


        // Display result.
        System.out.println(
            "Executing Task: " +
            selectedTask.getTaskId()
        );

        System.out.println(
            "Policy Used: " +
            strategy.getPolicyName()
        );

        System.out.println(
            "Task Details: " +
            selectedTask
        );

        System.out.println();
    }


    // ========================================================
    // EXECUTE ALL TASKS
    // ========================================================

    public void executeAll() {

        while (!waitingTasks.isEmpty()) {

            // executeNextTask() determines the policy again.
            executeNextTask();
        }

        System.out.println(
            "All tasks have been executed."
        );
    }
}


// ============================================================
// MAIN CLASS
// ============================================================

public class StrategyB2 {

    public static void main(String[] args) {

        // User's preferred policy is FCFS.
        TaskScheduler scheduler =
                new TaskScheduler(SchedulingPolicy.FCFS);


        // ----------------------------------------------------
        // Add tasks from the problem's example scenario
        // ----------------------------------------------------

        scheduler.addTask(
            new Task("T1", 0, 8, Priority.MEDIUM)
        );

        scheduler.addTask(
            new Task("T2", 1, 4, Priority.LOW)
        );

        scheduler.addTask(
            new Task("T3", 2, 4, Priority.MEDIUM)
        );

        scheduler.addTask(
            new Task("T4", 3, 4, Priority.LOW)
        );

        scheduler.addTask(
            new Task("T5", 4, 9, Priority.HIGH)
        );


        System.out.println(
            "\n===== EXECUTION START =====\n"
        );


        // Execute all tasks.
        //
        // The scheduler will dynamically select
        // FCFS / Priority / SJF depending on
        // the current waiting workload.
        scheduler.executeAll();
    }
}

