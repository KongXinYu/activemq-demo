package com.wzy.activemq.features.delay;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Calendar;

public class ComsumerAsyn {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";

    private static final String QUEUE_NAME = "Queue-延迟发送";

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
                    if(message instanceof TextMessage){
                        Calendar calendar = Calendar.getInstance();
                        System.out.println("收到消息的时间:"+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
                                + calendar.get(Calendar.MINUTE) + ":"
                                + calendar.get(Calendar.SECOND));
                        System.out.println(((TextMessage)message).getText());
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
