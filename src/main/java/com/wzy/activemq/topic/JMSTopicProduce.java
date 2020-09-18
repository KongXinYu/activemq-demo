package com.wzy.activemq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 多消费者通过负载均衡模式消费主题
 */
public class JMSTopicProduce {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";

    private static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws JMSException {
        // 1.创建连接工厂，按照给定的URL，采用默认的用户名密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2.通过连接工厂,获得connection并启动访问
        Connection connection = factory.createConnection();
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是队列queue还是主题topic)
        Topic topic = session.createTopic(TOPIC_NAME);

        // 5.创建生产者
        MessageProducer producer = session.createProducer(topic);

        // 6.通过生产者mq的topic中发送三条消息
        for (int i = 1; i <= 3; i++) {
            TextMessage message = session.createTextMessage("hello world"+i);
            producer.send(message);
        }
        // 7。关闭资源
        producer.close();
        session.close();
        connection.close();
        System.out.println("****消息发布到MQ主题消息完成");
    }

}
