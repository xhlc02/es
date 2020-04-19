package com.xhl.esjd.controller;

import com.xhl.esjd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author MuYan
 * @date 2020-04-19 20:05
 */
@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyWords}")
    public Boolean parse(@PathVariable String keyWords)throws Exception{
      return contentService.parseContent(keyWords);
    }
    @GetMapping("/search/{keyWords}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> serachPage(@PathVariable String keyWords,
                                               @PathVariable int pageNo,
                                               @PathVariable int pageSize)throws Exception{
        return contentService.serachHighlightPage(keyWords,pageNo,pageSize);
    }
}
