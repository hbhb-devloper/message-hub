package com.hbhb.cw.messagehub.config;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * 实现多账号轮询发送
 *
 * @author xiaokang
 * @since 2020-12-04
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig extends JavaMailSenderImpl implements JavaMailSender {

    private List<String> usernameList;
    private List<JavaMailSenderImpl> senders;
    private final MailProperties properties;
    private Integer index = 0;

    public MailConfig(MailProperties properties) {
        this.properties = properties;

        // 初始化账号
        if (usernameList == null) {
            usernameList = new ArrayList<>();
        }
        String[] userNames = this.properties.getUsername().split(",");
        Collections.addAll(usernameList, userNames);

        if (senders == null) {
            senders = new ArrayList<>();
        }
    }

    @PostConstruct
    public void buildSender() {
        usernameList.forEach(username -> {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setDefaultEncoding(this.properties.getDefaultEncoding().name());
            sender.setHost(this.properties.getHost());
            sender.setPort(this.properties.getPort());
            sender.setProtocol(this.properties.getProtocol());
            sender.setUsername(username);
            sender.setPassword(this.properties.getPassword());
            senders.add(sender);
        });
    }

    public JavaMailSenderImpl getSender() {
        if (senders.isEmpty()) {
            buildSender();
        }
        return senders.get(index++);
    }
}