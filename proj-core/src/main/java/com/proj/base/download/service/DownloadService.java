package com.proj.base.download.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 模板下载
 */
@Service
public interface DownloadService {

    /**
     * 模板下载
     *
     * @param request
     * @param response
     * @param name
     * @param path
     * @throws IOException
     */
    void commonDownload(HttpServletRequest request, HttpServletResponse response, String name, String path) throws IOException;

    /**
     * 模板在线浏览
     * @param request
     * @param response
     * @param name
     * @param path
     * @throws IOException
     */
    void commonOnline(HttpServletRequest request, HttpServletResponse response, String name, String path) throws IOException;
}
