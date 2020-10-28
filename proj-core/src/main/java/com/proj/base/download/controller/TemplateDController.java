package com.proj.base.download.controller;



import com.proj.base.download.service.impl.TemplateDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 模板下载
 */
@RestController
@RequestMapping("/template")
public class TemplateDController {

    @Autowired
    private TemplateDownloadService tdService;


    /**
     * 模板下载
     */
    @GetMapping("/download/{type}")
    public void downloadProBudget(HttpServletRequest req, HttpServletResponse res, @PathVariable String type) throws IOException {
        tdService.commonDownload(req, res, type);
    }

    /**
     * 在线浏览模板
     */
    @GetMapping("/online/{type}")
    public void onlineManual(HttpServletRequest req, HttpServletResponse res, @PathVariable String type) throws IOException {
        tdService.commonOnline(req, res, type);
    }
}
