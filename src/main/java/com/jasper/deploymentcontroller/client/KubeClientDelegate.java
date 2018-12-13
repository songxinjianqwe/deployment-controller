package com.jasper.deploymentcontroller.client;

import com.jasper.deploymentcontroller.action.pod.PodModifiedWatcher;
import com.jasper.deploymentcontroller.action.pod.UnifiedPodWatcher;
import com.jasper.deploymentcontroller.crd.DoneableMyDeployment;
import com.jasper.deploymentcontroller.crd.MyDeployment;
import com.jasper.deploymentcontroller.crd.MyDeploymentList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author zhoumeng
 * @version $Id: KubeClient.java, v 0.1 2018/12/8 下午4:10 zhoumeng Exp $
 */
@Component
@Slf4j
public class KubeClientDelegate implements InitializingBean, PodModifiedWatcher {
    private static final String API_SRV_ADDRESS = "http://localhost:12000";
    private static final long TIME_OUT = 20L;
    private KubernetesClient client;
    /** key为pod的uid，value为count down latch */
    private ConcurrentHashMap<String, CountDownLatch> deletePodLatchMap = new ConcurrentHashMap<>();
    /** key为pod的uid，value为count down latch */
    private ConcurrentHashMap<String, CountDownLatch> modifiedPodLatchMap = new ConcurrentHashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        Config config = new ConfigBuilder()
                .withMasterUrl(API_SRV_ADDRESS)
                .build();
        client = new DefaultKubernetesClient(config);//使用默认的就足够了
        log.info("connect k8s success!");
    }

    public KubernetesClient client() {
        return client;
    }


    /**
     * @param pod
     * @return
     */
    public void countDownIfExistsByPod(Pod pod) {
        CountDownLatch latch = deletePodLatchMap.remove(pod.getMetadata().getUid());
        if(latch != null) {
            latch.countDown();
        }
    }

    /**
     * 同步删除pod
     * @param pod
     */
    public void deletePodAndWait(Pod pod) {
        client.pods().delete(pod);
        CountDownLatch latch = new CountDownLatch(1);
        log.info("put latch to pod: {}" , pod.getMetadata().getName());
        deletePodLatchMap.put(pod.getMetadata().getUid(), latch);
        // 似乎不能对同一个对象在多处watch，只有第一个地方有效。所以只能在第一个地方去做线程间通信，通知当前线程出现此事件。
        try {
            latch.await(TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("{}", e);
        }
        log.info("deletePodAndWait wait finished successfully!");
    }

    /**
     * 同步创建pod
     * @param pod
     * @param myDeployment
     */
    public void createPodAndWait(Pod pod, MyDeployment myDeployment) {
        client.pods().create(pod);
        CountDownLatch latch = new CountDownLatch(1);
        modifiedPodLatchMap.put(pod.getMetadata().getUid(), latch);
        try {
            latch.await(TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
           log.error("{}", e);
        }
        log.info("createPodAndWait wait finished successfully!");
    }

    public MyDeployment getMyDeployment(Pod pod) {
        return client
                .customResources(MyDeployment.MY_DEPLOYMENT_CRD, MyDeployment.class, MyDeploymentList.class, DoneableMyDeployment.class)
                .inNamespace(pod.getMetadata().getNamespace())
                .withName(pod.getMetadata().getOwnerReferences().get(0).getName())
                .get();
    }

    @Override
    public void onPodModified(Pod pod) {
        if (UnifiedPodWatcher.isPodReady(pod)) {
            CountDownLatch latch = modifiedPodLatchMap.remove(pod.getMetadata().getUid());
            if(latch != null) {
                latch.countDown();
            }
        }
    }
}