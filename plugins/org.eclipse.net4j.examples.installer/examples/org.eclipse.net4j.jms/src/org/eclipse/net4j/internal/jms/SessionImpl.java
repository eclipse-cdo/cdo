/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.internal.jms.bundle.OM;
import org.eclipse.net4j.internal.jms.messages.Messages;
import org.eclipse.net4j.internal.jms.protocol.JMSAcknowledgeRequest;
import org.eclipse.net4j.internal.jms.protocol.JMSClientMessageRequest;
import org.eclipse.net4j.internal.jms.protocol.JMSCommitRequest;
import org.eclipse.net4j.internal.jms.protocol.JMSRecoverRequest;
import org.eclipse.net4j.internal.jms.protocol.JMSRegisterConsumerRequest;
import org.eclipse.net4j.internal.jms.protocol.JMSRollbackRequest;
import org.eclipse.net4j.internal.jms.util.DestinationUtil;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.util.concurrent.QueueWorker;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.transaction.TransactionRolledbackException;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SessionImpl extends QueueWorker<MessageConsumerImpl> implements Session
{
  private ConnectionImpl connection;

  private int id;

  private boolean transacted;

  private int acknowledgeMode;

  private Map<Long, MessageConsumerImpl> consumers = new HashMap<>();

  /**
   * Outgoing transacted messages
   */
  private List<MessageImpl> messages = new ArrayList<>();

  private Set<MessageProducerImpl> producers = new HashSet<>();

  public SessionImpl(ConnectionImpl connection, int id, boolean transacted, int acknowledgeMode) throws JMSException
  {
    this.connection = connection;
    this.id = id;
    this.transacted = transacted;
    this.acknowledgeMode = acknowledgeMode;

    try
    {
      activate();
    }
    catch (Exception ex)
    {
      throw new JMSException(ex.getMessage());
    }
  }

  public ConnectionImpl getConnection()
  {
    return connection;
  }

  public int getID()
  {
    return id;
  }

  @Override
  public boolean getTransacted()
  {
    return transacted;
  }

  @Override
  public int getAcknowledgeMode()
  {
    return acknowledgeMode;
  }

  @Override
  public MessageListener getMessageListener()
  {
    return null;
  }

  @Override
  public void setMessageListener(MessageListener listener)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public MessageProducer createProducer(Destination destination) throws JMSException
  {
    DestinationImpl dest = DestinationUtil.convert(destination);
    MessageProducerImpl producer = new MessageProducerImpl(this, dest);
    producers.add(producer);
    return producer;
  }

  @Override
  public MessageConsumer createConsumer(Destination destination) throws JMSException
  {
    return createConsumer(destination, null);
  }

  @Override
  public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException
  {
    return createConsumer(destination, null, false);
  }

  @Override
  public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException
  {
    DestinationImpl dest = DestinationUtil.convert(destination);
    long consumerID = registerConsumer(dest, messageSelector, noLocal, false);
    MessageConsumerImpl consumer = new MessageConsumerImpl(this, consumerID, dest, messageSelector);
    consumers.put(consumerID, consumer);
    return consumer;
  }

  @Override
  public Queue createQueue(String queueName)
  {
    return new QueueImpl(queueName);
  }

  @Override
  public TemporaryQueue createTemporaryQueue()
  {
    return new TemporaryQueueImpl();
  }

  @Override
  public QueueBrowser createBrowser(Queue queue)
  {
    return new QueueBrowserImpl(queue);
  }

  @Override
  public QueueBrowser createBrowser(Queue queue, String messageSelector)
  {
    return new QueueBrowserImpl(queue, messageSelector);
  }

  @Override
  public Topic createTopic(String topicName)
  {
    return new TopicImpl(topicName);
  }

  @Override
  public TemporaryTopic createTemporaryTopic()
  {
    return new TemporaryTopicImpl();
  }

  @Override
  public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException
  {
    return createDurableSubscriber(topic, name, null, false);
  }

  @Override
  public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException
  {
    TopicImpl dest = (TopicImpl)DestinationUtil.convert(topic);
    long consumerID = registerConsumer(dest, messageSelector, noLocal, true);
    TopicSubscriberImpl subscriber = new TopicSubscriberImpl(this, consumerID, dest, name, messageSelector, noLocal);
    consumers.put(consumerID, subscriber);
    return subscriber;
  }

  @Override
  public void unsubscribe(String name)
  {
    throw new NotYetImplementedException();
  }

  @Override
  public Message createMessage()
  {
    return new MessageImpl();
  }

  @Override
  public StreamMessage createStreamMessage()
  {
    return new StreamMessageImpl();
  }

  @Override
  public BytesMessage createBytesMessage()
  {
    return new BytesMessageImpl();
  }

  @Override
  public MapMessage createMapMessage()
  {
    return new MapMessageImpl();
  }

  @Override
  public ObjectMessage createObjectMessage()
  {
    return createObjectMessage(null);
  }

  @Override
  public ObjectMessage createObjectMessage(Serializable object)
  {
    return new ObjectMessageImpl(object);
  }

  @Override
  public TextMessage createTextMessage()
  {
    return createTextMessage(null);
  }

  @Override
  public TextMessage createTextMessage(String text)
  {
    return new TextMessageImpl(text);
  }

  @Override
  public void recover() throws JMSException
  {
    ensureNotTransacted();
    try
    {
      stop();
      new JMSRecoverRequest(connection.getProtocol(), id).send();
      start();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      close();
    }
  }

  @Override
  public void commit() throws JMSException
  {
    ensureTransacted();
    synchronized (messages)
    {
      try
      {
        String[] messageIDs = new JMSCommitRequest(connection.getProtocol(), id, messages).send();
        if (messageIDs == null)
        {
          throw new TransactionRolledbackException(Messages.getString("SessionImpl_0")); //$NON-NLS-1$
        }

        for (int i = 0; i < messageIDs.length; i++)
        {
          messages.get(i).setJMSMessageID(messageIDs[i]);
        }

        messages.clear();
      }
      catch (JMSException ex)
      {
        throw ex;
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new JMSException(ex.getMessage());
      }
    }
  }

  @Override
  public void rollback() throws JMSException
  {
    ensureTransacted();
    synchronized (messages)
    {
      try
      {
        if (!new JMSRollbackRequest(connection.getProtocol(), id).send())
        {
          throw new JMSException(Messages.getString("SessionImpl_1")); //$NON-NLS-1$
        }

        messages.clear();
      }
      catch (JMSException ex)
      {
        throw ex;
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new JMSException(ex.getMessage());
      }
    }
  }

  @Override
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  @Override
  public void run()
  {
    throw new UnsupportedOperationException();
  }

  public long registerConsumer(DestinationImpl destination, String messageSelector, boolean noLocal, boolean durable) throws JMSException
  {
    try
    {
      return new JMSRegisterConsumerRequest(connection.getProtocol(), id, destination, messageSelector, noLocal, durable).send();
    }
    catch (Exception ex)
    {
      throw new JMSException(ex.getMessage());
    }
  }

  public void sendMessage(Message message) throws JMSException
  {
    if (getTransacted())
    {
      synchronized (messages)
      {
        if (message instanceof MessageImpl)
        {
          messages.add(MessageUtil.copy(message));
        }
        else
        {
          messages.add(MessageUtil.convert(message));
        }
      }
    }
    else
    {
      try
      {
        MessageImpl impl = MessageUtil.convert(message);
        JMSClientMessageRequest request = new JMSClientMessageRequest(connection.getProtocol(), impl);
        String messageID = request.send(connection.getSendTimeout());
        if (messageID == null)
        {
          throw new JMSException(Messages.getString("SessionImpl_2")); //$NON-NLS-1$
        }

        message.setJMSMessageID(messageID);
      }
      catch (JMSException ex)
      {
        throw ex;
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new JMSException(ex.getMessage());
      }
    }
  }

  public boolean acknowledgeMessages(MessageConsumerImpl consumer)
  {
    try
    {
      new JMSAcknowledgeRequest(connection.getProtocol(), id).sendAsync();
      return true;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return true;
    }
  }

  public void handleServerMessage(long consumerID, MessageImpl message)
  {
    message.setReceivingSession(this);
    MessageConsumerImpl consumer = consumers.get(consumerID);
    if (consumer == null)
    {
      OM.LOG.warn(MessageFormat.format(Messages.getString("SessionImpl_3"), consumerID)); //$NON-NLS-1$
      return;
    }

    consumer.handleServerMessage(message);
  }

  @Override
  protected String getThreadName()
  {
    return "jms-session"; //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  @Override
  protected void work(WorkContext context, MessageConsumerImpl consumer)
  {
    consumer.dispatchMessage();
  }

  private void ensureTransacted() throws IllegalStateException
  {
    if (!transacted)
    {
      throw new IllegalStateException("Session " + id + " not transacted"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void ensureNotTransacted() throws IllegalStateException
  {
    if (transacted)
    {
      throw new IllegalStateException("Session " + id + " transacted"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void start()
  {
  }

  private void stop()
  {
  }
}
