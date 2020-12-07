package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {
	
	@SuppressWarnings("unused")
	private final String userId, orderId;
	@SuppressWarnings("unused")
	private final BigDecimal amount;
	
	public Order(String userId, String orderId, BigDecimal amount) {
		this.userId = userId;
		this.orderId = orderId;
		this.amount = amount;
	}

	public String getEmail() {
		return "email";
	}

	public String getUserId() {
		return userId;
	}
}
