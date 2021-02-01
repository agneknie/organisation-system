package core;

import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Organised.
 * Copyright (c) 2021, Agne Knietaite
 * All rights reserved.
 *
 * This source code is licensed under the GNU General Public License, Version 3
 * found in the LICENSE file in the root directory of this source tree.
 *
 * Class to represent a Week in the system.
 */
public class Week {
    private final int id;
    private final int userId;
    private final int periodId;
    private final int weekNumber;
    private final LocalDate startDate;
    private int minutesLeft;

    /**
     * Getter for week id.
     * @return id of the week
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the id of the user the week belongs to.
     * @return userId of the week
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Getter for the id of the period the week belongs to.
     * @return periodId of the week
     */
    public int getPeriodId() {
        return periodId;
    }

    /**
     * Getter for the week number of the week.
     * @return weekNumber of the week
     */
    public int getWeekNumber() {
        return weekNumber;
    }

    /**
     * Getter for the start date of the week.
     * @return date of the first day (Monday) of the week
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Getter for the minutes, that are left unturned into hours for this week.
     * @return minutesLeft of the week
     */
    public int getMinutesLeft() {
        return minutesLeft;
    }

    /**
     * Setter for the minutesLeft variable of the week instance.
     * @param minutesLeft minutesLeft to set
     */
    public void setMinutesLeft(int minutesLeft) {
        this.minutesLeft = minutesLeft;
    }

    /**
     * Constructor for creating a new Week instance.
     * Used when user is adding a new Period with Weeks.
     *
     * @param userId id of the user the week belongs to
     * @param periodId id of the period the week belongs to
     * @param weekNumber number of the week
     * @param startDate date of the Monday of the week
     */
    public Week (int userId, int periodId, int weekNumber, LocalDate startDate){
        this.id = 0;    // Week is not reconstructed from db/not already in db
        this.userId = userId;
        this.periodId = periodId;
        this.weekNumber = weekNumber;
        this.startDate = startDate;
        this.minutesLeft = 0;   // No minutes left yet
    }

    /**
     * Constructor for creating a new Week instance.
     * Used when a Week instance is reconstructed from the database.
     *
     * @param id id of the week
     * @param userId id of the user the week belongs to
     * @param periodId id of the period the week belongs to
     * @param weekNumber number of the week
     * @param startDate date of the Monday of the week
     * @param minutesLeft minutes left unturned to hours for the week
     */
    public Week (int id, int userId, int periodId, int weekNumber, String startDate, int minutesLeft){
        this.id = id;
        this.userId = userId;
        this.periodId = periodId;
        this.weekNumber = weekNumber;
        this.startDate = LocalDate.parse(startDate);  // Converts string to a date
        this.minutesLeft = minutesLeft;
    }

    /**
     * Method which goes through the database and returns the id of the week,
     * which is the last added week.
     *
     * @return id of the most recent added week. -1 if something is wrong.
     */
    protected static int getLastId() {
        int lastPeriod = -1;

        // Gets Database connection
        Connection connection = Database.getConnection();
        PreparedStatement pStatement = null;

        // Sets up the query
        String query = "SELECT * FROM Week ORDER BY id DESC LIMIT 1;";
        try {
            // Executes the statement
            pStatement = connection.prepareStatement(query);
            // Gets the number of the most recent period
            lastPeriod = pStatement.executeQuery().getInt("id");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Closes the prepared statement and result set
            if (pStatement != null) {
                try {
                    pStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // Returns the id of last week
        return lastPeriod;
    }

    /**
     * Method which adds the week instance to the database along with
     * creation and addition to the database of all days belonging to this
     * week.
     */
    protected void constructWeek(){
        // Adds the week to the database
        this.addWeek();
        int weekId = Week.getLastId();

        // Creates all days of the Week and adds them to the database
        for(int i=0; i<7; i++){
            Day newDay = new Day(this.userId, weekId, this.startDate.plusDays(i));
            newDay.addDay();
        }
    }

    /**
     * Method which adds a Week to the database.
     */
    private void addWeek(){
        // Checks if week is not in the database
        if(id == 0){
            // Gets Database connection
            Connection connection = Database.getConnection();
            PreparedStatement pStatement = null;

            // Sets up the query
            String query = "INSERT INTO Week VALUES(null,?,?,?,?,?);";
            try {
                // Fills prepared statement and executes
                pStatement = connection.prepareStatement(query);
                pStatement.setInt(1, userId);
                pStatement.setInt(2, periodId);
                pStatement.setInt(3, weekNumber);
                pStatement.setInt(4, minutesLeft);
                pStatement.setString(5, startDate.toString());

                pStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Closes the prepared statement and result set
                if (pStatement != null) {
                    try {
                        pStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Method which returns all days of the Week.
     *
     * @return List of all Days belonging to this Week.
     */
    public List<Day> getAllDays(){
        ArrayList<Day> days = new ArrayList<>();

        // Gets Database connection
        Connection connection = Database.getConnection();
        PreparedStatement pStatement = null;
        ResultSet rs = null;

        // Sets up the query
        String query = "SELECT * FROM Day WHERE weekId = ? AND userId = ?;";
        try {
            // Fills prepared statement and executes
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, id);
            pStatement.setInt(2, userId);

            //Executes the statement, gets the result set
            rs = pStatement.executeQuery();

            // If there are items in the result set, reconstructs the Days and saves them in a list
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("userId");
                String date = rs.getString("date");
                int hoursSpent = rs.getInt("hoursSpent");

                Day newDay = new Day(id, userId, this.id, date, hoursSpent);
                days.add(newDay);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Closes the prepared statement and result set
            if (pStatement != null) {
                try {
                    pStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // Returns a list of Year objects
        return days;
    }

    /**
     * Method which gets all days of the week and returns the cumulative
     * number of hours spent working.
     *
     * @return hours spent working during the week
     */
    public int getAllWeekHours(){
        int hours = 0;
        for(Day day : this.getAllDays()){
            hours += day.getHoursSpent();
        }
        return hours;
    }

    /**
     * Method which deletes a Week and all of its attached Days from the database.
     *
     * @return true if successful, false otherwise
     */
    public boolean deleteWeek(){
        int rowsAffected = 0;

        // Checks if Week exists in the database
        if(this.getId() != 0) {
            // Deletes all Days of the Week
            int counter = 0;
            List <Day> days = this.getAllDays();
            for(Day day : days){
                if(day.deleteDay()) counter++;
            }

            // If day deletion was successful, deletes the week
            if (counter == days.size()){

                // Gets Database connection
                Connection connection = Database.getConnection();
                PreparedStatement pStatement = null;

                // Sets up the query
                String query = "DELETE FROM Week WHERE id = ?;";
                try {
                    // Fills prepared statement and executes
                    pStatement = connection.prepareStatement(query);
                    pStatement.setInt(1, this.getId());

                    rowsAffected = pStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    // Closes the prepared statement
                    if (pStatement != null) {
                        try {
                            pStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        // If deletion was successful, returns true
        return rowsAffected != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Week week = (Week) o;
        return id == week.id && userId == week.userId && periodId == week.periodId && weekNumber == week.weekNumber && minutesLeft == week.minutesLeft && startDate.equals(week.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, periodId, weekNumber, startDate, minutesLeft);
    }
}
