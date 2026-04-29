package twodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import twodo.model.Todo;
import twodo.model.TodoList;
import twodo.model.User;
import twodo.repository.TodoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    public static final int WRITE_POINT = 10;
    public static final int EARLY_BONUS = 20;
    public static final int NORMAL_DONE = 30;
    public static final int TWO_DONE = 50;
    public static final int TWO_ALL_DONE_BONUS = 50;

    private final TodoRepository todoRepository;
    private final UserService userService;

    public int calcWritePoints(boolean earlyBonus) {
        return WRITE_POINT + (earlyBonus ? EARLY_BONUS : 0);
    }

    public int calcTodoDonePoints(boolean isTwo) {
        return isTwo ? TWO_DONE : NORMAL_DONE;
    }

    public boolean isEarlyBonus(LocalDateTime now) {
        return now.getHour() < 10;
    }

    public void grantTodoDonePoints(Todo todo, TodoList todoList, User user) {
        int pts = calcTodoDonePoints(todo.isTwo());
        todo.setPointsEarned(pts);
        userService.addPoints(user, pts);
        addPointsToTodoList(todoList, pts);

        if (todo.isTwo()) {
            List<Todo> twoDos = todoRepository.findByTodoListAndIsTwo(todoList, true);
            boolean allDone = twoDos.stream().allMatch(Todo::isCompleted);

            if (allDone) {
                userService.addPoints(user, TWO_ALL_DONE_BONUS);
                addPointsToTodoList(todoList, TWO_ALL_DONE_BONUS);
            }
        }
    }

    private void addPointsToTodoList(TodoList todoList, int pts) {
        todoList.setPointsEarned(todoList.getPointsEarned() + pts);
    }
}
