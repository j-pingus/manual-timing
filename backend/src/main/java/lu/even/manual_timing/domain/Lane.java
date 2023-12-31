package lu.even.manual_timing.domain;

import java.util.ArrayList;
import java.util.List;

public class Lane {
    private List<User> referees;
    private int laneNumber;

    public Lane(int laneNumber) {
        this.laneNumber = laneNumber;
        this.referees = new ArrayList<>();
    }

    public List<User> getReferees() {
        return referees;
    }

    public void setReferees(List<User> referees) {
        this.referees = referees;
    }

    public int getLaneNumber() {
        return laneNumber;
    }
}
