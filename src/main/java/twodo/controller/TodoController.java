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
import twodo.model.TodoList;
import twodo.model.User;
import twodo.service.TodoService;
import twodo.service.UserService;

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
        model.addAttribute("hasWrittenToday", todayList != null);
        model.addAttribute("todayList", todayList);

        if (todayList != null) {
            model.addAttribute("todos", todoService.getTodos(todayList));
        }

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
        todoService.createTodoList(user, List.of(two1, two2), extras);

        return "redirect:/dashboard";
    }

    @PostMapping("/todo/{id}/complete")
    public String completeTodo(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        todoService.completeTodo(id, user);

        return "redirect:/dashboard";
    }
}