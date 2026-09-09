package com.shop.service;

public class NotificationService {

    public void sendNotification(String email, String message) {
        System.out.println("Отправка уведомления на " + email);
        System.out.println("Сообщение: " + message);
        System.out.println("Уведомление отправлено");
    }
}