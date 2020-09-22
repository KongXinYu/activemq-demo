package com.wzy.activemq.features.async;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.AsyncCallback;

import javax.jms.*;
import java.util.UUID;

/**
 * 对于一个Slow Consumer(即消费者处理慢，发送者发送快的场景),
 * 使用同步发送消息可能出现Producer堵塞的情况,慢消费者适合使用异步发送。
 * 对于异步发送确认消息是否成功到达，使用AsyncCallback()回调方法确认
 */
public class ProduceAsyncSend {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";
    private static final String ACTIVEMQ_QUEUE_NAME = "Queue-异步投递回调";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(ACTIVEMQ_URL);
        //开启异步投递
        activeMQConnectionFactory.setUseAsyncSend(true);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        //向上转型到ActiveMQMessageProducer
        ActiveMQMessageProducer activeMQMessageProducer = (ActiveMQMessageProducer) session.createProducer(queue);

        for (int i = 0; i < 3; i++) {
            TextMessage textMessage = session.createTextMessage("message-" + i);
            textMessage.setJMSMessageID(UUID.randomUUID().toString() + "_wzy");
            final String textMessageId = textMessage.getJMSMessageID();
            //使用ActiveMQMessageProducer的发送消息,可以创建回调
            // 用来确认异步发送成功与否
            activeMQMessageProducer.send(textMessage, new AsyncCallback() {
                @Override
                public void onSuccess() {
                    System.out.println(textMessageId + "发送成功");
                }

                @Override
                public void onException(JMSException exception) {
                    System.out.println(textMessageId + "发送失败");
                }
            });
        }
        activeMQMessageProducer.close();
        session.close();
        connection.close();
    }

}
