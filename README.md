# zookeeper-test
这是一个对 zookeeper 集群简单的测试代码工程（java maven）。

### zookeeper集群搭建
首先在机器上搭建了zookeeper 伪集群。详细的搭建可参考：[zookeeper 集群搭建](https://github.com/ivinLin/zookeeper-/blob/master/README.md)

### 基本功能
整个工程分为客户端和服务端，主进程+线程分别承载服务端和客户端功能。

服务端功能
1. add node：增加结点到集群中
2. change the last node value：更新最后一个结点的值
3. delete the last node：删除最后一个结点
4. get the last node's value：获取最后一个结点值
5. get father node all children：获取父节点下所有的子结点

客户端功能
监听父节点下的子节点变化，一旦发现更新则打印出父节点下所有的结点。
