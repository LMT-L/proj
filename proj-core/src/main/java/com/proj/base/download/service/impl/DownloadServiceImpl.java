package com.proj.base.download.service.impl;

import com.proj.base.download.service.DownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 模板下载
 */
@Service
public class DownloadServiceImpl implements DownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadServiceImpl.class);

    /**
     * 公用下载模板方法
     *
     * @param request  request流
     * @param response response流
     * @throws IOException 输入输出异常
     */
    @Override
    public void commonDownload(HttpServletRequest request, HttpServletResponse response, String name, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        File sourceFile = resource.getFile();
        try (FileInputStream fis = new FileInputStream(sourceFile)) {
            //将url已转码汉字转换为正常汉字形式
            name = URLDecoder.decode(name, "utf-8");
            //获取文件流

            response.setContentType("application/x-msdownload");

            //判断浏览器类型
            String agent = request.getHeader("User-Agent").toLowerCase();
            if (agent != null && (agent.indexOf("msie") != -1 ||
                    (agent.indexOf("rv") != -1 && agent.indexOf("firefox") == -1))) {
                //识别IE浏览器
                name = URLEncoder.encode(name, "UTF-8");
            } else {
                name = new String((URLDecoder.decode(name, "UTF-8")).getBytes("UTF-8"), "ISO-8859-1");
            }

            response.addHeader("Content-Disposition", "attachment;filename=" + name);

            //输出流
            OutputStream os = response.getOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = fis.read(b)) > 0) {
                os.write(b, 0, i);
            }
            fis.close();
            os.flush();
            os.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * 公用在线浏览模板方法
     * @param request
     * @param response
     * @param name
     * @param path
     * @throws IOException
     */
    @Override
    public void commonOnline(HttpServletRequest request, HttpServletResponse response, String name, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        File sourceFile = resource.getFile();
        try (FileInputStream fis = new FileInputStream(sourceFile)) {
            //将url已转码汉字转换为正常汉字形式
            name = URLDecoder.decode(name, "utf-8");
            //获取文件mine类型
            URL u = new URL("file:///" + path);
            String contentType = u.openConnection().getContentType();
            response.setContentType(contentType);

            //判断浏览器类型
            String agent = request.getHeader("User-Agent").toLowerCase();
            if (agent != null && (agent.indexOf("msie") != -1 ||
                    (agent.indexOf("rv") != -1 && agent.indexOf("firefox") == -1))) {
                //识别IE浏览器
                name = URLEncoder.encode(name, "UTF-8");
            } else {
                name = new String((URLDecoder.decode(name, "UTF-8")).getBytes("UTF-8"), "ISO-8859-1");
            }

            //设置内嵌而不是下载
            response.setHeader("Content-Disposition", "inline;filename=" + name);

            //输出流
            OutputStream os = response.getOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = fis.read(b)) > 0) {
                os.write(b, 0, i);
            }
            fis.close();
            os.flush();
            os.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
