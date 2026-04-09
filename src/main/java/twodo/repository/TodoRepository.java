package twodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import twodo.model.Todo;
import twodo.model.TodoList;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTodoList(TodoList todoList);
    List<Todo> findByTodoListAndIsTwo(TodoList todoList, Boolean isTwo);
}