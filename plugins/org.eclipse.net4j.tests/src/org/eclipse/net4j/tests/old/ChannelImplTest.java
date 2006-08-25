/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.old;


import org.eclipse.net4j.core.Connector;
import org.eclipse.net4j.core.Executor;
import org.eclipse.net4j.core.Multiplexer;
import org.eclipse.net4j.core.Protocol;
import org.eclipse.net4j.core.impl.BufferImpl;
import org.eclipse.net4j.core.impl.ChannelImpl;
import org.eclipse.net4j.spring.Container;
import org.eclipse.net4j.spring.Service;
//import org.eclipse.net4j.spring.ValidationException;
import org.eclipse.net4j.spring.impl.ContainerImpl;
//import org.eclipse.net4j.tests.util.BlockingDetector;
import org.eclipse.net4j.tests.util.ServiceInvoker;
import org.eclipse.net4j.tests.util.TestUtils;
import org.eclipse.net4j.util.ImplementationError;

import org.easymock.MockControl;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;


public class ChannelImplTest extends TestCase
{
  protected static Container net4j = new ContainerImpl(null, (String[]) null, "net4j", null, null);

  protected ChannelImpl channel;

  protected Connector connectorMock;

  protected MockControl connectorMockControl;

  protected Executor dispatcherMock;

  protected MockControl dispatcherMockControl;

  protected Multiplexer multiplexerMock;

  protected MockControl multiplexerMockControl;

  protected Protocol protocolMock;

  protected MockControl protocolMockControl;

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
    init();
  }

  protected void init()
  {
    channel = new ChannelImpl();
    channel.setApplicationContext(net4j);
    channel.setBeanName("channel");

    connectorMockControl = MockControl.createControl(Connector.class);
    connectorMock = (Connector) connectorMockControl.getMock();

    dispatcherMockControl = MockControl.createControl(Executor.class);
    dispatcherMock = (Executor) dispatcherMockControl.getMock();

    multiplexerMockControl = MockControl.createControl(Multiplexer.class);
    multiplexerMock = (Multiplexer) multiplexerMockControl.getMock();

    protocolMockControl = MockControl.createControl(Protocol.class);
    protocolMock = (Protocol) protocolMockControl.getMock();
  }

  protected void start()
  {
    connectorMockControl.replay();
    dispatcherMockControl.replay();
    multiplexerMockControl.replay();
    protocolMockControl.replay();
  }

  protected void verify()
  {
    protocolMockControl.verify();
    multiplexerMockControl.verify();
    dispatcherMockControl.verify();
    connectorMockControl.verify();
  }

  protected BufferImpl createBuffer(int capacity, int level, boolean flipped)
  {
    BufferImpl buffer = new BufferImpl(capacity);

    if (level > 0)
    {
      buffer.put(Byte.MIN_VALUE); // for sequence-error-detection

      for (int i = 1; i < level; i++)
      {
        buffer.put((byte) (i % 256));
      }
    }

    if (flipped)
    {
      buffer.flip();
    }

    return buffer;
  }

  protected BlockingQueue<BufferImpl> createBufferQueue(int[] capacities, int[] levels,
      boolean flipped)
  {
    BlockingQueue<BufferImpl> queue = new LinkedBlockingQueue();

    for (int i = 0; i < levels.length; i++)
    {
      queue.add(createBuffer(capacities[i], levels[i], flipped));
    }

    return queue;
  }

  protected BlockingQueue<BufferImpl> createBufferQueue(int capacity, int[] levels, boolean flipped)
  {
    int[] capacities = new int[levels.length];
    Arrays.fill(capacities, capacity);
    return createBufferQueue(capacities, levels, flipped);
  }

  protected BlockingQueue<BufferImpl> createBufferQueue(int capacity, int level, boolean flipped)
  {
    return createBufferQueue(new int[] { capacity}, new int[] { level}, flipped);
  }

  public final void testSetGetChannelIndex()
  {
    channel.setChannelIndex((short) 4711);
    short result = channel.getChannelIndex();
    assertEquals("getChannelIndex returns the value of setChannelIndex", 4711, result);

    channel.setChannelIndex((short) 0);
    result = channel.getChannelIndex();
    assertEquals("getChannelIndex returns 0", 0, result);
  }

  public final void testSetGetConnector()
  {
    channel.setConnector(connectorMock);
    Connector result = channel.getConnector();
    assertEquals("getConnector returns the value of setConnector", connectorMock, result);

    channel.setConnector(null);
    result = channel.getConnector();
    assertNull("getConnector returns null", result);
  }

