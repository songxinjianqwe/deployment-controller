package com.jasper.deploymentcontroller.crd;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import lombok.Data;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentSpec.java, v 0.1 2018/12/7 下午7:48 zhoumeng Exp $
 */
@JsonDeserialize(
        using = JsonDeserializer.None.class
)
@Data
public class MyDeploymentSpec implements KubernetesResource {
    /** The number of desired pods */
    @JsonProperty("replicas")
    private Integer replicas;
    @JsonProperty("template")
    private PodTemplateSpec podTemplateSpec;
    @JsonProperty("selector")
    private LabelSelector labelSelector;
}
