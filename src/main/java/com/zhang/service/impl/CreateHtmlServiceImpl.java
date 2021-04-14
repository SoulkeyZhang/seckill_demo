package com.zhang.service.impl;

import com.zhang.entity.Resp;
import com.zhang.entity.Seckill;
import com.zhang.repository.SeckillRepository;
import com.zhang.service.CreateHtmlService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Service
public class CreateHtmlServiceImpl implements CreateHtmlService {

    @Autowired
    private SeckillRepository seckillRepository;
    private static int corePoolSize = Runtime.getRuntime().availableProcessors(); // 获取系统能够使用的线程数
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,corePoolSize + 1
            ,10, TimeUnit.SECONDS,new LinkedBlockingQueue<>(1000));

    @Autowired
    private Configuration configuration; // freemaker 生成模板的配置
    @Value("${spring.freemarker.html.path}")
    private String path;

    @Override
    public Resp creatAllHtmls() {
        List<Seckill> list = seckillRepository.findAll();
        final List<Future<String>> respList = new ArrayList<>(); //
        for (Seckill seckill : list) {
            respList.add(executor.submit(new createhtml(seckill)));
        }

        for (Future<String> fs : respList) {
            try {
                System.out.println(fs.get()); // 获取各个线程任务执行的结果，调用Future.get() 阻塞主线程，获取异步任务的返回结果
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return Resp.ok();
    }

    class createhtml implements Callable<String> {
        Seckill seckill;

        public createhtml(Seckill seckill) {
            this.seckill = seckill;
        }
        @Override
        public String call() throws Exception {
            Template template = configuration.getTemplate("goods.flt");
            File file= new File(path+seckill.getSeckillId()+".html");
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            template.process(seckill, writer);
            return "success";
        }
    }

}
