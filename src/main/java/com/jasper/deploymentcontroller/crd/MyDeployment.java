package com.jasper.deploymentcontroller.crd;

import com.jasper.deploymentcontroller.client.ext.PrinterColumn;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionBuilder;
import io.fabric8.kubernetes.client.CustomResource;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhoumeng
 * @version $Id: MyDeployment.java, v 0.1 2018/12/7 下午7:47 zhoumeng Exp $
 */
@Data
public class MyDeployment extends CustomResource {
    public static final String CRD_GROUP = "jasper.com";
    public static final String CRD_SINGULAR_NAME = "mydeployment";
    public static final String CRD_PLURAL_NAME = "mydeployments";
    public static final String CRD_NAME = CRD_PLURAL_NAME + "." + CRD_GROUP;
    public static final String CRD_KIND = "MyDeployment";
    public static final String CRD_SCOPE = "Namespaced";
    public static final String CRD_SHORT_NAME = "md";
    public static final String CRD_VERSION = "v1beta1";
    public static final String CRD_API_VERSION = "apiextensions.k8s.io/" + CRD_VERSION;

    public static CustomResourceDefinition MY_DEPLOYMENT_CRD = new CustomResourceDefinitionBuilder()
            .withApiVersion(CRD_API_VERSION)
            .withNewMetadata()
            .withName(CRD_NAME)
            .endMetadata()

            .withNewSpec()
            .withGroup(CRD_GROUP)
            .withVersion(CRD_VERSION)
            .withScope(CRD_SCOPE)
            .withNewNames()
            .withKind(CRD_KIND)
            .withShortNames(CRD_SHORT_NAME)
            .withSingular(CRD_SINGULAR_NAME)
            .withPlural(CRD_PLURAL_NAME)
            .endNames()
            .endSpec()

            .withNewStatus()
            .withNewAcceptedNames()
            .addToShortNames(new String[]{"availableReplicas", "replicas", "updatedReplicas"})
            .endAcceptedNames()
            .endStatus()
            .build();

    public static List<PrinterColumn> ADDITIONAL_PRINTER_COLUMNS = new ArrayList<>();

    static {
        ADDITIONAL_PRINTER_COLUMNS.add(new PrinterColumn("DESIRED", "string", "The number of desired pods", ".spec.replicas"));
        ADDITIONAL_PRINTER_COLUMNS.add(new PrinterColumn("CURRENT", "string", "Total number of non-terminated pods targeted by this deployment (their labels match the selector).", ".status.replicas"));
        ADDITIONAL_PRINTER_COLUMNS.add(new PrinterColumn("AVAILABLE", "string", "Total number of available pods (ready for at least minReadySeconds) targeted by this deployment.", ".status.availableReplicas"));
        ADDITIONAL_PRINTER_COLUMNS.add(new PrinterColumn("UP-TO-DATE", "string", "Total number of non-terminated pods targeted by this deployment that have the desired template spec.", ".status.updatedReplicas"));
        ADDITIONAL_PRINTER_COLUMNS.add(new PrinterColumn("AGE", "date", "age", ".metadata.creationTimestamp"));
    }

    @JsonProperty("spec")
    private MyDeploymentSpec spec;
    @JsonProperty("status")
    private MyDeploymentStatus status = new MyDeploymentStatus();

    public Pod createPod() {
        int hashCode = this.getSpec().getPodTemplateSpec().hashCode();
        Pod pod = new PodBuilder()
                .withNewMetadata()
                .withLabels(this.getSpec().getLabelSelector().getMatchLabels())
                .addToLabels("pod-template-hash", String.valueOf(hashCode > 0 ? hashCode : -hashCode))
                .withName(this.getMetadata().getName()
                        .concat("-")
                        .concat(UUID.randomUUID().toString()))
                .withNamespace(this.getMetadata().getNamespace())
                .withOwnerReferences(
                        new OwnerReferenceBuilder()
                                .withApiVersion(this.getApiVersion())
                                .withController(Boolean.TRUE)
                                .withBlockOwnerDeletion(Boolean.TRUE)
                                .withKind(this.getKind())
                                .withName(this.getMetadata().getName())
                                .withUid(this.getMetadata().getUid())
                                .build()
                )
                .withUid(UUID.randomUUID().toString())
                .endMetadata()
                .withSpec(this.getSpec().getPodTemplateSpec().getSpec())
                .build();
        return pod;
    }

    /**
     * 判断pod镜像是否与myDeployment不一致
     * @param pod
     * @return
     */
    public boolean isPodTemplateChanged(Pod pod) {
        List<String> images = spec.getPodTemplateSpec().getSpec().getContainers().stream().map(Container::getImage).collect(Collectors.toList());
        List<String> podImages = pod.getSpec().getContainers().stream().map(Container::getImage).collect(Collectors.toList());
        return !images.equals(podImages);
    }
}