package com.proj.base.download.service.impl;

import com.proj.base.download.service.DownloadService;
import com.youth.common.exception.SystemRuntimeException;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 模板下载
 */
@Service
public class TemplateDownloadService {

    @Resource
    private DownloadService downloadSe;

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDownloadService.class);

    public void commonDownload(HttpServletRequest request, HttpServletResponse response, String type) throws IOException {
        if (Strings.isEmpty(type)) {
            throw new SystemRuntimeException("系统异常");
        }
        Template template = Template.get(type);
        if (template == null) {
            throw new SystemRuntimeException("系统异常");
        }
        downloadSe.commonDownload(request, response, template.getFileName(), template.getPath());
    }

    /**
     * 在线浏览模板
     * @param request
     * @param response
     * @param type
     * @throws IOException
     */
    public void commonOnline(HttpServletRequest request, HttpServletResponse response, String type) throws IOException {
        if (Strings.isEmpty(type)) {
            throw new SystemRuntimeException("系统异常");
        }
        Template template = Template.get(type);
        if (template == null) {
            throw new SystemRuntimeException("系统异常");
        }
        downloadSe.commonOnline(request, response, template.getFileName(), template.getPath());
    }

    /**
     * 模板枚举
     */
    public enum Template {
        APPRAISAL("appraisal", "岗位胜任能力得分导入模板.xls", "template/import/appraisal.xls"),
        STRAIGHT_APPRAISAL("straightAppraisal", "直线经理评价导入模板.xls", "template/import/straightAppraisal.xls"),
        UNDERSTAFF("understaff", "超缺员维护导入模板.xls", "template/import/understaff.xls"),
        STAFF_ABILITY("staffAbility", "员工性格能力测评得分导入模板.xls", "template/import/staffAbility.xls");

        private String typeCode;

        private String fileName;

        private String path;

        Template(String typeCode, String fileName, String path) {
            this.typeCode = typeCode;
            this.fileName = fileName;
            this.path = path;
        }

        public String getFileName() {
            return fileName;
        }

        public String getPath() {
            return path;
        }

        private static final Map<String, Template> enumMap = new HashMap();

        static {
            Iterator i$ = EnumSet.allOf(Template.class).iterator();
            while (i$.hasNext()) {
                Template c = (Template) i$.next();
                enumMap.put(c.typeCode, c);
            }
        }

        public static Template get(String code) {
            return enumMap.get(code);
        }
    }
}
