package twodo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import twodo.model.Todo;
import twodo.model.TodoList;
import twodo.model.User;
import twodo.service.TodoService;
import twodo.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        TodoList todayList = todoService.getTodayList(user);

        model.addAttribute("user", user);
        model.addAttribute("isToday", true);
        model.addAttribute("hasWrittenToday", todayList != null);
        model.addAttribute("todoList", todayList);

        if (todayList != null) {
            model.addAttribute("todos", todoService.getTodos(todayList));
        }

        model.addAttribute("history", todoService.getHistory(user));

        return "dashboard";
    }

    @GetMapping("/dashboard/{todoListId}")
    public String dashboardDetail(@PathVariable Long todoListId,
                                  @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        TodoList todoList = todoService.getTodoListById(todoListId);

        // user - todoList 소유 검증
        if (!todoList.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard?forbidden=true";
        }

        boolean isToday = todoList.getDate().equals(LocalDate.now());

        model.addAttribute("user", user);
        model.addAttribute("isToday", isToday);
        model.addAttribute("hasWrittenToday", todoService.hasWrittenToday(user));
        model.addAttribute("todoList", todoList);
        model.addAttribute("todos", todoService.getTodos(todoList));
        model.addAttribute("history", todoService.getHistory(user));

        return "dashboard";
    }

    @GetMapping("/write")
    public String writePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (todoService.hasWrittenToday(user)) {
            return "redirect:/dashboard?alreadyWritten=true";
        }

        model.addAttribute("user", user);
        return "write";
    }

    @PostMapping("/write")
    public String submitWrite(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam String two1,
                              @RequestParam String two2,
                              @RequestParam(required = false) List<String> extras) {
        User user = userService.findByUsername(userDetails.getUsername());
        TodoList todoList = todoService.createTodoList(user, List.of(two1, two2), extras);

        return "redirect:/dashboard/" + todoList.getId();
    }

    @PostMapping("/todo/{id}/complete")
    public String completeTodo(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Todo todo = todoService.completeTodo(id, user);

        return "redirect:/dashboard/" + todo.getTodoList().getId();
    }
}