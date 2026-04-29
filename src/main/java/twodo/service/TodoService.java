package twodo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import twodo.common.ErrorMessage;
import twodo.model.*;
import twodo.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoListRepository todoListRepository;
    private final TodoRepository todoRepository;
    private final UserService userService;
    private final PointService pointService;

    public boolean hasWrittenToday(User user) {
        return todoListRepository.existsByUserAndDate(user, LocalDate.now());
    }

    @Transactional
    public TodoList createTodoList(User user, List<String> twoContents, List<String> extraContents) {
        if (hasWrittenToday(user)) {
            throw new IllegalArgumentException(ErrorMessage.TODO_LIST_ALREADY_EXISTS);
        }

        if (twoContents == null || twoContents.size() != 2) {
            throw new IllegalArgumentException(ErrorMessage.TWO_GOALS_REQUIRED);
        }

        // 10시 이전 보너스 로직
        LocalDateTime now = LocalDateTime.now();
        boolean earlyBonus = pointService.isEarlyBonus(now);

        TodoList todoList = TodoList.builder()
                .user(user)
                .date(LocalDate.now())
                .createdAt(now)
                .earlyBonus(earlyBonus)
                .build();
        todoListRepository.save(todoList);

        // Two목표 저장
        for (String content : twoContents) {
            todoRepository.save(Todo.builder()
                    .todoList(todoList)
                    .content(content)
                    .isTwo(true)
                    .build());
        }

        // Extra목표 저장
        if (extraContents != null && !extraContents.isEmpty()) {
            // Todo Two목표 저장과 Extra목표 저장 for 루프 따로 메서드로 빼기
            for (String content : extraContents) {
                if (content != null && !content.isBlank()) {
                    todoRepository.save(Todo.builder()
                            .todoList(todoList)
                            .content(content)
                            .isTwo(false)
                            .build());
                }
            }
        }

        int pts = pointService.calcWritePoints(earlyBonus);

        // todoList 생성 및 초기 작성 점수 부여하여, 데이터베이스에 저장
        todoList.setPointsEarned(pts);
        todoListRepository.save(todoList);

        userService.addPoints(user, pts);

        return todoList;
    }

    @Transactional
    public Todo completeTodo(Long todoId, User user) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException(ErrorMessage.TODO_NOT_FOUND));

        if (todo.isCompleted()) return todo;

        TodoList todoList = todoListRepository.findById(todo.getTodoList().getId())
                .orElseThrow(() -> new RuntimeException(ErrorMessage.TODO_LIST_NOT_FOUND));

        todo.setCompleted(true);
        pointService.grantTodoDonePoints(todo, todoList, user);
        todoListRepository.save(todoList);
        todoRepository.save(todo);

        return todo;
    }

    public TodoList getTodayList(User user) {
        return todoListRepository.findByUserAndDate(user, LocalDate.now()).orElse(null);
    }

    public List<Todo> getTodos(TodoList todoList) {
        return todoRepository.findByTodoList(todoList);
    }

    public List<TodoList> getHistory(User user) {
        return todoListRepository.findByUserOrderByDateDesc(user);
    }
}