//  public final void testSetGetDispatcher()
//  {
//    channel.setDispatcher(dispatcherMock);
//    Executor result = channel.getDispatcher();
//    assertEquals("getDispatcher returns the value of setDispatcher", dispatcherMock, result);
//
//    channel.setDispatcher(null);
//    result = channel.getDispatcher();
//    assertNull("getDispatcher returns null", result);
//  }

  public final void testSetGetMultiplexer()
  {
    channel.setMultiplexer(multiplexerMock);
    Multiplexer result = channel.getMultiplexer();
    assertEquals("getBufferPool returns the value of setMultiplexer", multiplexerMock, result);

    channel.setMultiplexer(null);
    result = channel.getMultiplexer();
    assertNull("getMultiplexer returns null", result);
  }

  public final void testSetGetProtocol()
  {
    channel.setProtocol(protocolMock);
    Protocol result = channel.getProtocol();
    assertEquals("getProtocol returns the value of setProtocol", protocolMock, result);

    channel.setProtocol(null);
    result = channel.getProtocol();
    assertNull("getProtocol returns null", result);
  }

//  public final void testSetGetInternalReturnValue()
//  {
//    Object returnValue = new Object();
//
//    channel.internalSetReturnValue(returnValue);
//    Object result = channel.testGetReturnValue();
//    assertEquals("testGetReturnValue returns the value of internalSetReturnValue", returnValue,
//        result);
//
//    channel.internalSetReturnValue(null);
//    result = channel.testGetReturnValue();
//    assertNull("testGetReturnValue returns null", result);
//  }

  public final void testSetGetProtocolData()
  {
    Object protocolData = new Object();

    channel.setProtocolData(protocolData);
    Object result = channel.getProtocolData();
    assertEquals("getProtocolData returns the value of setProtocolData", protocolData, result);

    channel.setProtocolData(null);
    result = channel.getProtocolData();
    assertNull("getProtocolData returns null", result);
  }

//  public final void testServiceGuards()
//  {
//    new ServiceInvoker(channel)
//    {
//      protected void invokeService(Service bean) throws Exception
//      {
//        ((ChannelImpl) bean).handleTransmission();
//      }
//    };
//
//    new ServiceInvoker(channel)
//    {
//      protected void invokeService(Service bean) throws Exception
//      {
//        ((ChannelImpl) bean).transmit(null);
//      }
//    };
//
//    new ServiceInvoker(channel)
//    {
//      protected void invokeService(Service bean) throws Exception
//      {
//        ((ChannelImpl) bean).transmit(null);
//      }
//    };
//  }

//  public final void testValidate()
//  {
//    channel.setDispatcher(dispatcherMock);
//    channel.setMultiplexer(multiplexerMock);
//    channel.testValidate();
//
//    try
//    {
//      channel.setDispatcher(null);
//      channel.setMultiplexer(multiplexerMock);
//      channel.testValidate();
//      fail("expected ValidationException");
//    }
//    catch (ValidationException expected)
//    {
//      TestUtils.assertContains(expected, "dispatcher");
//    }
//
//    try
//    {
//      channel.setDispatcher(dispatcherMock);
//      channel.setMultiplexer(null);
//      channel.testValidate();
//      fail("expected ValidationException");
//    }
//    catch (ValidationException expected)
//    {
//      TestUtils.assertContains(expected, "multiplexer");
//    }
//  }

  /**
   * PRECONDITION: 	dataSize == 0
   * 
   * EXPECTATION: 	doesn't block; 
   * 				throws IllegalArgumentException
   */
//  public final void testEnsureReceiverBuffer1() throws Throwable
//  {
//    BlockingQueue<BufferImpl> receiverQueue = new LinkedBlockingQueue();
//    channel.testSetReceiverQueue(receiverQueue);
//
//    start();
//    try
//    {
//      new BlockingDetector(channel, receiverQueue, false)
//      {
//        protected void blockableOperation(Object target) throws Exception
//        {
//          ((ChannelImpl) target).testEnsureReceiverBufferData(0);
//        }
//      };
//      fail("IllegalArgumentException expected");
//    }
//    catch (IllegalArgumentException expected)
//    {
//      TestUtils.assertContains(expected, "dataSize");
//    }
//    verify();
//  }

  /**
   * PRECONDITION: 	dataSize < 0
   * 
   * EXPECTATION: 	doesn't block; 
   * 				throws IllegalArgumentException
   */
