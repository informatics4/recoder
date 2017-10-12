import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Message {
    public Message(){
        setupConnetionFactory();
        publishToAMQP();
    }


    private BlockingQueue<String> queue = new LinkedBlockingDeque<String>();
    void publishMessage(String message){
        try{
            queue.put(message);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    ConnectionFactory factory = new ConnectionFactory();
    public void setupConnetionFactory(){
//        String uri = "amqp://ehlbpomi:AODIFxJKO0QmTUqke2_FHjy5AKKcQ5ed@wasp.rmq.cloudamqp.com/ehlbpomi";
        String uri ="localhost";
        factory.setHost(uri);
//        try {
//            factory.setAutomaticRecoveryEnabled(false);
//            factory.setUri(uri);
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
    }

    Thread subscribeThread;
    Thread publishThread;


    public void publishToAMQP(){
        publishThread = new Thread(new Runnable() {
            public void run() {
                while (true){
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.confirmSelect();
                        channel.exchangeDeclare("audioFiles", "fanout");

                        while (true){
                            String message = queue.take();
                            try{
                                channel.basicPublish("audioFiles", "audio", null, message.getBytes());
                                System.out.println("publishing..."+ message);
                                channel.waitForConfirmsOrDie();
                            }catch (Exception e){
                                queue.put(message);
                                throw e;
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Connection broken "+ e.getClass().getName());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        publishThread.start();
    }
}

