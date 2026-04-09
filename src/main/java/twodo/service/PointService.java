package twodo.service;

import org.springframework.stereotype.Service;

@Service
public class PointService {

    public static final int WRITE_POINT = 10;
    public static final int EARLY_BONUS = 20;
    public static final int NORMAL_DONE = 30;
    public static final int TWO_DONE = 50;
    public static final int TWO_ALL_DONE_BONUS = 50;

    public int calcWritePoints(boolean earlyBonus) {
        return WRITE_POINT + (earlyBonus ? EARLY_BONUS : 0);
    }

    public int calcTodoDonePoints(boolean isTwo) {
        // Todo: TWO_ALL_DONE_BONUS 로직 책임 이동

        return isTwo ? TWO_DONE : NORMAL_DONE;
    }
}