//  public final void testEnsureReceiverBuffer2() throws Throwable
//  {
//    BlockingQueue<BufferImpl> receiverQueue = new LinkedBlockingQueue();
//    channel.testSetReceiverQueue(receiverQueue);
//
//    start();
//    try
//    {
//      new BlockingDetector(channel, receiverQueue, false)
//      {
//        protected void blockableOperation(Object target) throws Exception
//        {
//          ((ChannelImpl) target).testEnsureReceiverBufferData(-1);
//        }
//      };
//      fail("IllegalArgumentException expected");
//    }
//    catch (IllegalArgumentException expected)
//    {
//      TestUtils.assertContains(expected, "dataSize");
//    }
//    verify();
//  }

  /**
   * PRECONDITION: 	receiverBuffer exists; 
   * 				0 < level < dataSize
   * 
   * EXPECTATION: 	doesn't block; 
   * 				throws ImplementationError
   */
//  public final void testEnsureReceiverBuffer3() throws Throwable
//  {
//    BufferImpl receiverBuffer = createBuffer(20, 3, true);
//    channel.testSetReceiverBuffer(receiverBuffer);
//
//    BlockingQueue<BufferImpl> receiverQueue = new LinkedBlockingQueue();
//    channel.testSetReceiverQueue(receiverQueue);
//
//    start();
//    try
//    {
//      new BlockingDetector(channel, receiverQueue, false)
//      {
//        protected void blockableOperation(Object target) throws Exception
//        {
//          ((ChannelImpl) target).testEnsureReceiverBufferData(7);
//        }
//      };
//      fail("ImplementationError expected");
//    }
//    catch (ImplementationError expected)
//    {
//      TestUtils.assertContains(expected, "receiverBuffer level too low");
//    }
//    verify();
//  }

  /**
   * PRECONDITION: 	receiverBuffer exists; 
   * 				0 < dataSize < level
   * 
   * EXPECTATION: 	doesn't block; 
   * 				receiverBuffer not returned to bufferPool
   */
//  public final void testEnsureReceiverBuffer4() throws Throwable
//  {
//    BufferImpl receiverBuffer = createBuffer(20, 12, true);
//    channel.testSetReceiverBuffer(receiverBuffer);
//
//    BlockingQueue<BufferImpl> receiverQueue = new LinkedBlockingQueue();
//    channel.testSetReceiverQueue(receiverQueue);
//
//    start();
//    new BlockingDetector(channel, receiverQueue, false)
//    {
//      protected void blockableOperation(Object target) throws Exception
//      {
//        ((ChannelImpl) target).testEnsureReceiverBufferData(7);
//      }
//    };
//    verify();
//  }

  /**
   * PRECONDITION: 	receiverBuffer exists; 
   * 				0 < dataSize == level
   * 
   * EXPECTATION: 	doesn't block; 
   * 				receiverBuffer not returned to bufferPool
   */
//  public final void testEnsureReceiverBuffer5() throws Throwable
//  {
//    BufferImpl receiverBuffer = createBuffer(20, 12, true);
//    channel.testSetReceiverBuffer(receiverBuffer);
//
//    BlockingQueue<BufferImpl> receiverQueue = new LinkedBlockingQueue();
//    channel.testSetReceiverQueue(receiverQueue);
//
//    start();
//    new BlockingDetector(channel, receiverQueue, false)
//    {
//      protected void blockableOperation(Object target) throws Exception
//      {
//        ((ChannelImpl) target).testEnsureReceiverBufferData(12);
//      }
//    };
//    verify();
//  }

  /**
   * PRECONDITION: 	receiverBuffer exists; 
   * 				0 == level < dataSize; 
   * 				receiverQueue empty
   * 
   * EXPECTATION: 	receiverBuffer returned to bufferPool; 
   * 				blocks
   */
//  public final void testEnsureReceiverBuffer6() throws Throwable
//  {
//    BufferImpl receiverBuffer = createBuffer(20, 0, true);
//    channel.testSetReceiverBuffer(receiverBuffer);
//
//    BlockingQueue<BufferImpl> receiverQueue = new LinkedBlockingQueue();
//    channel.testSetReceiverQueue(receiverQueue);
//
//    connectorMock.releaseBuffer(receiverBuffer);
//    channel.setConnector(connectorMock);
//
//    start();
//    new BlockingDetector(channel, receiverQueue, true)
//    {
//      protected void blockableOperation(Object target) throws Exception
//      {
//        ((ChannelImpl) target).testEnsureReceiverBufferData(7);
//      }
//    };
//    verify();
//  }

  /**
   * PRECONDITION: 	receiverBuffer exists; 
   * 				0 == level < dataSize; 
   * 				receiverQueue has too small buffer
   * 
   * EXPECTATION: 	doesn't block; 
   * 				receiverBuffer returned to bufferPool
   * 				throws ImplementationError
   */
