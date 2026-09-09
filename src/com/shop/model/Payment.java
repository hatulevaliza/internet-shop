package com.shop.model;

public class Payment {
    private double amount;
    private String method;
    private boolean successful;

    public Payment(double amount, String method) {
        this.amount = amount;
        this.method = method;
        this.successful = false;
    }

    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public boolean isSuccessful() { return successful; }

    public void process() {
        if (amount > 0) {
            successful = true;
            System.out.println("Оплата " + amount + " через " + method + " прошла успешно");
        }
    }

    @Override
    public String toString() {
        return "Оплата: " + amount + " (" + method + ") " + (successful ? "УСПЕШНО" : "НЕ УДАЛАСЬ");
    }
}