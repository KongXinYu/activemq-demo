package com.wzy.activemq.message;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSQueueComsumerAsyn {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";

    private static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws JMSException, InterruptedException {
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
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                try {
                    if(message != null && message instanceof TextMessage){
                        System.out.println(((TextMessage)message).getText());
//                        ((TextMessage)message).getIntProperty()
                    } else if (message != null && message instanceof MapMessage) {
                        int num = ((MapMessage)message).getInt("map");
                        System.out.println("map message num: "+ num);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        // 7。关闭资源
        Thread.currentThread().suspend();
        consumer.close();
        session.close();
        connection.close();
        System.out.println("****消息接收MQ队列完成");
    }

}