//  public final void testEnsureReceiverBuffer7() throws Throwable
//  {
//    BufferImpl receiverBuffer = createBuffer(20, 0, true);
//    channel.testSetReceiverBuffer(receiverBuffer);
//
//    BlockingQueue<BufferImpl> receiverQueue = createBufferQueue(20, 5, true);
//    channel.testSetReceiverQueue(receiverQueue);
//
//    connectorMock.releaseBuffer(receiverBuffer);
//    channel.setConnector(connectorMock);
//
//    start();
//    try
//    {
//      new BlockingDetector(channel, receiverQueue, false)
//      {
//        protected void blockableOperation(Object target) throws Exception
//        {
//          ((ChannelImpl) target).testEnsureReceiverBufferData(7);
//        }
//      };
//      fail("ImplementationError expected");
//    }
//    catch (ImplementationError expected)
//    {
//      TestUtils.assertContains(expected, "receiverBuffer level too low");
//    }
//    verify();
//  }

  /**
   * PRECONDITION: 	receiverBuffer exists; 
   * 				0 == level < dataSize; 
   * 				receiverQueue has exact buffer
   * 
   * EXPECTATION: 	doesn't block; 
   * 				receiverBuffer returned to bufferPool
   */
//  public final void testEnsureReceiverBuffer8() throws Throwable
//  {
//    BufferImpl receiverBuffer = createBuffer(20, 0, true);
//    channel.testSetReceiverBuffer(receiverBuffer);
//
//    BlockingQueue<BufferImpl> receiverQueue = createBufferQueue(20, 7, true);
//    channel.testSetReceiverQueue(receiverQueue);
//
//    connectorMock.releaseBuffer(receiverBuffer);
//    channel.setConnector(connectorMock);
//
//    start();
//    new BlockingDetector(channel, receiverQueue, false)
//    {
//      protected void blockableOperation(Object target) throws Exception
//      {
//        ((ChannelImpl) target).testEnsureReceiverBufferData(7);
//      }
//    };
//    verify();
//  }

  /**
   * PRECONDITION: 	receiverBuffer exists; 
   * 				0 == level < dataSize; 
   * 				receiverQueue has too big buffer
   * 
   * EXPECTATION: 	doesn't block; 
   * 				receiverBuffer returned to bufferPool
   */
//  public final void testEnsureReceiverBuffer9() throws Throwable
//  {
//    BufferImpl receiverBuffer = createBuffer(20, 0, true);
//    channel.testSetReceiverBuffer(receiverBuffer);
//
//    BlockingQueue<BufferImpl> receiverQueue = createBufferQueue(20, 12, true);
//    channel.testSetReceiverQueue(receiverQueue);
//
//    connectorMock.releaseBuffer(receiverBuffer);
//    channel.setConnector(connectorMock);
//
//    start();
//    new BlockingDetector(channel, receiverQueue, false)
//    {
//      protected void blockableOperation(Object target) throws Exception
//      {
//        ((ChannelImpl) target).testEnsureReceiverBufferData(7);
//      }
//    };
//    verify();
//  }

  /**
   * PRECONDITION: 	transmitterBuffer doesn't exists
   * 
   * EXPECTATION: 	throws ImplementationError
   */
//  public final void testFlush1()
//  {
//    start();
//    try
//    {
//      channel.flush();
//      fail("ImplementationError expected");
//    }
//    catch (ImplementationError expected)
//    {
//      TestUtils.assertContains(expected, "transmitterBuffer must exist");
//    }
//    verify();
//  }

  /**
   * PRECONDITION: 	transmitterBuffer exists;
   * 				level == 0 
   * 
   * EXPECTATION: 	immediately returns
   */
//  public final void testFlush2()
//  {
//    BufferImpl transmitterBuffer = createBuffer(20, 0, false);
//    channel.testSetTransmitterBuffer(transmitterBuffer);
//
//    start();
//    channel.flush();
//    verify();
//  }

  /**
   * PRECONDITION: 	transmitterBuffer exists;
   * 				level > 0 
   * 
   * EXPECTATION: 	transmitterBuffer is flipped;
   * 				appended to transmitterQueue;
   * 				channel is scheduled for transmission;
   * 				a new transmitterBuffer is fetched from the bufferPool;
   * 				the new transmitterBuffer is empty
   */
