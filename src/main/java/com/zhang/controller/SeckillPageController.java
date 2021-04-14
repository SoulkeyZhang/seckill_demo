package com.zhang.controller;

import com.zhang.entity.Resp;
import com.zhang.entity.Seckill;
import com.zhang.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "秒伤商品")
@RestController
@RequestMapping("/seckillPage")
public class SeckillPageController {
    private final static Logger logger = LoggerFactory.getLogger(SeckillPageController.class);
    @Autowired
    private SeckillService seckillService;

    @ApiOperation(value = "秒杀商品列表",nickname = "Soulkey")
    @PostMapping("/list")
    public Resp list() {
        List<Seckill> list = seckillService.getSeckillList();
        return Resp.ok(list);
    }

}
