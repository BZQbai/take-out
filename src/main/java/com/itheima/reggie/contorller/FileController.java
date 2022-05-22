package com.itheima.reggie.contorller;

import com.itheima.reggie.common.R;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class FileController {
    @Value("${reggie.path}")
    private String bassPath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> fileLoad(MultipartFile file){
        //获取文件的名称
        String filename = file.getOriginalFilename();

        File file1 = new File(bassPath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        //取出图片名称后缀名
        String suffix = filename.substring(filename.lastIndexOf("."));
        //通过UUID生成一个随机的名称
        UUID randomUUID = UUID.randomUUID();

        filename = randomUUID + suffix;

        //将接受的文件进行转存
        try {
            file.transferTo(new File(bassPath+filename));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(filename);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void fileDownLoad(String name, HttpServletResponse response) {
        try {
            //将图片加载进输入流中
            FileInputStream inputStream = new FileInputStream(new File(bassPath + name));
            //将流中数据写到页面中
            //获取输出流
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            response.setContentType("image/png");
            while ((len=inputStream.read(bytes)) != -1) {
               outputStream.write(bytes, 0, len);
            }


            //关闭流
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
