package com.wzy.activemq.features.redelivery;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 多消费者轮询消息
 */
public class QueueProduce {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";

    private static final String QUEUE_NAME = "Queue-重试策略";

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

        // 5.创建生产者
        MessageProducer producer = session.createProducer(queue);

        // 6.通过生产者mq的queue中发送三条消息
        for (int i = 1; i <= 3; i++) {
            TextMessage message = session.createTextMessage("Queue-重试策略:"+i);
            producer.send(message);
        }
        // 7。关闭资源
        producer.close();
        session.close();
        connection.close();
        System.out.println("****消息发布到MQ队列完成");
    }

}
