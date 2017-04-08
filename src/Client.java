package com.ivin.tsh.zookeeper_test;

import com.ivin.tsh.zookeeper_test.NameNode;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;


	/**
	 * 客户端类
	 * @author ivin
	 *
	 */
	public class Client extends Thread{
		
		private String strHostPort;
		private ZooKeeper zk1;
		
		public Client(String str){
			this.strHostPort = str;
		}
		
		public void getChildren() throws Exception{

			/**
			 * 获取父节点下的数据，同时再次注册监听事件watcher
			 */
			List<String> chd = zk1.getChildren(NameNode.FATHERNODE, true);
			System.out.println("[Client]children node is: " + chd.toString());
		}
		
		/**
	     * zk 客户端
	     * @throws KeeperException
	     * @throws InterruptedExceptio
	     */
	    public void run(){
	    	
	    	System.out.println("[Client]begin connect to zookeeper...");
	    	
			try{
	    	    zk1 = new ZooKeeper(strHostPort, 15000, new Watcher() {
	    	    	public void process(WatchedEvent ev) {
	    	    		System.out.println("[Client]" + ev);
	    	    		
	    	    		if(ev.getType() == EventType.NodeChildrenChanged 
	    					&& ev.getPath().equals(NameNode.FATHERNODE)){
	    	    			System.out.println("[Client]node changed, path:" + ev.getPath());
	    	    			try {
								getChildren();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    	    		}
	    			}
	    	    	
	    	    
	    		});
	    	    System.out.println("[Client]zookeeper client connected");
	    	    
	    	    getChildren();

	        	while(true){
	        		//System.out.println("Client is running");;
	        		Thread.sleep(3000);
	        	}
			}
	    	catch (Exception e) {
				// TODO: handle exception
			}
	    }
	}

