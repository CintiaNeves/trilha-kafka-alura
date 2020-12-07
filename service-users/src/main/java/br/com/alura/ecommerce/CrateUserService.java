package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class CrateUserService {

    private final Connection connection;

    CrateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute("CREATE TABLE USERS (" +
                "UUID VARCHAR(200) PRIMARY KEY," +
                "EMAIL VARCHAR(200))");
    }

    public static void main(String[] args) throws SQLException {

        var createUserService = new CrateUserService();

        try(KafkaService<Order> service = new KafkaService<>(CrateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Order.class,
                new HashMap<>())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {

        System.out.println("----------------------------------------");
        System.out.println("Processing new order, checking for user.");
        System.out.println(record.value());
        var order = record.value();

        if(isNewUser(order.getEmail())){
            insertNewUser(order);
        }
    }

    private void insertNewUser(Order order) throws SQLException {
        var insert =  connection.prepareStatement("INSERT INTO USERS (UUID, EMAIL) VALUES (?, ?)");
        insert.setString(1, order.getUserId());
        insert.setString(2, order.getEmail());
        insert.execute();

    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("SELECT UUID FROM USERS WHERE EMAIL = ? LIMIT 1 ");
        exists.setString(1, email);

        ResultSet rs = exists.executeQuery();

        return !rs.next();
    }
}
