package com.proj.tookit.excel.excelExport;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.youth.common.exception.SystemRuntimeException;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 打包下载工具类
 *
 * @author zgp
 *
 */
public class ExportZipUtil {

    /**
     * 导出zip，默认编码UTF-8
     * @param ins 导出文件的流数组，长度与fileNames一致
     * @param innerFileNames 导出的文件在压缩文件中的名称数组，长度与ins一致，{aaa.xls,bbb.docx}，名称与ins中的文件流一一对应
     * @param zipName 导出的压缩文件名称,xxx.zip
     * @param response response
     */
    public static void download(InputStream[] ins, String[] innerFileNames, String zipName, HttpServletResponse response) {
        ExportZipUtil.download(ins, innerFileNames, zipName, null, response);
    }


    /**
     * 导出zip，指定编码
     * @param ins 导出文件的流数组，长度与fileNames一致
     * @param innerFileNames 导出的文件在压缩文件中的名称数组，长度与ins一致，{aaa.xls,bbb.docx}，名称与ins中的文件流一一对应
     * @param zipName 导出的压缩文件名称,xxx.zip
     * @param charset 指定编码 传null， 糊涂默认系统编码
     * @param response response
     */
    public static void download(InputStream[] ins, String[] innerFileNames, String zipName, Charset charset, HttpServletResponse response) {
        if(ArrayUtil.isEmpty(ins) || ArrayUtil.isEmpty(innerFileNames) || StrUtil.isBlankIfStr(zipName)){
            throw new SystemRuntimeException("参数格式异常");
        }
        // 判断文件流个数与文件名个数是否一致
        if(ins.length != innerFileNames.length){
            throw new SystemRuntimeException("参数长度异常");
        }
        FileInputStream fileInputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            zipName = java.net.URLEncoder.encode(zipName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setContentType("application/x-msdownload;charset=UTF-8");
            //临时文件，无用，但是没有的话压缩文件中无文件，方法调用后删除临时文件
            File tempFile = File.createTempFile("download",".zip");
            //默认编码UTF-8，内部文件名列表innerFileNames和流列表ins长度必须一致
            File zip = ZipUtil.zip(tempFile, innerFileNames, ins, charset);
            fileInputStream = new FileInputStream(zip);
            servletOutputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int i = 0;

            while ((i = fileInputStream.read(b)) > 0) {
                servletOutputStream.write(b, 0, i);
                servletOutputStream.flush();
            }

            for (InputStream in : ins) {
                in.close();
            }
            //删除临时文件
            if (!tempFile.delete()) {
                // 删除失败
            }
        }catch (IOException io) {
            System.out.println(io);
        } finally {
            try{
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
            }catch (IOException io) {
                System.out.println(io);
            }
        }

    }
}
