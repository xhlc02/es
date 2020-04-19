package com.xhl.esjd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MuYan
 * @date 2020-04-19 19:34
 */
@Controller
public class IndexController {
    @GetMapping({"/","/index"})
    public String index(){

        return "/index";
    }
}
