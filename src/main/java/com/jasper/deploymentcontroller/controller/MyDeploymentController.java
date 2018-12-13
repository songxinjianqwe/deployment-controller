package com.jasper.deploymentcontroller.controller;

import com.jasper.deploymentcontroller.action.pod.UnifiedPodWatcher;
import com.jasper.deploymentcontroller.crd.DoneableMyDeployment;
import com.jasper.deploymentcontroller.crd.MyDeployment;
import com.jasper.deploymentcontroller.crd.MyDeploymentList;
import com.jasper.deploymentcontroller.action.deployment.MyDeploymentActionHandler;
import com.jasper.deploymentcontroller.client.KubeClientDelegate;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhoumeng
 * @version $Id: MyDeploymentController.java, v 0.1 2018/12/7 下午8:01 zhoumeng Exp $
 */
@Component
@Slf4j
public class MyDeploymentController {
    static {
        KubernetesDeserializer.registerCustomKind(MyDeployment.CRD_GROUP + "/" + MyDeployment.CRD_VERSION, MyDeployment.CRD_KIND, MyDeployment.class);
    }
    @Autowired
    private Map<String, MyDeploymentActionHandler> myDeploymentHandlers;
    @Autowired
    private KubeClientDelegate delegate;
    @Autowired
    private UnifiedPodWatcher unifiedPodWatcher;

    /**
     * 入口
     */
    public void run() {
        // 创建CRD
        CustomResourceDefinition myDeploymentCrd = createCrdIfNotExists();
        // 监听Pod的事件
        watchPod();
        // 监听MyDeployment的事件
        watchMyDeployment(myDeploymentCrd);
    }


    private CustomResourceDefinition createCrdIfNotExists() {
        CustomResourceDefinition myDeploymentCrd = delegate.client().customResourceDefinitions().withName(MyDeployment.CRD_NAME).get();
        if (myDeploymentCrd != null) {
            log.info("Found CRD: " + myDeploymentCrd.getMetadata().getSelfLink());
        } else {
            myDeploymentCrd = MyDeployment.MY_DEPLOYMENT_CRD;

            // TODO hack it
            // add subresources and additionalPrinterColumns
//            CustomResourceDefinitionSpecExt specWithSubResources = new CustomResourceDefinitionSpecExt();
//            BeanUtils.copyProperties(myDeploymentCrd.getSpec(),specWithSubResources);
//            SubResources subResources = new SubResources();
//            subResources.setStatus(new SubResources.Status());
//            specWithSubResources.setSubResources(subResources);
//            specWithSubResources.setAdditionalPrinterColumns(MyDeployment.ADDITIONAL_PRINTER_COLUMNS);
//            myDeploymentCrd.setSpec(specWithSubResources);

            delegate.client().customResourceDefinitions().create(myDeploymentCrd);
            log.info("Created CRD {}" , myDeploymentCrd);
        }
        return myDeploymentCrd;
    }

    private void watchMyDeployment(CustomResourceDefinition myDeploymentCrd) {
        MixedOperation<MyDeployment, MyDeploymentList, DoneableMyDeployment, Resource<MyDeployment, DoneableMyDeployment>> myDeploymentClient = delegate.client().customResources(myDeploymentCrd, MyDeployment.class, MyDeploymentList.class, DoneableMyDeployment.class);
        myDeploymentClient.watch(new Watcher<MyDeployment>() {
            @Override
            public void eventReceived(Action action, MyDeployment myDeployment) {
                log.info("myDeployment: {} => {}" , action , myDeployment);
                if(myDeploymentHandlers.containsKey(MyDeploymentActionHandler.RESOURCE_NAME + action.name())) {
                    myDeploymentHandlers.get(MyDeploymentActionHandler.RESOURCE_NAME + action.name()).handle(myDeployment);
                }
            }

            @Override
            public void onClose(KubernetesClientException e) {
                log.error("watching myDeployment {} caught an exception {}", e);
            }
        });
    }

    private void watchPod() {
        delegate.client().pods().watch(new Watcher<Pod>() {
            @Override
            public void eventReceived(Action action, Pod pod) {
                // 如果是被MyDeployment管理的Pod
                if(pod.getMetadata().getOwnerReferences().stream().anyMatch(ownerReference -> ownerReference.getKind().equals(MyDeployment.CRD_KIND))) {
                    unifiedPodWatcher.eventReceived(action, pod);
                }
            }

            @Override
            public void onClose(KubernetesClientException e) {
                log.error("watching pod {} caught an exception {}", e);
            }
        });
    }

}