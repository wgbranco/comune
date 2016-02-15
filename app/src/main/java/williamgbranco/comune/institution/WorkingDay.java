package williamgbranco.comune.institution;

import java.sql.Time;

/**
 * Created by William Gomes de Branco on 29/01/2016.
 */
public class WorkingDay
{
    private int dayOfTheWeek;
    private Time openingTime;
    private Time closingTime;

    public WorkingDay()
    {}

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public Time getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Time openingTime) {
        this.openingTime = openingTime;
    }

    public Time getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Time closingTime) {
        this.closingTime = closingTime;
    }
}