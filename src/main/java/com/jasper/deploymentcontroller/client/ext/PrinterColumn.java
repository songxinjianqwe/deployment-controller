package com.jasper.deploymentcontroller.client.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhoumeng
 * @version $Id: PrinterColumn.java, v 0.1 2018/12/9 下午3:30 zhoumeng Exp $
 */
@Data
@AllArgsConstructor
public class PrinterColumn {
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("description")
    private String description;
    @JsonProperty("JSONPath")
    private String jsonPath;
}