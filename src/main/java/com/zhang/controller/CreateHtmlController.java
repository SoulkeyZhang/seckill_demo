package com.zhang.controller;

import com.zhang.entity.Resp;
import com.zhang.service.CreateHtmlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "生成静态商品页")
@RestController
@RequestMapping("/createHtml")
public class CreateHtmlController {

    private final static Logger logger = LoggerFactory.getLogger(CreateHtmlController.class);

    @Autowired
    private CreateHtmlService createHtmlService;

    @ApiOperation(value = "生成静态商品页",nickname = "Soulkey")
    @PostMapping("/start")
    public Resp start() {
        logger.info("生成秒杀活动页");
        return createHtmlService.creatAllHtmls();
    }
}
