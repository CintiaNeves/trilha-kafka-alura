package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);

        try{
            connection.createStatement().execute("CREATE TABLE USERS (" +
                    "UUID VARCHAR(200) PRIMARY KEY," +
                    "EMAIL VARCHAR(200))");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try (var service = new KafkaService(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws SQLException {

        System.out.println("----------------------------------------");
        System.out.println("Processing new order, checking for user.");
        System.out.println(record.value());
        var order = record.value();

        if(isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        }
    }

    private void insertNewUser(String email) throws SQLException {
        var insert =  connection.prepareStatement("INSERT INTO USERS (UUID, EMAIL) VALUES (?, ?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();

    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("SELECT UUID FROM USERS WHERE EMAIL = ? LIMIT 1 ");
        exists.setString(1, email);

        ResultSet rs = exists.executeQuery();

        return !rs.next();
    }
}
