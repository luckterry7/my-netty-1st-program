package cn.terry.netty.my_netty_1st_program.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {
	
	
	public void bind(int port){
		//建立线程组，用于接受客户端的连接
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		
		//建立线程组，用于进行SocketChannel的网络读写
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class) //创建的channel为NioServerSocketChannel类
				.option(ChannelOption.SO_BACKLOG, 1024)//设置tcp的参数，
				.childHandler(new ChildChannelHandler());//绑定i/o事件的处理类
		
			//绑定端口，调用同步阻塞方法sysn，等待绑定操作完成
			ChannelFuture f = b.bind(port).sync();
			
			//调用sync阻塞方法，等待服务端链路关闭之后main函数才退出
			f.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	
	public static void main(String[] args) {
		new TimeServer().bind(8080);
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new TimeServerHandler());
		}
		
	}
}
