package twodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import twodo.model.TodoList;
import twodo.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    Optional<TodoList> findByUserAndDate(User user, LocalDate date);
    List<TodoList> findByUserOrderByDateDesc(User user);
    boolean existsByUserAndDate(User user, LocalDate date);
}