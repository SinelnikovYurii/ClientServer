package part.example.serverpart;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import jakarta.annotation.PostConstruct;


import jakarta.annotation.PreDestroy;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import part.example.serverpart.handlers.CustomHandler;
import part.example.serverpart.storage.Repository;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Server {


    @Value("${server.port}")
    private int port;

    private Channel channel;

    @PostConstruct
    public void init() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{

            PropertyConfigurator.configure("src\\main\\resources\\log4j.properties");


            Logger log4jLogger = Logger.getRootLogger();
            log4jLogger.info("Server started at port " + port);

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new CustomHandler());

                        }

                    });


            ChannelFuture future = bootstrap.bind(port).sync();

            channel = future.channel();

            readConsoleCommands();

            future.channel().closeFuture().sync();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private void readConsoleCommands(){
        Scanner scanner = new Scanner(System.in);

        String savePattern = "^save\\s+([\\w\\-.]+\\.\\w+)$";
        String loadPattern = "^load\\s+([\\w\\-.]+\\.\\w+)$";

        while (true) {
            String message = scanner.nextLine();
            Matcher matcher1 = Pattern.compile(savePattern).matcher(message);
            Matcher matcher2 = Pattern.compile(loadPattern).matcher(message);
            if(message.equals("exit")){
                shutdown();
                break;
            }else if(matcher1.matches()){

                String fileName = matcher1.group(1);

                Repository.saveToJson(fileName);

            }else if(matcher2.matches()){

                String fileName = matcher2.group(1);

                Repository.loadFromJson(fileName);

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

        System.exit(0);
    }







}
