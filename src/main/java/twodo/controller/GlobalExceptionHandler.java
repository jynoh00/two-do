package twodo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletResponse response, Model model) {
        // 완전하지 않음, NoResourceFoundException의 경우 런타임에러로 처리하도록 했어서 200 status가 반환됨
        // 추후 각 내부 예외처리에서 ResponseStatusException을 사용하도록 변경
        // todo!!: 404코드도 안 뜸, 빠른 수정할 것
        int status = response.getStatus();
        model.addAttribute("errCode", status);
        return "error/status";
    }
}
