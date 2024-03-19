package org.example.jdbc;

import org.example.jdbc.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
        String sql = """
                SELECT * from flightrepo.public.ticket
                """;

        try (var connection = ConnectionManager.get();
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
        try(var connection = ConnectionManager.get();
        var statement = connection.createStatement()){
            var result = statement.executeQuery(sql);
            while (result.next())
                tickets.add(result.getLong("ticket_id"));

        }
        return tickets;
    }
    public static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) {
        List<Long> flights = new ArrayList<>();
        try (var connection = ConnectionManager.get()) {
            String sql = """
                SELECT * FROM flight
                WHERE departure_time > ? and arrival_time < ?;
        """;
            var prepareStatement = connection.prepareStatement(sql);
            // Set parameters using proper format
            prepareStatement.setObject(1, start);
            prepareStatement.setObject(2, end);
            var result = prepareStatement.executeQuery();
            while (result.next()) flights.add(result.getLong("flight_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flights;
    }

    public static void checkMetaData() {
        try (Connection connection = ConnectionManager.get()){
            var metaData = connection.getMetaData();
            var catalogs = metaData.getCatalogs();
            while (catalogs.next()) System.out.println(catalogs.getString(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
