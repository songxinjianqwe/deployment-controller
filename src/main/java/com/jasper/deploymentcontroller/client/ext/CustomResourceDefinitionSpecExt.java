package com.jasper.deploymentcontroller.client.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionSpec;
import lombok.Data;

import java.util.List;

/**
 *
 * https://book.kubebuilder.io/basics/status_subresource.html
 * The PUT and POST verbs on objects MUST ignore the "status" values,
 * to avoid accidentally overwriting the status in read-modify-write scenarios.
 * A /status subresource MUST be provided to enable system components to update statuses of resources they manage.
 *
 * @author zhoumeng
 * @version $Id: CustomResourceDefinitionSpecExt.java, v 0.1 2018/12/9 下午3:23 zhoumeng Exp $
 */
@Data
public class CustomResourceDefinitionSpecExt extends CustomResourceDefinitionSpec {
    @JsonProperty("subresources")
    private SubResources subResources;
    @JsonProperty("additionalPrinterColumns")
    private List<PrinterColumn> additionalPrinterColumns;
}