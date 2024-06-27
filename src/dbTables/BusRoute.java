package dbTables;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class BusRoute {
    private String startTime;
    private String endTime;

    public BusRoute(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Duration getDuration(String firstDepartureTime, String lastArrivalTime) {
        // Define the date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        // Parse the strings into LocalTime objects
        LocalTime startTime = LocalTime.parse(firstDepartureTime, formatter);
        LocalTime endTime = LocalTime.parse(lastArrivalTime, formatter);

        // Calculate the time difference
        return Duration.between(startTime, endTime);
    }

    public int calculateTripTime() {
        Duration duration = getDuration(getStartTime(), getEndTime());
        // Return the duration in minutes
        return (int) duration.toMinutes();
    }

    // Getters and Setters
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}