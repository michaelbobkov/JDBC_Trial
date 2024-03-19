package org.example.jdbc;

import org.example.jdbc.utils.ConnectionManager;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
        String sql = """
                SELECT * from flightrepo.public.ticket
                """;

        try (var connection = ConnectionManager.open();
            var statement = connection.createStatement()) {
            var result = statement.executeQuery(sql);
            while (result.next()){
                System.out.println(result.getLong("ticket_id"));

            }
            System.out.println(geyTicketsByFlightIf(1L));
            System.out.println(getFlightsBetween(LocalDateTime.of(2002,2, 1,2,0),
                    LocalDateTime.now()));

        }
    }

    public static List<Long> geyTicketsByFlightIf(Long flightId) throws SQLException {
        List<Long> tickets = new ArrayList<>();
        String sql = """
                SELECT * from flightrepo.public.ticket
                where flight_id = %s;
                """.formatted(flightId);
        try(var connection = ConnectionManager.open();
        var statement = connection.createStatement()){
            var result = statement.executeQuery(sql);
            while (result.next())
                tickets.add(result.getLong("ticket_id"));

        }
        return tickets;
    }
    public static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) {
        List<Long> flights = new ArrayList<>();
        try (var connection = ConnectionManager.open()) {
            String sql = """
                SELECT * FROM flight
                WHERE departure_time > ? and arrival_time < ?;
        """;
            var statement = connection.prepareStatement(sql);
            // Set parameters using proper format
            statement.setObject(1, start);
            statement.setObject(2, end);
            var result = statement.executeQuery();
            while (result.next()) flights.add(result.getLong("flight_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flights;
    }


}
