package com.xavier.cmpp.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Api，测试服务是否启动完成
 *
 * @author NewGr8Player
 */
@RestController
@RequestMapping("/")
public class TestApi {

    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public String test() {
        return "Success!";
    }
}
