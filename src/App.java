package com.ivin.tsh.zookeeper_test;

import com.ivin.tsh.zookeeper_test.Client;
import com.ivin.tsh.zookeeper_test.NameNode;

import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.jboss.netty.util.internal.CaseIgnoringComparator;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;



public class App{

    private ZooKeeper zk;
    private String hostPort;
    private static int INTCOUNT = 0;
    
    private static final Charset CHARSET=Charset.forName("UTF-8");
    
    public App(String hostPort) {
        this.hostPort = hostPort;
    }

    /**
     * 连接 zookeeper，并注册监听
     * @throws IOException
     */
    public void startZKServer() throws KeeperException, InterruptedException, IOException{
    	
    	System.out.println("[Server]begin connect to zookeeper...");
        zk = new ZooKeeper(hostPort, 15000, new Watcher() {
			public void process(WatchedEvent arg0) {
				System.out.println("[Server]" + arg0);
				
			}
		});
        System.out.println("[Server] zookeeper server connected");
        
        //创建父节点
        try{
	        Stat stat = zk.exists(NameNode.FATHERNODE, false);
	        String strValue = "I-am-father";
	        if(null == stat){	// 不存在父节点
	        	zk.create(NameNode.FATHERNODE, strValue.getBytes(CHARSET),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	        	System.out.println("[Server]Create node: " + NameNode.FATHERNODE + " value: " + strValue + " OK");
	        }
	        else{
	        	zk.setData(NameNode.FATHERNODE, strValue.getBytes(CHARSET), -1);
	        	System.out.println("[Server]Set node: " + NameNode.FATHERNODE + " value: " + strValue + " OK");
	        }
        }
        catch(KeeperException.SessionExpiredException e){
        	System.out.println(e);
        	throw e;
        }
        catch (KeeperException e) {
			// TODO: handle exception
        	System.out.println(e);
        	throw e;
		}
        
        while(true){
        	//System.out.println("[Server] running");
        	System.out.println("-----you can do below-----");
        	System.out.println("1.add node");
        	System.out.println("2.change the last node value");
        	System.out.println("3.delete the last node");
        	System.out.println("4.get the last node's value");
        	System.out.println("5.get father node all children");
        	
        	System.out.print("-->please input your choice:");
        	Scanner sc = new Scanner(System.in);
        	int intVal = sc.nextInt();
        	try{
        		writeData(intVal);
        	}
        	catch (Exception e) {
				System.out.println("write data Failed." );
				e.printStackTrace();
			}
        	Thread.sleep(3000);
        	//sc.close();
        }
        
    }
    
    public void writeData(int intVal) throws Exception{
    	// 获取父节点下的所有子节点
    	List<String> sChild = zk.getChildren(NameNode.FATHERNODE, false);
		int iCount = sChild.size();
		String sNode = NameNode.FATHERNODE + NameNode.SONNODE + iCount;
		
    	switch (intVal) {
		case 1:
			System.out.println("[s]Begin add node...");
			iCount += 1;
			sNode = NameNode.FATHERNODE + NameNode.SONNODE + iCount;
			while(true){
				Stat stat = zk.exists(sNode, false);
				if(null == stat){
					zk.create(sNode, "Hello".getBytes(CHARSET), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					break;
				}
				else{
					iCount += 1;
					sNode = NameNode.FATHERNODE + NameNode.SONNODE + iCount;
				}
			}
			
			System.out.println("[s]add node:" + sNode + " OK");
			break;
		case 2:
			System.out.println("[s]begin change the last node vale");
			while(iCount > 0){
				Stat stat = zk.exists(sNode, false);
				if(null == stat){
					iCount -= 1;
					sNode = NameNode.FATHERNODE + NameNode.SONNODE + iCount;
				}
				else{
					byte[] sData = zk.getData(sNode, false, stat);
					String sVal = new String(sData) + "-changed1";
					zk.setData(sNode, sVal.getBytes(CHARSET), -1);
					System.out.println("[s]change node:" + sNode +" value:" + sVal + " OK");
					break;
				}
			}
			if(iCount <= 0){
				System.out.println("[s]change node value Failed, not found any node");
			}
			break;
		case 3:
			System.out.println("[s]begin delete the last node");
			while(iCount > 0){
				Stat stat = zk.exists(sNode, false);
				if(null == stat){
					iCount -= 1;
					sNode = NameNode.FATHERNODE + NameNode.SONNODE + iCount;
				}
				else{
					zk.delete(sNode, -1);
					System.out.println("[s]delete node:" + sNode + " OK");
					break;
				}
			}
			if(iCount <= 0)
				System.out.println("[s]delete node Failed, not found any node");
			
			break;
		case 4:
			System.out.println("[s]begin get the last node's value");
			while(iCount > 0){
				Stat stat = zk.exists(sNode, false);
				if(null == stat){
					iCount -= 1;
					sNode = NameNode.FATHERNODE + NameNode.SONNODE + iCount;
				}
				else{
					byte[] sData = zk.getData(sNode, false, stat);
					String sVal = new String(sData);
					System.out.println("[s]get the node:" + sNode + " value OK, value:" + sVal);
					break;
				}
			}
			if(iCount <= 0)
				System.out.println("[s]get node value Failed, not found any node");
			break;
		case 5:
			System.out.println("[s]begin get all children node");
			System.out.println("[s]get children nodes OK, is: " + sChild.toString());
			break;
		default:
			System.out.println("[s]input invalid, try again plz");
			break;
		}
    }
    
    public static void main(String[] args) throws Exception{
    	
        String strHost = "127.0.0.1:9181,127.0.0.1:9182,127.0.0.1:9183";
        // 启动客户端线程
        Thread th1 = new Client(strHost);
        th1.start();
        
        App cli = new App(strHost);
        cli.startZKServer();
       
        System.out.println("--- end---");
    }
}
