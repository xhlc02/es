package com.xhl.esjd.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author MuYan
 * @date 2020-04-19 19:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Content {
    private String title;
    private  String price;
    private  String img;
}
