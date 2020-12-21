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
    private List<String> passwordList;
    private List<JavaMailSenderImpl> senders;
    private final MailProperties properties;

    public MailConfig(MailProperties properties) {
        this.properties = properties;

        // 初始化账号
        if (usernameList == null) {
            usernameList = new ArrayList<>();
        }
        String[] userNames = this.properties.getUsername().split(",");
        Collections.addAll(usernameList, userNames);

        // 初始化密码
        if (passwordList == null) {
            passwordList = new ArrayList<>();
        }
        String[] passwords = this.properties.getPassword().split(",");
        Collections.addAll(passwordList, passwords);

        if (senders == null) {
            senders = new ArrayList<>();
        }
    }

    @PostConstruct
    public void buildSender() {
        for (int i = 0; i < usernameList.size(); i++) {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setDefaultEncoding(this.properties.getDefaultEncoding().name());
            sender.setHost(this.properties.getHost());
            sender.setPort(this.properties.getPort());
            sender.setProtocol(this.properties.getProtocol());
            sender.setUsername(usernameList.get(i));
            sender.setPassword(passwordList.get(i));
            senders.add(sender);
        }
    }

    public JavaMailSenderImpl getSender(int index) {
        if (senders.isEmpty()) {
            buildSender();
        }
        return senders.get(index);
    }
}
