package com.collinsongroup.vendingmachine;

import static com.collinsongroup.vendingmachine.model.ProductName.COKE;
import static com.collinsongroup.vendingmachine.model.ProductName.PEPSI;
import static com.collinsongroup.vendingmachine.model.ProductName.SODA;
import java.util.Scanner;
import java.util.HashMap;
import java.util.TreeMap;

import com.collinsongroup.vendingmachine.model.Product;
import com.collinsongroup.vendingmachine.model.ProductName;

public class VendingMachineMain {

	private final static HashMap<ProductName, Product> productsInventory = new HashMap<ProductName, Product>();

	private final static TreeMap<Double, Integer> change = new TreeMap<Double, Integer>();

	private final static HashMap<Integer, ProductName> productsMenu = new HashMap<Integer, ProductName>();

	private Scanner input = new Scanner(System.in);

	private double customerAmount;

	static {
		showProductsMenu();
		loadInventory(COKE, 0.25, 30); // 30 cokes present in machine
		loadInventory(PEPSI, 0.35, 50); // 50 pepsi present in machine
		loadInventory(SODA, 0.45, 50); // 50 sodas present in machine
		loadMoneyIntoVendingMachine(); // load money to vending machine to give the exchange to customers
	}

	public static void main(String[] args) {
		VendingMachineMain machine = new VendingMachineMain();
		machine.execute();
	}

	/**
	 * Add money to vending machine for giving change to the customers
	 * 
	 * @param cat
	 * @param amount
	 */
	public static void loadMoneyIntoVendingMachine(Double cat, Integer amount) {
		if (change.get(cat) != null) {
			int oldvalue = change.get(cat);
			change.put(cat, oldvalue + amount);
		} else
			change.put(cat, amount);
	}

	public void loadProducts(ProductName name, Double price, Integer amount) {
		if (productsInventory.get(name) != null) {
			Product current = productsInventory.get(name);
			current.setQuantity(current.getQuantity() + amount);
		} else {
			Product current = new Product(name, price, amount);
			productsInventory.put(name, current);
		}
	}

	public double getMachineTotal() {
		double total = 0;
		for (Double val : change.keySet())
			total = total + (val * change.get(val));
		return total;
	}

	public static void loadMoneyIntoVendingMachine() {
		loadMoneyIntoVendingMachine(new Double(0.01), new Integer(100));
		loadMoneyIntoVendingMachine(new Double(0.02), new Integer(100));
		loadMoneyIntoVendingMachine(new Double(0.05), new Integer(100));
		loadMoneyIntoVendingMachine(new Double(0.1), new Integer(100));
		loadMoneyIntoVendingMachine(new Double(0.2), new Integer(100));
		loadMoneyIntoVendingMachine(new Double(0.5), new Integer(100));
		loadMoneyIntoVendingMachine(new Double(1.0), new Integer(100));
		loadMoneyIntoVendingMachine(new Double(2.0), new Integer(100));
	}

	private static void showProductsMenu() {
		productsMenu.put(1, COKE);
		productsMenu.put(2, PEPSI);
		productsMenu.put(3, SODA);
	}

	private static void loadInventory(ProductName name, Double price, Integer amount) {
		if (productsInventory.get(name) != null) {
			Product existingProduct = productsInventory.get(name);
			existingProduct.setQuantity(existingProduct.getQuantity() + amount);
		} else {
			Product current = new Product(name, price, amount);
			productsInventory.put(name, current);
		}
	}

	public void purchaseProduct(Integer sel, double money) {
		Product current = productsInventory.get(productsMenu.get(sel));
		double expected = current.getPrice();
		if (money >= expected) {
			if (expected - money < this.getMachineTotal()) {
				this.dispenseProduct(current);
				this.calculateChange(money - expected);
				this.customerAmount = (double) (money - expected);
			} else
				System.out.println("Sorry ! Not enough change. Aborting operation");
		} else
			System.out.format("Sorry, Insufficent funds ! Please insert %.2f ",
					(expected - money) + " to buy this Product");

	}

	public void dispenseProduct(Product newProduct) {
		newProduct.setQuantity(newProduct.getQuantity() - 1);
		System.out.println("Please take the " + newProduct.getName() + " from the drawer");
	}

	public void execute() {
		System.out.println("*** Products Menu Selection ******");
		for (Integer number : productsMenu.keySet()) {
			ProductName name = productsMenu.get(number);
			if (productsInventory.get(name) != null) {
				System.out.println(
						"Press " + (int) number + " for " + name + " price " + productsInventory.get(name).getPrice());
			}
		}
		boolean exit = false;
		while (true) {
			inputCoinsInVendingMachine();
			while (!exit) {
				System.out
						.println("Amount credited :  " + String.format("%.2f", this.customerAmount * 100) + " cents ");
				System.out.println(
						"Please enter a selection, $ to add credit, enter CANCEL for cancellation and EXIT to Quit: ");
				if (input.hasNextInt()) {
					int selection = input.nextInt();
					if (productsMenu.containsKey(selection)) {
						purchaseProduct(selection, this.customerAmount);
					} else {
						System.out.println("Enter a valid menu Product");
					}

				} else {
					String command = input.next();
					if ("$".equalsIgnoreCase(command)) {
						inputCoinsInVendingMachine();
					} else if ("CANCEL".equalsIgnoreCase(command)) {
						System.out
								.println("Refunded  " + String.format("%.2f", this.customerAmount * 100) + " cents and please collect the amount");
						System.out.println("Goodbye ! Have a nice day");
						System.exit(0);
					} else if ("EXIT".equalsIgnoreCase(command)) {
						System.out.println("Goodbye ! Have a nice day");
						System.exit(0);
					}
				}
			}
		}
	}

	private void inputCoinsInVendingMachine() {
		System.out.println(
				"Please enter coins in the form of cents (Example : 1,5,10,25 Cents), followed by * to start product (COKE/PEPSI/SODA) selection: ");
		while (input.hasNext()) {
			if (input.hasNextDouble()) {
				double inputValue = input.nextDouble();
				this.customerAmount = this.customerAmount + (inputValue / 100);
			} else if ("*".equalsIgnoreCase(input.next())) {
				break;
			}
		}
	}

	public void calculateChange(Double needed) {
		System.out.format("Change needed : %.2f", needed);
		double temp = needed;
		for (Double value : change.descendingMap().keySet()) {
			int numCoins = (int) (temp / value.doubleValue());
			temp = temp % value.doubleValue();
			if (numCoins > 0) {
				int old = change.get(value).intValue();
				if (numCoins < old) {
					change.put(value, new Integer(old - numCoins));
					System.out.println("Please take your " + numCoins + " coin of " + value);
				}
			}
		}
	}
}