/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.old;


import org.eclipse.net4j.core.impl.AbstractPool;
import org.eclipse.net4j.spring.Container;
import org.eclipse.net4j.spring.impl.ContainerImpl;

import junit.framework.TestCase;


public class PoolTest extends TestCase
{
  protected class TestPooled
  {
    protected Object key;

    public TestPooled(Object key)
    {
      this.key = key;
    }

    public Object getKey()
    {
      return key;
    }
  }


  protected class TestPool extends AbstractPool
  {
    protected Class doGetPooledClass(Object key)
    {
      return TestPooled.class;
    }

    protected Object newPooled(Object key) throws Exception
    {
      return new TestPooled(key);
    }
  }

  protected static Container net4j = new ContainerImpl(null, (String[]) null, "net4j", null, null);

  protected TestPool pool;

  //  protected Pool bufferPoolMock;
  //
  //  protected MockControl bufferPoolMockControl;
  //
  //  protected Connector connectorMock;
  //
  //  protected MockControl connectorMockControl;
  //
  //  protected Executor dispatcherMock;
  //
  //  protected MockControl dispatcherMockControl;
  //
  //  protected Multiplexer multiplexerMock;
  //
  //  protected MockControl multiplexerMockControl;
  //
  //  protected Protocol protocolMock;
  //
  //  protected MockControl protocolMockControl;

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
    pool = new TestPool();
    pool.setApplicationContext(net4j);
    pool.setBeanName("testPool");

