package br.com.alura.ecommerce;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            var email = req.getParameter("email");
            var orderId = UUID.randomUUID();
            var amount = new BigDecimal(req.getParameter("amount"));

            var order = new Order(orderId.toString(), amount, email);
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

            String body = "Thank you for your order! We are processing your order!";
            String subject = "I don't know yet.";

            Email emailCode = new Email(subject, body);
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

            System.out.println("New order sent successfully");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order sent.");

        } catch (InterruptedException | ExecutionException e) {
            throw new ServletException(e);
        }

    }
}
