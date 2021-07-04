package com.collinsongroup.vendingmachine.model;

public class Product {
	private ProductName name;
	private double price;
	private double quantity;

	public Product(ProductName name, double price, double quantity) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}

	public void setQuantity(double newQuantity) {
		quantity = newQuantity;
	}

	public double getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

	public ProductName getName() {
		return name;
	}

	public String toString() {
		return String.format("|%-18s|%-11.0f|%-11.2f|", name, quantity, price);
	}
}