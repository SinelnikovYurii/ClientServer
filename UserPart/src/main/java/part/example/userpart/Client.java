package part.example.userpart;

import io.netty.bootstrap.Bootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Client {

    @Value("${server.host}")
    String host;
    @Value("${server.port}")
    int port;

    private Channel channel;

    private EventLoopGroup group;

    private ScheduledExecutorService executor;


    @PostConstruct
    public void startMessaging() throws Exception {
        group = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            socketChannel.pipeline().addLast(new CustomHandler());

                        }


                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            channel = future.channel();


            readConsoleInput();

            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
            shutdown();
        }finally {
            shutdown();
        }



    }

    private void readConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        if(!channel.isActive()){
            shutdown();
        }

        while (channel != null && channel.isActive()) {
            String message = scanner.nextLine();
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(message);

                if(Performer.CheckExit(message)){
                    shutdown();
                    break;
                }
            } else {
                System.out.println("Channel is not active. Unable to send message.");
            }
        }

    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            try {
                channel.close().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        System.exit(0);
    }



}
