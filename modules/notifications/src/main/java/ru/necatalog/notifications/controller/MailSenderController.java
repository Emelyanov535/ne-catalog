//package ru.necatalog.notifications.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import ru.necatalog.notifications.controller.dto.MailDto;
//import ru.necatalog.notifications.service.MailSenderService;
//
//@RestController("api/v1/notification")
//@RequiredArgsConstructor
//public class MailSenderController {
//    private final MailSenderService mailSenderService;
//
//    @PostMapping("/send")
//    public void send(@RequestBody MailDto mailDto) {
//        mailSenderService.sendMail(mailDto.getEmailTo(), mailDto.getSubject(), mailDto.getMessage());
//    }
//}
