package twodo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
            throw new IllegalArgumentException("이미 투두리스트를 작성했습니다.");
        }

        if (twoContents == null || twoContents.size() != 2) {
            throw new IllegalArgumentException("Two 목표는 정확히 2개여야 합니다.");
        }

        // 10시 이전 보너스 로직
        LocalDateTime now = LocalDateTime.now();
        boolean earlyBonus = now.getHour() < 10;

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
        todoList.setPointsEarned(pts);
        todoListRepository.save(todoList);
        userService.addPoints(user, pts);

        return todoList;
    }

    @Transactional
    public Todo completeTodo(Long todoId, User user) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("투두를 찾을 수 없습니다."));

        if (todo.isCompleted()) return todo;

        TodoList todoList = todoListRepository.findById(todo.getTodoList().getId())
                        .orElseThrow(() -> new RuntimeException("투두리스트를 찾을 수 없습니다."));

        todo.setCompleted(true);
        int pts = pointService.calcTodoDonePoints(todo.isTwo());
        todo.setPointsEarned(pts);
        todoRepository.save(todo);

        userService.addPoints(user, pts);
        addPointsToTodoList(todoList, pts);

        // Todo: 포인트서비스에서 하는게 좋지 않을까
        if (todo.isTwo()) {
            List<Todo> twoDos = todoRepository.findByTodoListAndIsTwo(todo.getTodoList(), true);
            boolean allDone = twoDos.stream().allMatch(Todo::isCompleted);

            if (allDone) {
                userService.addPoints(user, PointService.TWO_ALL_DONE_BONUS);
                addPointsToTodoList(todoList, PointService.TWO_ALL_DONE_BONUS);
            }
        }

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

    public void addPointsToTodoList(TodoList todoList, int pts) {
        todoList.setPointsEarned(todoList.getPointsEarned() + pts);
        todoListRepository.save(todoList);
    }
}
