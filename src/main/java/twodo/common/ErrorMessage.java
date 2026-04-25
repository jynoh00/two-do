package twodo.common;

public class ErrorMessage {
    public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    public static final String USERNAME_NOT_FOUND = "사용자 없음: ";
    public static final String DUPLICATE_USERNAME = "이미 사용 중인 아이디입니다.";
    public static final String DUPLICATE_NICKNAME = "이미 사용 중인 닉네임입니다.";
    public static final String PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다.";
    public static final String INVALID_CREDENTIALS = "아이디 또는 비밀번호가 올바르지 않습니다.";

    public static final String REQUIRED_ALL_FIELDS = "모든 항목을 입력해주세요.";
    public static final String INVALID_USERNAME_FORMAT = "아이디는 영문 소문자, 숫자 포함 8-15자로 입력해주세요.";
    public static final String INVALID_PASSWORD_FORMAT = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)를 포함한 8~16자로 입력해주세요.";
    public static final String INVALID_NICKNAME_FORMAT = "닉네임은 한글, 영문, 숫자 2~8자로 입력해주세요.";

    public static final String TODO_NOT_FOUND = "투두를 찾을 수 없습니다.";
    public static final String TODO_LIST_NOT_FOUND = "투두리스트를 찾을 수 없습니다.";
    public static final String TODO_LIST_ALREADY_EXISTS = "이미 투두리스트를 작성했습니다.";
    public static final String TWO_GOALS_REQUIRED = "Two 목표는 정확히 2개여야 합니다.";
}