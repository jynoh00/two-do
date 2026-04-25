package twodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import twodo.common.ErrorMessage;
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
                .orElseThrow(() -> new RuntimeException(ErrorMessage.USER_NOT_FOUND));
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
            throw new IllegalArgumentException(ErrorMessage.REQUIRED_ALL_FIELDS);
        }

        // 아이디 입력갑 검증
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_USERNAME_FORMAT);
        }

        // 패스워드 입력값 검증
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PASSWORD_FORMAT);
        }

        // 닉네임 입력값 검증
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_NICKNAME_FORMAT);
        }

        // 아이디 중복 검증
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(ErrorMessage.DUPLICATE_USERNAME);
        }

        // 닉네임 중복 검증
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException(ErrorMessage.DUPLICATE_NICKNAME);
        }

        // 패스워드 재입력 동일 검사
        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException(ErrorMessage.PASSWORD_MISMATCH);
        }
    }
}
