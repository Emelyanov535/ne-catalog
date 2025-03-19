package ru.necatalog.notifications.controller.dto;

import lombok.Data;

@Data
public class MailDto {
    private String emailTo;
    private String subject;
    private String message;
}
