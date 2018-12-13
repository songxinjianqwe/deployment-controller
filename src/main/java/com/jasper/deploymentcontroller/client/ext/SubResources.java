package com.jasper.deploymentcontroller.client.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhoumeng
 * @version $Id: SubResources.java, v 0.1 2018/12/9 下午3:12 zhoumeng Exp $
 */
@Data
public class SubResources {
    @JsonProperty("status")
    private Status status;

    @Data
    public static class Status {
        private String dummy;
    }
}