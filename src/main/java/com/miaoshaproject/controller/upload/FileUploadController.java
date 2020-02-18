package com.miaoshaproject.controller.upload;

import com.miaoshaproject.controller.BaseController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileUploadController extends BaseController {
    @PostMapping("/multiUpload")
    @ResponseBody
    public String multiUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"assets";
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                return "上传第" + (i++) + "个文件失败";
            }
            String fileName = file.getOriginalFilename();

            File dest = new File(filePath + fileName);
            try {
                file.transferTo(dest);
                System.out.println("第" + (i + 1) + "个文件上传成功");
            } catch (IOException e) {
                System.out.println(e.toString());
                return "上传第" + (i++) + "个文件失败";
            }
        }

        return "上传成功";

    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,HttpServletResponse response)throws Exception {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        String fileName = file.getOriginalFilename();
        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"assets";
        File dest = new File(filePath + fileName);
        long size = file.getSize();
        System.out.println(size);
        if(size>20*1024*1024){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"上传文件错误");
            return "上传失败";
        }

        try {
            file.transferTo(dest);
            System.out.println("上传成功");
            return "上传成功";
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return "上传失败！";
    }
    @RequestMapping("/download")
    public Object download(HttpServletResponse response, @RequestParam String fileName) {
        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"assets";
        File file = new File( filePath+fileName);
        if (file.exists()) {
            response.setContentType("application/force-download");
            // 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                return "下载成功";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return"文件不存在";
    }


}
