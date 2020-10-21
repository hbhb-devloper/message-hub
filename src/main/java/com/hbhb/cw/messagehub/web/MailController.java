package com.hbhb.cw.messagehub.web;

import com.hbhb.cw.messagehub.api.MailApi;
import com.hbhb.cw.messagehub.service.MailService;
import com.hbhb.cw.messagehub.vo.MailVO;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author xiaokang
 * @since 2020-09-28
 */
@Tag(name = "发送邮件")
@RestController
@RequestMapping("/mail")
public class MailController implements MailApi {

    @Resource
    private MailService mailService;

    @Operation(summary = "发送邮件")
    @Override
    public void postMail(MailVO vo) {
        mailService.sendMimeMessage(vo.getReceiver(), vo.getTitle(), vo.getContent());
    }
}
