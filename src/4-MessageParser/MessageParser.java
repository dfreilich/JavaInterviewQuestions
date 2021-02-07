package interview1;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class MessageParser {
    Queue<Object> messages;

    void test() throws InterruptedException {
        MessageFactory factory = new MessageFactory();
        while(true) {
            if(!messages.isEmpty()) {
                Object message = messages.poll();
                //todo
                MessageHandler m = factory.parse(message);
                m.send();
            } else {
                messages.wait();
            }
        }

    }

    public interface Message { }
    // EmailMessage {subject, body, toEmail, ...}
    // TextMessage {text}
    // ImageMessage {image}
    // ...
    public class EmailMessage implements Message {}
    public class TextMessage implements Message { }
    public class ImageMessage implements Message {}

    public interface MessageHandler {
        void send();
    }


    public class MessageFactory{
        private Map<Class, MessageFactory> factories;

        public MessageFactory() {
            factories = new HashMap<>();
            factories.put(EmailMessage.class, new EmailFactory());
        }

        MessageHandler parse(Object o ) {
            MessageFactory factory = factories.get(o.getClass());
            return factory.parse(o);
        }
    }

    public class EmailFactory extends MessageFactory {
        EmailMessageHandler parse(Object o) {
            return new EmailMessageHandler(o);
        }
    }

    public class EmailMessageHandler implements MessageHandler {
        private Sender sender;
        private String subject;
        private String body;
        private String to;
        private EmailMessage message;

        public EmailMessageHandler(Object o) {
            this.message = (EmailMessage) o;
        }

        public void send(){
            sender.send(message);
        }
    }

    public interface Sender<T extends Message> {
        boolean send(T e);
    }

    public class EmailSender implements Sender<EmailMessage> {
        private Queue<EmailMessage> queue;
        private Object lock;

        @Override
        public boolean send(EmailMessage em){
            // sends email
            queue.add(em);
            lock.notify();
            return true;
        }

        public boolean sendMessages() throws InterruptedException {
            while(true) {
                if(!queue.isEmpty()) {
                    EmailMessage em = queue.poll();
                    // send message
                } else {
                    lock.wait();
                }
            }
        }
    }
}