    //    bufferPoolMockControl = MockControl.createControl(Pool.class);
    //    bufferPoolMock = (Pool) bufferPoolMockControl.getMock();
    //
    //    connectorMockControl = MockControl.createControl(Connector.class);
    //    connectorMock = (Connector) connectorMockControl.getMock();
    //
    //    dispatcherMockControl = MockControl.createControl(Executor.class);
    //    dispatcherMock = (Executor) dispatcherMockControl.getMock();
    //
    //    multiplexerMockControl = MockControl.createControl(Multiplexer.class);
    //    multiplexerMock = (Multiplexer) multiplexerMockControl.getMock();
    //
    //    protocolMockControl = MockControl.createControl(Protocol.class);
    //    protocolMock = (Protocol) protocolMockControl.getMock();
  }

  protected void start()
  {
    //    bufferPoolMockControl.replay();
    //    connectorMockControl.replay();
    //    dispatcherMockControl.replay();
    //    multiplexerMockControl.replay();
    //    protocolMockControl.replay();
  }

  protected void verify()
  {
    //    protocolMockControl.verify();
    //    multiplexerMockControl.verify();
    //    dispatcherMockControl.verify();
    //    connectorMockControl.verify();
    //    bufferPoolMockControl.verify();
  }

  //  protected BufferImpl createBuffer(int capacity, int level, boolean flipped)
  //  {
  //    BufferImpl buffer = new BufferImpl(capacity);
  //
  //    if (level > 0)
  //    {
  //      buffer.put(Byte.MIN_VALUE); // for sequence-error-detection
  //
  //      for (int i = 1; i < level; i++)
  //      {
  //        buffer.put((byte) (i % 256));
  //      }
  //    }
  //
  //    if (flipped)
  //    {
  //      buffer.flip();
  //    }
  //
  //    return buffer;
  //  }
  //
  //  protected NodeCachingLinkedList createBufferQueue(int[] capacities, int[] levels, boolean flipped)
  //  {
  //    NodeCachingLinkedList queue = new NodeCachingLinkedList();
  //
  //    for (int i = 0; i < levels.length; i++)
  //    {
  //      queue.add(createBuffer(capacities[i], levels[i], flipped));
  //    }
  //
  //    return queue;
  //  }
  //
  //  protected NodeCachingLinkedList createBufferQueue(int capacity, int[] levels, boolean flipped)
  //  {
  //    int[] capacities = new int[levels.length];
  //    Arrays.fill(capacities, capacity);
  //    return createBufferQueue(capacities, levels, flipped);
  //  }
  //
  //  protected NodeCachingLinkedList createBufferQueue(int capacity, int level, boolean flipped)
  //  {
  //    return createBufferQueue(new int[] { capacity}, new int[] { level}, flipped);
  //  }

  public final void testGet()
  {
    pool.setMinimumCapacity(0);
    pool.setMaximumCapacity(10);

    final int COUNT = 20;
    Object[] pooled = new Object[COUNT];

    for (int i = COUNT - 1; i >= 0; --i)
    {
      pooled[i] = pool.get();
      assertNotNull(pooled[i]);
      assertEquals(0, pool.getLevel());
    }

    for (int i = 0; i < COUNT; i++)
    {
      assertEquals(i, pool.getLevel());
      pool.put(pooled[i]);
      assertEquals(i + 1, pool.getLevel());
    }

    for (int i = COUNT - 1; i >= 0; --i)
    {
      assertEquals(i + 1, pool.getLevel());
      pooled[i] = pool.get();
      assertNotNull(pooled[i]);
      assertEquals(i, pool.getLevel());
    }

  }

  //  public final void testSetGetChannelIndex()
  //  {
  //    pool.setChannelId((short) 4711);
  //    short result = pool.getChannelId();
  //    assertEquals("getChannelId returns the value of setChannelId", 4711, result);
  //
  //    pool.setChannelId((short) 0);
  //    result = pool.getChannelId();
  //    assertEquals("getChannelId returns 0", 0, result);
  //  }
  //
  //  public final void testSetGetBufferPool()
  //  {
  //    pool.setBufferPool(bufferPoolMock);
  //    Pool result = pool.getBufferPool();
  //    assertEquals("getBufferPool returns the value of setBufferPool", bufferPoolMock, result);
  //
  //    pool.setBufferPool(null);
  //    result = pool.getBufferPool();
  //    assertNull("getBufferPool returns null", result);
  //  }
  //
  //  public final void testSetGetConnector()
  //  {
  //    pool.setConnector(connectorMock);
  //    Connector result = pool.getConnector();
  //    assertEquals("getConnector returns the value of setConnector", connectorMock, result);
  //
  //    pool.setConnector(null);
  //    result = pool.getConnector();
  //    assertNull("getConnector returns null", result);
  //  }
  //
  //  public final void testSetGetDispatcher()
  //  {
  //    pool.setDispatcher(dispatcherMock);
  //    Executor result = pool.getDispatcher();
  //    assertEquals("getDispatcher returns the value of setDispatcher", dispatcherMock, result);
  //
  //    pool.setDispatcher(null);
  //    result = pool.getDispatcher();
  //    assertNull("getDispatcher returns null", result);
  //  }
  //
  //  public final void testSetGetMultiplexer()
  //  {
  //    pool.setMultiplexer(multiplexerMock);
  //    Multiplexer result = pool.getMultiplexer();
  //    assertEquals("getBufferPool returns the value of setMultiplexer", multiplexerMock, result);
  //
  //    pool.setMultiplexer(null);
  //    result = pool.getMultiplexer();
  //    assertNull("getMultiplexer returns null", result);
  //  }
  //
  //  public final void testSetGetProtocol()
  //  {
  //    pool.setProtocol(protocolMock);
  //    Protocol result = pool.getProtocol();
  //    assertEquals("getProtocol returns the value of setProtocol", protocolMock, result);
  //
  //    pool.setProtocol(null);
  //    result = pool.getProtocol();
  //    assertNull("getProtocol returns null", result);
  //  }
  //
  //  public final void testSetGetInternalReturnValue()
  //  {
  //    Object returnValue = new Object();
  //
  //    pool.internalSetReturnValue(returnValue);
  //    Object result = pool.testGetReturnValue();
  //    assertEquals("testGetReturnValue returns the value of internalSetReturnValue", returnValue,
  //        result);
  //
  //    pool.internalSetReturnValue(null);
  //    result = pool.testGetReturnValue();
  //    assertNull("testGetReturnValue returns null", result);
  //  }
  //
  //  public final void testSetGetProtocolData()
  //  {
  //    Object protocolData = new Object();
  //
  //    pool.setProtocolData(protocolData);
  //    Object result = pool.getProtocolData();
  //    assertEquals("getProtocolData returns the value of setProtocolData", protocolData, result);
  //
  //    pool.setProtocolData(null);
  //    result = pool.getProtocolData();
  //    assertNull("getProtocolData returns null", result);
  //  }
  //
  //  public final void testServiceGuards()
  //  {
  //    new ServiceInvoker(pool)
  //    {
  //      protected void invokeService(DefaultBean bean) throws Exception
  //      {
  //        ((DefaultChannel) bean).handleTransmission();
  //      }
  //    };
  //
  //    new ServiceInvoker(pool)
  //    {
  //      protected void invokeService(DefaultBean bean) throws Exception
  //      {
  //        ((DefaultChannel) bean).notification(null);
  //      }
  //    };
  //
  //    new ServiceInvoker(pool)
  //    {
  //      protected void invokeService(DefaultBean bean) throws Exception
  //      {
  //        ((DefaultChannel) bean).request(null);
  //      }
  //    };
  //  }
  //
  //  public final void testValidate()
  //  {
  //    pool.setBufferPool(bufferPoolMock);
  //    pool.setDispatcher(dispatcherMock);
  //    pool.setMultiplexer(multiplexerMock);
  //    pool.testValidate();
  //
  //    try
  //    {
  //      pool.setBufferPool(null);
  //      pool.setDispatcher(dispatcherMock);
  //      pool.setMultiplexer(multiplexerMock);
  //      pool.testValidate();
  //      fail("expected ValidationException");
  //    }
  //    catch (ValidationException expected)
  //    {
  //      TestUtils.assertContains(expected, "bufferPool");
  //    }
  //
  //    try
  //    {
  //      pool.setBufferPool(bufferPoolMock);
  //      pool.setDispatcher(null);
  //      pool.setMultiplexer(multiplexerMock);
  //      pool.testValidate();
  //      fail("expected ValidationException");
  //    }
  //    catch (ValidationException expected)
  //    {
  //      TestUtils.assertContains(expected, "dispatcher");
  //    }
  //
  //    try
  //    {
  //      pool.setBufferPool(bufferPoolMock);
  //      pool.setDispatcher(dispatcherMock);
  //      pool.setMultiplexer(null);
  //      pool.testValidate();
  //      fail("expected ValidationException");
  //    }
  //    catch (ValidationException expected)
  //    {
  //      TestUtils.assertContains(expected, "multiplexer");
  //    }
  //  }
  //
  //  /**
  //   * PRECONDITION: 	dataSize == 0
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				throws IllegalArgumentException
  //   */
  //  public final void testEnsureReceiverBuffer1() throws Throwable
  //  {
  //    NodeCachingLinkedList receiverQueue = new NodeCachingLinkedList();
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    start();
  //    try
  //    {
  //      new BlockingDetector(pool, receiverQueue, false)
  //      {
  //        protected void blockableOperation(Object target) throws Exception
  //        {
  //          ((DefaultChannel) target).testEnsureReceiverBuffer(0);
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
  //
  //  /**
  //   * PRECONDITION: 	dataSize < 0
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				throws IllegalArgumentException
  //   */
  //  public final void testEnsureReceiverBuffer2() throws Throwable
  //  {
  //    NodeCachingLinkedList receiverQueue = new NodeCachingLinkedList();
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    start();
  //    try
  //    {
  //      new BlockingDetector(pool, receiverQueue, false)
  //      {
  //        protected void blockableOperation(Object target) throws Exception
  //        {
  //          ((DefaultChannel) target).testEnsureReceiverBuffer(-1);
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
  //
  //  /**
  //   * PRECONDITION: 	receiverBuffer exists; 
  //   * 				0 < level < dataSize
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				throws ImplementationError
  //   */
  //  public final void testEnsureReceiverBuffer3() throws Throwable
  //  {
  //    BufferImpl receiverBuffer = createBuffer(20, 3, true);
  //    pool.testSetReceiverBuffer(receiverBuffer);
  //
  //    NodeCachingLinkedList receiverQueue = new NodeCachingLinkedList();
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    try
  //    {
  //      new BlockingDetector(pool, receiverQueue, false)
  //      {
  //        protected void blockableOperation(Object target) throws Exception
  //        {
  //          ((DefaultChannel) target).testEnsureReceiverBuffer(7);
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
  //
  //  /**
  //   * PRECONDITION: 	receiverBuffer exists; 
  //   * 				0 < dataSize < level
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				receiverBuffer not returned to bufferPool
  //   */
  //  public final void testEnsureReceiverBuffer4() throws Throwable
  //  {
  //    BufferImpl receiverBuffer = createBuffer(20, 12, true);
  //    pool.testSetReceiverBuffer(receiverBuffer);
  //
  //    NodeCachingLinkedList receiverQueue = new NodeCachingLinkedList();
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    new BlockingDetector(pool, receiverQueue, false)
  //    {
  //      protected void blockableOperation(Object target) throws Exception
  //      {
  //        ((DefaultChannel) target).testEnsureReceiverBuffer(7);
  //      }
  //    };
  //    verify();
  //  }
  //
  //  /**
  //   * PRECONDITION: 	receiverBuffer exists; 
  //   * 				0 < dataSize == level
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				receiverBuffer not returned to bufferPool
  //   */
  //  public final void testEnsureReceiverBuffer5() throws Throwable
  //  {
  //    BufferImpl receiverBuffer = createBuffer(20, 12, true);
  //    pool.testSetReceiverBuffer(receiverBuffer);
  //
  //    NodeCachingLinkedList receiverQueue = new NodeCachingLinkedList();
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    new BlockingDetector(pool, receiverQueue, false)
  //    {
  //      protected void blockableOperation(Object target) throws Exception
  //      {
  //        ((DefaultChannel) target).testEnsureReceiverBuffer(12);
  //      }
  //    };
  //    verify();
  //  }
  //
  //  /**
  //   * PRECONDITION: 	receiverBuffer exists; 
  //   * 				0 == level < dataSize; 
  //   * 				receiverQueue empty
  //   * 
  //   * EXPECTATION: 	receiverBuffer returned to bufferPool; 
  //   * 				blocks
  //   */
  //  public final void testEnsureReceiverBuffer6() throws Throwable
  //  {
  //    BufferImpl receiverBuffer = createBuffer(20, 0, true);
  //    pool.testSetReceiverBuffer(receiverBuffer);
  //
  //    NodeCachingLinkedList receiverQueue = new NodeCachingLinkedList();
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    bufferPoolMock.put(new Integer(20), receiverBuffer);
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    new BlockingDetector(pool, receiverQueue, true)
  //    {
  //      protected void blockableOperation(Object target) throws Exception
  //      {
  //        ((DefaultChannel) target).testEnsureReceiverBuffer(7);
  //      }
  //    };
  //    verify();
  //  }
  //
  //  /**
  //   * PRECONDITION: 	receiverBuffer exists; 
  //   * 				0 == level < dataSize; 
  //   * 				receiverQueue has too small buffer
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				receiverBuffer returned to bufferPool
  //   * 				throws ImplementationError
  //   */
  //  public final void testEnsureReceiverBuffer7() throws Throwable
  //  {
  //    BufferImpl receiverBuffer = createBuffer(20, 0, true);
  //    pool.testSetReceiverBuffer(receiverBuffer);
  //
  //    NodeCachingLinkedList receiverQueue = createBufferQueue(20, 5, true);
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    bufferPoolMock.put(new Integer(20), receiverBuffer);
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    try
  //    {
  //      new BlockingDetector(pool, receiverQueue, false)
  //      {
  //        protected void blockableOperation(Object target) throws Exception
  //        {
  //          ((DefaultChannel) target).testEnsureReceiverBuffer(7);
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
  //
  //  /**
  //   * PRECONDITION: 	receiverBuffer exists; 
  //   * 				0 == level < dataSize; 
  //   * 				receiverQueue has exact buffer
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				receiverBuffer returned to bufferPool
  //   */
  //  public final void testEnsureReceiverBuffer8() throws Throwable
  //  {
  //    BufferImpl receiverBuffer = createBuffer(20, 0, true);
  //    pool.testSetReceiverBuffer(receiverBuffer);
  //
  //    NodeCachingLinkedList receiverQueue = createBufferQueue(20, 7, true);
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    bufferPoolMock.put(new Integer(20), receiverBuffer);
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    new BlockingDetector(pool, receiverQueue, false)
  //    {
  //      protected void blockableOperation(Object target) throws Exception
  //      {
  //        ((DefaultChannel) target).testEnsureReceiverBuffer(7);
  //      }
  //    };
  //    verify();
  //  }
  //
  //  /**
  //   * PRECONDITION: 	receiverBuffer exists; 
  //   * 				0 == level < dataSize; 
  //   * 				receiverQueue has too big buffer
  //   * 
  //   * EXPECTATION: 	doesn't block; 
  //   * 				receiverBuffer returned to bufferPool
  //   */
  //  public final void testEnsureReceiverBuffer9() throws Throwable
  //  {
  //    BufferImpl receiverBuffer = createBuffer(20, 0, true);
  //    pool.testSetReceiverBuffer(receiverBuffer);
  //
  //    NodeCachingLinkedList receiverQueue = createBufferQueue(20, 12, true);
  //    pool.testSetReceiverQueue(receiverQueue);
  //
  //    bufferPoolMock.put(new Integer(20), receiverBuffer);
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    new BlockingDetector(pool, receiverQueue, false)
  //    {
  //      protected void blockableOperation(Object target) throws Exception
  //      {
  //        ((DefaultChannel) target).testEnsureReceiverBuffer(7);
  //      }
  //    };
  //    verify();
  //  }
  //
  //  /**
  //   * PRECONDITION: 	transmitterBuffer doesn't exists
  //   * 
  //   * EXPECTATION: 	throws ImplementationError
  //   */
  //  public final void testFlush1()
  //  {
  //    start();
  //    try
  //    {
  //      pool.flush();
  //      fail("ImplementationError expected");
  //    }
  //    catch (ImplementationError expected)
  //    {
  //      TestUtils.assertContains(expected, "transmitterBuffer must exist");
  //    }
  //    verify();
  //  }
  //
  //  /**
  //   * PRECONDITION: 	transmitterBuffer exists;
  //   * 				level == 0 
  //   * 
  //   * EXPECTATION: 	immediately returns
  //   */
  //  public final void testFlush2()
  //  {
  //    BufferImpl transmitterBuffer = createBuffer(20, 0, false);
  //    pool.testSetTransmitterBuffer(transmitterBuffer);
  //
  //    start();
  //    pool.flush();
  //    verify();
  //  }
  //
  //  /**
  //   * PRECONDITION: 	transmitterBuffer exists;
  //   * 				level > 0 
  //   * 
  //   * EXPECTATION: 	transmitterBuffer is flipped;
  //   * 				appended to transmitterQueue;
  //   * 				channel is scheduled for transmission;
  //   * 				a new transmitterBuffer is fetched from the bufferPool;
  //   * 				the new transmitterBuffer is empty
  //   */
  //  public final void testFlush3()
  //  {
  //    BufferImpl transmitterBuffer = createBuffer(20, 7, false);
  //    pool.testSetTransmitterBuffer(transmitterBuffer);
  //
  //    NodeCachingLinkedList transmitterQueue = new NodeCachingLinkedList();
  //    pool.testSetTransmitterQueue(transmitterQueue);
  //
  //    multiplexerMock.schedule(pool);
  //    pool.setMultiplexer(multiplexerMock);
  //
  //    bufferPoolMockControl.expectAndReturn(bufferPoolMock.get(), createBuffer(10, 10, false));
  //    pool.setBufferPool(bufferPoolMock);
  //
  //    start();
  //    pool.flush();
  //    verify();
  //
  //    assertTrue("transmitterBuffer is flipped", transmitterBuffer.position() == 0);
  //    assertTrue("transmitterBuffer is in transmitterQueue",
  //        transmitterQueue.get(0) == transmitterBuffer);
  //    assertNotSame("a new transmitterBuffer is expected", transmitterBuffer, pool
  //        .testGetTransmitterBuffer());
  //    assertTrue("the new transmitterBuffer must be empty", pool.testGetTransmitterBuffer()
  //        .position() == 0);
  //
  //  }
}
