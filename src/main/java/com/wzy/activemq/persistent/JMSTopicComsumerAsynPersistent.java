package com.wzy.activemq.persistent;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSTopicComsumerAsynPersistent {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";

    private static final String TOPIC_NAME = "topic_persistent";

    public static void main(String[] args) throws JMSException, InterruptedException {
        // 1.创建连接工厂，按照给定的URL，采用默认的用户名密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2.通过连接工厂,获得connection并启动访问
        Connection connection = factory.createConnection();
        connection.setClientID("topic_persistent_01");

        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是队列queue还是主题topic)
        Topic topic = session.createTopic(TOPIC_NAME);

        // 5.创建消费者
        MessageConsumer consumer = session.createDurableSubscriber(topic, "吴");

        connection.start();

        // 6.通过消费者获取mq中对应queue的消息
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                if(message != null && message instanceof TextMessage){
                    try {
                        System.out.println(((TextMessage)message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 7。关闭资源
        Thread.currentThread().suspend();
        consumer.close();
        session.close();
        connection.close();
        System.out.println("****消息接收MQ主题消息完成");
    }

}
