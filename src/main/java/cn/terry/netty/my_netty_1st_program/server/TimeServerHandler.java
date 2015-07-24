package cn.terry.netty.my_netty_1st_program.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter{

	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		//将缓存区的数组复制到新建的数组中
		buf.readBytes(req);
		
		String body = new String(req,"UTF-8");
		
		System.out.println("The time server receive message :" + body);
		
		String currentTime = "query time" .equalsIgnoreCase(body)
								? new java.util.Date(System.currentTimeMillis()).toString()
								: "Bad query";
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		//Ctx 的write方法异步发送响应信息给到客户端
		ctx.write(resp);
								
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//将消息发送队列中的信息写到socketChannel中发送给客户端,
		//实际上，为了防止频繁的唤醒selector进行消息发送，write方法先把待发送的消息放入缓存数组中，最后调用flush，把信息全部写到socketChannel中
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
}
