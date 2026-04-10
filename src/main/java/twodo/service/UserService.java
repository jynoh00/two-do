package twodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import twodo.model.User;
import twodo.repository.UserRepository;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,15}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$");

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9]{2,8}$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 로직
    public void register(String username, String password, String passwordCheck, String nickname) {
        // 유효성 검사
        validCheck(username, password, passwordCheck, nickname);

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();

        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // 점수 추가 로직
    public void addPoints(User user, int points) {
        user.setTotalPoints(user.getTotalPoints() + points);
        userRepository.save(user);
    }

    private void validCheck(String username, String password, String passwordCheck, String nickname) {
        // 빈 입력
        if (username == null || username.isBlank() ||
                password == null || password.isBlank() ||
                passwordCheck == null || passwordCheck.isBlank() ||
                nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("모든 항목을 입력해주세요.");
        }

        // 아이디 입력갑 검증
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("아이디는 영문 소문자, 숫자 포함 8-15로 입력해주세요.");
        }

        // 패스워드 입력값 검증
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)를 포함한 8~16자로 입력해주세요.");
        }

        // 닉네임 입력값 검증
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자 2~8자로 입력해주세요.");
        }

        // 아이디 중복 검증
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 닉네임 중복 검증
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 패스워드 재입력 동일 검사
        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
