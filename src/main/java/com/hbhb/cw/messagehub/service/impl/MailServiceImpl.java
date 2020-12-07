package com.hbhb.cw.messagehub.service.impl;

import com.hbhb.cw.messagehub.config.MailConfig;
import com.hbhb.cw.messagehub.service.MailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaokang
 * @since 2020-09-29
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Resource
    private JavaMailSender mailSender;
    @Resource
    private MailConfig mailConfig;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendSimpleMailMessage(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
            log.info("[{}]成功发送邮件[{}][{}]到{}", sender, subject, content, to);
        } catch (Exception e) {
            log.error("发送简单邮件时发生异常!", e);
        }
    }

    @Override
    public void sendMimeMessage(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("[{}]成功发送邮件[{}][{}]到{}", sender, subject, content, to);
        } catch (MessagingException e) {
            log.error("发送MimeMessge时发生异常！", e);
        }
    }


    @Override
    public void sendMimeMessage(String to, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename();
            assert fileName != null;
            helper.addAttachment(fileName, file);

            mailSender.send(message);
            log.info("[{}]成功发送邮件[{}][{}]到{}", sender, subject, content, to);
        } catch (MessagingException e) {
            log.error("发送带附件的MimeMessge时发生异常！", e);
        }
    }

    @Override
    public void sendMimeMessage(String to, String subject, String content, Map<String, String> rscIdMap) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            for (Map.Entry<String, String> entry : rscIdMap.entrySet()) {
                FileSystemResource file = new FileSystemResource(new File(entry.getValue()));
                helper.addInline(entry.getKey(), file);
            }
            mailSender.send(message);
            log.info("[{}]成功发送邮件[{}][{}]到{}", sender, subject, content, to);
        } catch (MessagingException e) {
            log.error("发送带静态文件的MimeMessge时发生异常！", e);
        }
    }

    @Override
    public boolean send(int index, String to, String subject, String content) {
        // 多个发送邮箱轮换
        JavaMailSenderImpl sender = mailConfig.getSender(index);
        if (sender != null) {
            try {
                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
                helper.setFrom(Objects.requireNonNull(sender.getUsername()), "杭州移动财务管理系统");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content, true);
                sender.send(message);
                log.info("[{}]成功发送邮件[{}][{}]到[{}]", Objects.requireNonNull(sender.getUsername()), subject, content, to);
                return true;
            } catch (MailSendException | MessagingException | UnsupportedEncodingException e) {
                if (e instanceof MailSendException) {
                    return false;
                }
            }
        }
        return false;
    }
}
