package com.wzy.activemq.features.delay;/**
 * @description:
 * @author: Administrator
 * @time: 2020/9/22 0022 上午 11:44
 */

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;
import java.util.Calendar;

/**
 *
 * 延时发送和定时发送
 *@author: WuZY
 *@time: 2020/9/22
 *
 */
public class ProducerDelaySend {
    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";
    private static final String ACTIVEMQ_QUEUE_NAME = "Queue-延迟发送";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        //向上转型到ActiveMQMessageProducer 发送
        MessageProducer messageProducer = session.createProducer(queue);
        long delay = 3 * 1000;      //延迟发送的时间
        long period = 4 * 1000;     //每次发送的时间间隔
        int repeat = 5;                     //发送的次数

        for (int i = 0; i < 3; i++) {
            TextMessage textMessage = session.createTextMessage("message-延时发送" + i);
            //给消息设置属性以便MQ服务器读取到这些信息,好做对应的处理
            textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
            textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
            textMessage.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);
            Calendar calendar = Calendar.getInstance();
            System.out.println("发送消息的时间:"+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND));
            messageProducer.send(textMessage);
        }
        messageProducer.close();
        session.close();
        connection.close();
    }
}
