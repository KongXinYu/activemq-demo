package com.wzy.activemq.features.redelivery;/**
 * @description:
 * @author: Administrator
 * @time: 2020/9/22 0022 下午 1:39
 */

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

import javax.jms.*;
import java.util.Calendar;

/**
 *
 *activemq 在下列四种情况下回重发：
 * 1：Client用了transactions且再session中调用了rollback
 * 2：Client用了transactions且再调用commit之前关闭或者没有commit
 * 3：Client再CLIENT_ACKNOWLEDGE的传递模式下，session中调用了recover
 * 4 : 客户端连接超时（也许正在执行的代码花费的时间比配置的超时时间长）。
 * 每次1秒间隔，默认最大重发6次(消费者最多会消费7次)，超过的会被放进死信队列（DLQ）
 *
 * 本例演示：使用事务情况下不提交，将导致重发，并修改重发次数为2次
 *@author: WuZY
 *@time: 2020/9/22 0022 下午 1:39
 *
 */
public class ComsumerRedelivery {

    private static final String ACTIVEMQ_URL = "tcp://192.168.1.203:61616";

    private static final String QUEUE_NAME = "Queue-重试策略";

    public static void main(String[] args) throws JMSException {
        // 1.创建连接工厂，按照给定的URL，采用默认的用户名密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //修改重发次数为2次
        RedeliveryPolicy queuePolicy = new RedeliveryPolicy();
//        queuePolicy.setInitialRedeliveryDelay(0);
//        queuePolicy.setRedeliveryDelay(1000);
//        queuePolicy.setUseExponentialBackOff(false);
        queuePolicy.setMaximumRedeliveries(2);
        factory.setRedeliveryPolicy(queuePolicy);

        // 2.通过连接工厂,获得connection并启动访问
        Connection connection = factory.createConnection();
        connection.start();
        //3.创建会话session
        // createSession(true , ) 表示开启事务 如果没有提交或者出异常会回滚导致重复消费
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是队列queue还是主题topic)
        Queue queue = session.createQueue(QUEUE_NAME);

        // 5.创建消费者
        MessageConsumer consumer = session.createConsumer(queue);

        // 6.通过消费者获取mq中对应queue的消息
        while (true) {
            Message message = consumer.receive();
            if(message instanceof TextMessage){
                Calendar calendar = Calendar.getInstance();
                System.out.println(((TextMessage)message).getText());
            } else if(message == null) {
                break;
            }
        }

        // 7。关闭资源
//        session.commit();
        consumer.close();
        session.close();
        connection.close();
        System.out.println("****消息接收MQ队列完成");


    }
}
