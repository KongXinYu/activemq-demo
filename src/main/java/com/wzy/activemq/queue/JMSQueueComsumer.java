package com.wzy.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSQueueComsumer {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";

    private static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws JMSException {
        // 1.创建连接工厂，按照给定的URL，采用默认的用户名密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2.通过连接工厂,获得connection并启动访问
        Connection connection = factory.createConnection();
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是队列queue还是主题topic)
        Queue queue = session.createQueue(QUEUE_NAME);

        // 5.创建消费者
        MessageConsumer consumer = session.createConsumer(queue);

        // 6.通过消费者获取mq中对应queue的消息
        while (true) {
            long timeout = 4000; // 一定时间内没有接收到消息，就不在阻塞，不填则一直阻塞
            TextMessage message = (TextMessage) consumer.receive(timeout);
            if (message != null) {
                System.out.println("接收到的消息："+ message.getText());
            } else {
                break;
            }
        }
        // 7。关闭资源
        consumer.close();
        session.close();
        connection.close();
        System.out.println("****消息接收MQ队列完成");
    }
}
