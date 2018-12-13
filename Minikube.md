# minikube

安装virtualbox(官网下载),minikube

curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/

- minikube start
- minikube ip 查看虚拟机ip 
- minikube dashboard 
- minikube ssh 登录到虚拟机内部

minikube addons enable dashboard 开启dashboard，之后需要minikube delete & minikube start

minikube为开发或者测试在本地启动一个节点的kubernetes集群，minikube打包了和配置一个linux虚拟机、docker与kubernetes组件。


- `kubectl run --image=nginx:alpine nginx-app --port=80 `
- `kubectl run NAME --image=image [--env="key=value"][--port=port] [--replicas=replicas][--dry-run=bool] [--overrides=inline-json][--command] -- [COMMAND`][args...]
  - 启动一个deployment
  - 使用kubectl run在设定很复杂的时候需要非常长的一条语句，敲半天也很容易出错，也没法保存，在碰到转义字符的时候也经常会很抓狂，所以更多场景下会使用yaml或者json文件，而使用kubectl create或者delete就可以利用这些yaml文件
- `kubectl get pods -w` 
  - 获取pod，并监听
- `kubectl get deployments`
  - 获取deployments
- `kubectl expose deployment nginx-app --type=NodePort  --port=32143` 
  - 发布服务【也可以用kubectl create -f xx-service.yaml】
- `kubectl get services `
  - 获取服务
- ` minikube service my-nginx-app --url`
  - 获取访问服务的地址【其实就是kubectl get services中的cluster-ip:上面的port】

- `minikube dashboard`
  - 打开dashboard http://192.168.99.102:30000/#!/overview?namespace=default
- `kubectl delete services hello-minikube`
  - 删除服务
- `kubectl delete pods $pod-name`
  - 删除pod（注意deployment会保证始终有一个pod）
- `kubectl get deployments`
  - 获取deployments
- `kubectl delete deployments $deployment-name`
  - 删除deployments
- `kubectl get`
  - 类似于 `docker ps`，查询资源列表
- `kubectl get pods --all-namespaces` 
  - 获取所有namespace下的pod
- `kubectl describe` 
  - 类似于 `docker inspect`，获取资源的详细信息
- `kubectl logs` 
  - 类似于 `docker logs`，获取容器的日志
- `kubectl exec` 
  - 类似于 `docker exec`，在容器内执行一个命令
- `kubectl scale deployment $deployment_name --replicas=4`
  - 缩扩容
- `kubectl cluster-info`
  - Kubernetes master is running at https://192.168.99.102:8443
  - 查看集群信息，运行的ip，端口（这个ip和minikube ip的结果是一样的）
  - 可以用这个ip:port来访问API Server，但需要CA等权限设置
- `kubectl proxy  --port=8080`
  - 使API server监听在本地的指定端口上，绕开权限管理
  - 之后可以
    - curl http://localhost:8080/api/
    - curl http://localhost:8080/api/v1/namespaces/default/pods
- `kubectl create  –f  crd.yaml`
  - 创建一个CRD
- `kubectl get CustomResourceDefinition $crd_name  -o yaml `
  - 查看CRD是否创建成功
  - kubectl get CustomResourceDefinition mydeployments.jasper.com  -o yaml
  - kubectl delete CustomResourceDefinition mydeployments.jasper.com  -o yaml
- 创建资源同理
- `kubectl get mydeployments -o yaml` 这里get的对象是单数/复数形式的name
- 更新:replace/patch/edit
  - replace
    replace命令用于对已有资源进行更新、替换。如前面create中创建的nginx，当我们需要更新resource的一些属性的时候，如果修改副本数量，增加、修改label，更改image版本，修改端口等。都可以直接修改原yaml文件，然后执行replace命令。 
       注：名字不能被更更新。另外，如果是更新label，原有标签的pod将会与更新label后的rc断开联系，有新label的rc将会创建指定副本数的新的pod，但是默认并不会删除原来的pod。所以此时如果使用get po将会发现pod数翻倍，进一步check会发现原来的pod已经不会被新rc控制，此处只介绍命令不详谈此问题，好奇者可自行实验。 
    kubectl replace -f rc-nginx.yaml 
  - patch
    如果一个容器已经在运行，这时需要对一些容器属性进行修改，又不想删除容器，或不方便通过replace的方式进行更新。kubernetes还提供了一种在容器运行时，直接对容器进行修改的方式，就是patch命令。 
       如前面创建pod的label是app=nginx-2，如果在运行过程中，需要把其label改为app=nginx-3，这patch命令如下： 
    kubectl patch pod rc-nginx-2-kpiqt -p '{"metadata":{"labels":{"app":"nginx-3"}}}' 
  - edit `kubectl edit $resource_type/$resource_name`（根据yaml文件）
  - set `kubectl set image $resource_type/$resource_name $container_name=nginx:1.9.1`