//  public final void testFlush3()
//  {
//    BufferImpl transmitterBuffer = createBuffer(20, 7, false);
//    channel.testSetTransmitterBuffer(transmitterBuffer);
//
//    BlockingQueue<BufferImpl> transmitterQueue = new LinkedBlockingQueue();
//    channel.testSetTransmitterQueue(transmitterQueue);
//
//    multiplexerMock.schedule(channel);
//    channel.setMultiplexer(multiplexerMock);
//
//    connectorMockControl.expectAndReturn(connectorMock.provideBuffer(), createBuffer(10, 10, true));
//    channel.setConnector(connectorMock);
//
//    start();
//    channel.flush();
//    verify();
//
//    assertTrue("transmitterBuffer is flipped", transmitterBuffer.position() == 0);
//    assertTrue("transmitterBuffer is in transmitterQueue",
//        transmitterQueue.peek() == transmitterBuffer);
//    assertNotSame("a new transmitterBuffer is expected", transmitterBuffer, channel
//        .testGetTransmitterBuffer());
//    assertTrue("the new transmitterBuffer must be empty", channel.testGetTransmitterBuffer()
//        .position() < channel.testGetTransmitterBuffer().limit()); // TODO test emptyness
//  }

  //  public final void testReceiveBoolean()
  //  {
  //    throw new ImplementationError("Implement receiveBoolean()");
  //  }
  //
  //  public final void testReceiveByte()
  //  {
  //    throw new ImplementationError("Implement receiveByte()");
  //  }
  //
  //  public final void testReceiveChar()
  //  {
  //    throw new ImplementationError("Implement receiveChar()");
  //  }
  //
  //  public final void testReceiveDouble()
  //  {
  //    throw new ImplementationError("Implement receiveDouble()");
  //  }
  //
  //  public final void testReceiveFloat()
  //  {
  //    throw new ImplementationError("Implement receiveFloat()");
  //  }
  //
  //  public final void testReceiveInt()
  //  {
  //    throw new ImplementationError("Implement receiveInt()");
  //  }
  //
  //  public final void testReceiveLong()
  //  {
  //    throw new ImplementationError("Implement receiveLong()");
  //  }
  //
  //  public final void testReceiveObject()
  //  {
  //    throw new ImplementationError("Implement receiveObject()");
  //  }
  //
  //  public final void testReceiveShort()
  //  {
  //    throw new ImplementationError("Implement receiveShort()");
  //  }
  //
  //  public final void testReceiveString()
  //  {
  //    throw new ImplementationError("Implement receiveString()");
  //  }
  //
  //  public final void testTransmitBoolean()
  //  {
  //    throw new ImplementationError("Implement transmitBoolean()");
  //  }
  //
  //  public final void testTransmitByte()
  //  {
  //    throw new ImplementationError("Implement transmitByte()");
  //  }
  //
  //  public final void testTransmitChar()
  //  {
  //    throw new ImplementationError("Implement transmitChar()");
  //  }
  //
  //  public final void testTransmitDouble()
  //  {
  //    throw new ImplementationError("Implement transmitDouble()");
  //  }
  //
  //  public final void testTransmitFloat()
  //  {
  //    throw new ImplementationError("Implement transmitFloat()");
  //  }
  //
  //  public final void testTransmitInt()
  //  {
  //    throw new ImplementationError("Implement transmitInt()");
  //  }
  //
  //  public final void testTransmitLong()
  //  {
  //    throw new ImplementationError("Implement transmitLong()");
  //  }
  //
  //  public final void testTransmitObject()
  //  {
  //    throw new ImplementationError("Implement transmitObject()");
  //  }
  //
  //  public final void testTransmitShort()
  //  {
  //    throw new ImplementationError("Implement transmitShort()");
  //  }
  //
  //  public final void testTransmitString()
  //  {
  //    throw new ImplementationError("Implement transmitString()");
  //  }
  //
  //  public final void testNotifyFinished()
  //  {
  //    throw new ImplementationError("Implement notifyFinished()");
  //  }
  //
  //  public final void testNotifyData()
  //  {
  //    throw new ImplementationError("Implement notifyData()");
  //  }
  //
  //  public final void testNotification()
  //  {
  //    throw new ImplementationError("Implement notification()");
  //  }
  //
  //  public final void testRequest()
  //  {
  //    throw new ImplementationError("Implement request()");
  //  }
  //
  //  public final void testHandleTransmission()
  //  {
  //    throw new ImplementationError("Implement handleTransmission()");
  //  }
}
