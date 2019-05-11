package com.example.itmoplayer;


import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.*;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


class Proxy {

    String msg;

    public String mainMain(String DbCommand, String[] user_db_properties) throws InterruptedException, TimeoutException, IOException {
        EmitNameko client = new EmitNameko();
        if (DbCommand.equals("insert_account_details")) {
            String args = String.format("{\"args\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"], \"kwargs\": {}}",
                    user_db_properties[0], user_db_properties[1], user_db_properties[2], user_db_properties[3], user_db_properties[4], user_db_properties[5]);
            msg = client.call("account_details.insert_account_details", args);
        } else if (DbCommand.equals("find_account_details")) {
            String args = String.format("{\"args\": [\"%s\", \"%s\"], \"kwargs\": {}}",
                    user_db_properties[0], user_db_properties[1]);
            msg = client.call("account_details.find_account_details", args);
        } else if (DbCommand.equals("get_account_details")) {
            String args = String.format("{\"args\": [\"%s\"], \"kwargs\": {}}", user_db_properties[0]);
            msg = client.call("account_details.get_account_details", args);
        }
        return msg;
    }


    public static void main(String[] args)
            throws IOException, InterruptedException, TimeoutException {
        EmitNameko client = new EmitNameko();
        String msg = client.call("greeting_service.hello", "{\"args\": [\"Kirill\", \"Trezubov\", \"Kirill\"], \"kwargs\": {}}");
    }
}

class EmitNameko {

    private final String EXCHANGE_NAME = "nameko-rpc";
    private String replyQueueName ;

    public  String call(String routingKey, String args)
            throws IOException, InterruptedException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.0.2.2");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
        replyQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(replyQueueName, EXCHANGE_NAME, replyQueueName);

        final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .contentType("application/json")
                .contentEncoding("utf-8")
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish( EXCHANGE_NAME, routingKey, props, args.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    String resp = new String(body, "UTF-8");
                    System.out.println(" [x] Revc '" + resp + "'");
                    response.offer(resp);
                }
            }
        });

        return response.take();


    }
    //...


}