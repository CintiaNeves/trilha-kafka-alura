package br.com.alura.ecommerce;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.Source;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
            try (KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>()) {
                try {
                    var orderId = UUID.randomUUID();
                    var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    var email = Math.random() + "@email.com";
                    var order = new Order(orderId.toString(), amount, email);

                    String body = "Thank you for your order! We are processing your order!";
                    String subject = "I don't know yet.";

                    Email emailCode = new Email(subject, body);

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                } catch (ExecutionException e) {
                    throw new ServletException(e);
                }
            }
        }
    }
}
