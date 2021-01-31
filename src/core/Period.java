package core;

import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * Class to represent a Period in the system.
 */
public class Period {
    private final int id;
    private final int userId;
    private final int associatedYear;
    private final String name;

    /**
     * Getter for the period id.
     *
     * @return id of the period
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for id of the user the period belongs to.
     *
     * @return userId of the period
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Getter for year number, which is associated with the period.
     *
     * @return year number of the period
     */
    public int getAssociatedYear() {
        return associatedYear;
    }

    /**
     * Getter for period name.
     *
     * @return name of the period.
     */
    public String getName() {
        return name;
    }

    /**
     * Method which goes through the database and returns the id of the period,
     * which is the last added period.
     *
     * @return id of the most recent added period. -1 if something is wrong.
     */
    protected static int getLastId() {
        int lastPeriod = -1;

        // Gets Database connection
        Connection connection = Database.getConnection();
        PreparedStatement pStatement = null;

        // Sets up the query
        String query = "SELECT * FROM Period ORDER BY id DESC LIMIT 1;";
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
        // Returns the id of last period
        return lastPeriod;
    }

    /**
     * Constructor for Period.
     * Used when a Period instance is reconstructed from the database.
     *
     * @param id id of the Period
     * @param userId id of the user the period belongs to
     * @param associatedYear year number associated with the period
     * @param name name of the period
     */
    protected Period(int id, int userId, int associatedYear, String name) {
        this.id = id;
        this.userId = userId;
        this.associatedYear = associatedYear;
        this.name = name;
    }

    /**
     * Constructor for Period.
     * Used when a Period instance is created by the User.
     *
     * @param userId id of the user
     * @param associatedYear year number associated with the period
     * @param name name of the period
     */
    public Period(int userId, int associatedYear, String name){
        this.id = 0;    // Period id not reconstructed from db/not already in db
        this.userId = userId;
        this.associatedYear = associatedYear;
        this.name = name;
    }

    /**
     * Method which adds the Period to the database.
     */
    public void addPeriod(){
        // Checks if period is not in the database
        if(id == 0){
            // Gets Database connection
            Connection connection = Database.getConnection();
            PreparedStatement pStatement = null;

            // Sets up the query
            String query = "INSERT INTO Period VALUES(null,?,?,?);";
            try {
                // Fills prepared statement and executes
                pStatement = connection.prepareStatement(query);
                pStatement.setInt(1, userId);
                pStatement.setInt(2, associatedYear);
                pStatement.setString(3, name);

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
     * Method which gets all Weeks of the Period existing in the database.
     *
     * @return List of Weeks of the Period
     */
    public List<Week> getAllWeeks(){
        ArrayList<Week> weeks = new ArrayList<>();

        // Gets Database connection
        Connection connection = Database.getConnection();
        PreparedStatement pStatement = null;
        ResultSet rs = null;

        // Sets up the query
        String query = "SELECT * FROM Week WHERE periodId = ? AND userId = ?;";
        try {
            // Fills prepared statement and executes
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, id);
            pStatement.setInt(2, userId);

            //Executes the statement, gets the result set
            rs = pStatement.executeQuery();

            // If there are items in the result set, reconstructs the Weeks and saves them in a list
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("userId");
                int weekNumber = rs.getInt("weekNumber");
                int minutesLeft = rs.getInt("minutesLeft");
                String startDate = rs.getString("startDate");

                Week newWeek = new Week(id, userId, this.id, weekNumber, startDate, minutesLeft);
                weeks.add(newWeek);
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
        // Returns a list of Week objects
        return weeks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return id == period.id && userId == period.userId && associatedYear == period.associatedYear && name.equals(period.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, associatedYear, name);
    }
}