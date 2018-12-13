package com.jasper.deploymentcontroller.crd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentStatus.java, v 0.1 2018/12/7 下午7:56 zhoumeng Exp $
 */
@Data
public class MyDeploymentStatus {
    /** Total number of available pods (ready for at least minReadySeconds) targeted by this deployment. */
    @JsonProperty("availableReplicas")
    private Integer availableReplicas;
    /** Total number of non-terminated pods targeted by this deployment that have the desired template spec. */
    @JsonProperty("updatedReplicas")
    private Integer updatedReplicas;
    /** Total number of non-terminated pods targeted by this deployment (their labels match the selector). */
    @JsonProperty("replicas")
    private Integer replicas;
}