/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocol2;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IChallengeResponse;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.Negotiator;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.spi.net4j.ChannelMultiplexer;
import org.eclipse.spi.net4j.InternalBuffer;
import org.eclipse.spi.net4j.MessageConnector;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;
import org.eclipse.spi.net4j.SchedulingConnector;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exercises the provider-independent message connector using a deterministic in-memory connection.
 *
 * @author Eike Stepper
 */
public class MessageConnectorTest extends AbstractOMTest
{
  public void testDataFrameWaitsForPrecedingControlChannelRegistration() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    short channelID = -23;
    short independentChannelID = -24;
    CountDownLatch controlProtocolCreationEntered = new CountDownLatch(1);
    CountDownLatch allowControlProtocolCreation = new CountDownLatch(1);
    CountDownLatch dataFrameReceived = new CountDownLatch(1);
    CountDownLatch independentFrameReceived = new CountDownLatch(1);
    CountDownLatch channelRegistered = new CountDownLatch(1);
    AtomicInteger dataFrameReleases = new AtomicInteger();
    AtomicInteger independentFrameReleases = new AtomicInteger();
    connector.setIndependentDataFrameReceived(independentFrameReceived);
    connector.openInverse(independentChannelID);
    connector.blockProtocolCreation(controlProtocolCreationEntered, allowControlProtocolCreation, dataFrameReceived);
    ContainerEventAdapter<IChannel> channelRegistrationListener = new ContainerEventAdapter<>()
    {
      @Override
      protected void onAdded(IContainer<IChannel> container, IChannel channel)
      {
        if (channel.getID() == channelID)
        {
          channelRegistered.countDown();
        }
      }
    };
    connector.addListener(channelRegistrationListener);
    try
    {
      connection.receive(registrationFrame(channelID), () -> {
      });
      assertTrue("The actual control-channel registration path did not reach protocol creation", //$NON-NLS-1$
          controlProtocolCreationEntered.await(5, TimeUnit.SECONDS));
      awaitRequests(connection, 2);
      assertNull("The channel was registered before its control operation completed", connector.getChannel(channelID)); //$NON-NLS-1$

      connection.receive(dataFrame(channelID), dataFrameReleases::incrementAndGet);
      assertEquals("A dependent data frame was discarded before channel registration", 0, dataFrameReleases.get()); //$NON-NLS-1$
      awaitRequests(connection, 3);
      connection.receive(dataFrame(independentChannelID), independentFrameReleases::incrementAndGet);
      assertTrue("A pending channel registration blocked an independent channel", independentFrameReceived.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("The independent data frame was not released exactly once", 1, independentFrameReleases.get()); //$NON-NLS-1$

      allowControlProtocolCreation.countDown();
      boolean acknowledgementSent = connection.sent.await(5, TimeUnit.SECONDS);
      boolean registered = channelRegistered.await(5, TimeUnit.SECONDS);
      assertTrue(
          "The control registration did not publish the new channel (registered=" + connector.getChannel(channelID) + ", ack="
              + (acknowledgementSent ? acknowledgementError(connection.lastBuffer) : "not sent") + ", aborted=" + connection.aborted + ")", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          registered);
      assertTrue("The data frame was not delivered after the control channel registered it", //$NON-NLS-1$
          dataFrameReceived.await(5, TimeUnit.SECONDS));
      assertEquals("The dependent data frame was not released exactly once", 1, dataFrameReleases.get()); //$NON-NLS-1$
    }
    finally
    {
      allowControlProtocolCreation.countDown();
      connector.removeListener(channelRegistrationListener);
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testRemovalBeforeFirstReceiveQueueCreationDoesNotReintroduceQueue() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    IChannel channel = connector.openInverse((short)-28);
    CountDownLatch channelResolved = new CountDownLatch(1);
    CountDownLatch continueLookup = new CountDownLatch(1);
    CountDownLatch channelRemoved = new CountDownLatch(1);
    AtomicInteger providerReleases = new AtomicInteger();
    BlockingChannelMap blockingMap = null;
    Thread receiver = null;
    Thread remover = null;
    try
    {
      Field channelsField = ChannelMultiplexer.class.getDeclaredField("channels"); //$NON-NLS-1$
      channelsField.setAccessible(true);
      @SuppressWarnings("unchecked")
      ConcurrentMap<Short, IChannel> channels = (ConcurrentMap<Short, IChannel>)channelsField.get(connector);
      blockingMap = new BlockingChannelMap((short)-28, channelResolved, continueLookup, channelRemoved);
      blockingMap.putAll(channels);
      channelsField.set(connector, blockingMap);

      receiver = new Thread(() -> connection.receive(dataFrame((short)-28), providerReleases::incrementAndGet), "Receive lookup race"); //$NON-NLS-1$
      receiver.start();
      assertTrue("The receiver did not resolve the target channel", channelResolved.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      remover = new Thread(channel::close, "Remove receive channel"); //$NON-NLS-1$
      remover.start();
      assertTrue("The channel was not removed from the multiplexer", channelRemoved.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      continueLookup.countDown();
      receiver.join(5000);
      remover.join(5000);
      assertFalse("The receive callback did not finish", receiver.isAlive()); //$NON-NLS-1$
      assertFalse("The channel close did not finish", remover.isAlive()); //$NON-NLS-1$

      Field queuesField = MessageConnector.class.getDeclaredField("channelReceiveQueues"); //$NON-NLS-1$
      queuesField.setAccessible(true);
      Map<?, ?> queues = (Map<?, ?>)queuesField.get(connector);
      assertFalse("A removed channel was reintroduced into the receive queue map", queues.containsKey(channel)); //$NON-NLS-1$
      assertEquals("The stale-channel frame was not released exactly once", 1, providerReleases.get()); //$NON-NLS-1$
    }
    finally
    {
      continueLookup.countDown();
      if (receiver != null)
      {
        receiver.join(1000);
      }

      if (remover != null)
      {
        remover.join(1000);
      }

      channel.close();
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testReceiveBudgetExhaustionFailsPendingConfirmationAndReleasesLeases() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    ExecutorService executor = Executors.newCachedThreadPool();
    TestConnector connector = new TestConnector(9, 0);
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.getConfig().setReceiveExecutor(executor);
    connector.setConnection(connection);
    TestSignalProtocol protocol = new TestSignalProtocol();
    protocol.setExecutorService(executor);
    CountDownLatch requesting = new CountDownLatch(1);
    AtomicReference<Throwable> requestFailure = new AtomicReference<>();
    AtomicInteger partialResponseReleases = new AtomicInteger();
    AtomicInteger exhaustionFrameReleases = new AtomicInteger();
    Thread requestThread = null;
    TestConnector unrelatedConnector = null;
    try
    {
      LifecycleUtil.activate(connector);
      connector.setSkipPeerRegistration(true);
      protocol.open(connector);
      short channelID = protocol.getChannel().getID();

      requestThread = new Thread(() -> {
        try
        {
          new RequestWithConfirmation<Boolean>(protocol, (short)31)
          {
            @Override
            protected void requesting(ExtendedDataOutputStream out) throws Exception
            {
              out.writeBoolean(true);
              requesting.countDown();
            }

            @Override
            protected Boolean confirming(ExtendedDataInputStream in) throws Exception
            {
              in.readLong();
              return true;
            }
          }.send();
        }
        catch (Throwable ex)
        {
          requestFailure.set(ex);
        }
      }, "Pending confirmation request"); //$NON-NLS-1$
      requestThread.start();
      assertTrue("The confirmation request was not serialized", requesting.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("The confirmation request was not sent", connection.firstAttempt.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("The confirmation send did not enter provider ownership", connection.firstPendingSend.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      IBuffer request = connection.lastBuffer;
      int requestCorrelation = request.getByteBuffer().duplicate().order(ByteOrder.BIG_ENDIAN).getInt(IBuffer.HEADER_SIZE);
      connection.complete();

      int confirmationCorrelation = -(requestCorrelation & ~1) | 1;
      connection.receive(signalFrame(channelID, confirmationCorrelation), partialResponseReleases::incrementAndGet);
      awaitRequests(connection, 2);
      assertEquals("The partial confirmation should remain leased while the request waits", 0, partialResponseReleases.get()); //$NON-NLS-1$

      connection.receive(dataFrame(channelID), exhaustionFrameReleases::incrementAndGet);
      awaitInactive(connector);
      requestThread.join(5000);
      assertFalse("Receive-budget exhaustion did not unblock the pending confirmation", requestThread.isAlive()); //$NON-NLS-1$
      assertTrue("The pending request did not fail with a transport lifecycle exception", //$NON-NLS-1$
          requestFailure.get() instanceof RemoteException);
      assertTrue("The pending request did not report protocol deactivation", requestFailure.get().getMessage().contains("deactivated")); //$NON-NLS-1$
      assertEquals("The leased partial response was not released exactly once", 1, partialResponseReleases.get()); //$NON-NLS-1$
      assertEquals("The frame rejected by the receive budget was not released exactly once", 1, exhaustionFrameReleases.get()); //$NON-NLS-1$
      assertTrue("The exhausted connection was not aborted", connection.aborted); //$NON-NLS-1$

      MemoryConnection unrelatedConnection = new MemoryConnection();
      unrelatedConnector = activate(unrelatedConnection);
      CountDownLatch unrelatedFrameReceived = new CountDownLatch(1);
      IChannel unrelatedChannel = unrelatedConnector.openInverse((short)-29);
      unrelatedChannel.setReceiveHandler(buffer -> {
        buffer.release();
        unrelatedFrameReceived.countDown();
      });
      unrelatedConnection.receive(dataFrame((short)-29), () -> {
      });
      assertTrue("Receive-budget exhaustion affected an unrelated connection", //$NON-NLS-1$
          unrelatedFrameReceived.await(5, TimeUnit.SECONDS));
    }
    finally
    {
      protocol.close();
      if (requestThread != null && requestThread.isAlive())
      {
        requestThread.interrupt();
        requestThread.join(1000);
      }

      if (unrelatedConnector != null)
      {
        LifecycleUtil.deactivate(unrelatedConnector);
      }

      LifecycleUtil.deactivate(connector);
      executor.shutdownNow();
    }
  }

  public void testAcceptedSendCompletesAndReleasesBuffer() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    try
    {
      IBuffer buffer = enqueue(connector, connector.getControlChannel());
      assertTrue("Message was not submitted", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(buffer, connection.lastBuffer);
      assertEquals(BufferState.PUTTING, buffer.getState());
      assertEquals(1, connection.sendCount.get());

      connection.complete();
      awaitReleased(buffer);
      assertReleased(buffer);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testSingleInFlightAndWritableAfterRejection() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    connection.rejectNext = true;
    TestConnector connector = activate(connection);
    try
    {
      IBuffer first = enqueue(connector, connector.getControlChannel());
      IBuffer second = enqueue(connector, connector.getControlChannel());
      assertTrue("Initial send was not attempted", connection.firstAttempt.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals(1, connection.sendCount.get());
      connection.writable();
      assertTrue("Writable notification did not resume send", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(first, connection.lastBuffer);
      connection.writable();
      assertEquals("A second buffer was submitted before completion", 2, connection.sendCount.get()); //$NON-NLS-1$

      connection.complete();
      awaitReleased(first);
      assertTrue("Second buffer was not submitted after completion", connection.secondAttempt.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(second, connection.lastBuffer);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testFairRotationAcrossBackloggedChannels() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    try
    {
      IChannel firstChannel = connector.openInverse((short)-7);
      IChannel secondChannel = connector.openInverse((short)-8);
      IBuffer first = enqueue(connector, firstChannel);
      enqueue(connector, firstChannel);
      IBuffer second = enqueue(connector, secondChannel);

      assertTrue("First channel was not submitted", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(first, connection.lastBuffer);
      connection.complete();
      assertTrue("Round-robin scheduler did not select the second channel", connection.secondAttempt.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(second, connection.lastBuffer);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testChannelClosePreservesBorrowedBufferUntilCompletion() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    try
    {
      IChannel channel = connector.openInverse((short)-9);
      IBuffer buffer = enqueue(connector, channel);
      assertTrue("Channel send was not submitted", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      channel.close();
      assertFalse("Channel close released a buffer still borrowed by the provider", isReleased(buffer)); //$NON-NLS-1$
      connection.complete();
      awaitReleased(buffer);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testSendFailureAbortsAndReleasesBuffer() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    IBuffer buffer = enqueue(connector, connector.getControlChannel());
    try
    {
      assertTrue(
          "Failed send was not submitted (count=" + connection.sendCount.get() + ", active=" + connector.isActive() + ", state=" + connector.getState() + ")", //$NON-NLS-4$
          connection.sent.await(5, TimeUnit.SECONDS));
      connection.fail(new IllegalStateException("send failed")); //$NON-NLS-1$
      awaitInactive(connector);
      assertTrue(connection.aborted);
      awaitReleased(buffer);
      assertReleased(buffer);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testAbortFailureStillDeactivatesAndAllowsReactivation() throws Exception
  {
    MemoryConnection failedConnection = new MemoryConnection();
    failedConnection.deferAbortCompletions = true;
    RuntimeException abortFailure = new IllegalStateException("controlled abort failure"); //$NON-NLS-1$
    failedConnection.abortFailure = abortFailure;
    TestConnector connector = new TestConnector();
    CountingBufferProvider bufferProvider = new CountingBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.getConfig().setBufferProvider(bufferProvider);
    connector.setConnection(failedConnection);
    LifecycleUtil.activate(connector);
    IBuffer pending = enqueue(connector, connector.getControlChannel());
    bufferProvider.track(pending);
    RuntimeException transportFailure = new IllegalStateException("controlled transport failure"); //$NON-NLS-1$
    CountDownLatch received = new CountDownLatch(1);
    AtomicInteger receivedStorageReleaseCount = new AtomicInteger();

    try
    {
      assertTrue("Failed-generation send was not submitted", failedConnection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      IChannel receiveChannel = connector.openInverse((short)-15);
      receiveChannel.setReceiveHandler(buffer -> {
        buffer.release();
        received.countDown();
      });
      failedConnection.requestFailure = transportFailure;
      ByteBuffer frame = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      frame.putShort((short)-15).putShort((short)1).put((byte)0x5A).flip();
      failedConnection.receive(frame, receivedStorageReleaseCount::incrementAndGet);
      assertTrue("Failed-generation receive was not dispatched", received.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      awaitInactive(connector);
      assertEquals("Abort was not requested exactly once", 1, failedConnection.abortCount.get()); //$NON-NLS-1$
      assertEquals("Original transport failure was not preserved", "controlled transport failure", transportFailure.getMessage()); //$NON-NLS-1$
      assertEquals("Abort failure was not retained as a suppressed diagnostic", 1, transportFailure.getSuppressed().length); //$NON-NLS-1$
      assertSame(abortFailure, transportFailure.getSuppressed()[0]);
      assertEquals("Received provider storage was not released exactly once", 1, receivedStorageReleaseCount.get()); //$NON-NLS-1$
      assertEquals("A throwing abort released a provider-owned buffer prematurely", BufferState.PUTTING, pending.getState()); //$NON-NLS-1$
      assertEquals("A throwing abort released the pending send buffer", 0, bufferProvider.retainedBuffers.get()); //$NON-NLS-1$

      MemoryConnection replacement = new MemoryConnection();
      connector.setConnection(replacement);
      LifecycleUtil.activate(connector);
      assertEquals(ConnectorState.CONNECTED, connector.getState());

      failedConnection.complete();
      awaitReleased(pending);
      assertEquals("The late terminal callback did not release the send buffer exactly once", 1, bufferProvider.retainedBuffers.get()); //$NON-NLS-1$
      assertEquals("A late callback changed the replacement generation", LifecycleState.ACTIVE, connector.getLifecycleState()); //$NON-NLS-1$
      assertEquals(ConnectorState.CONNECTED, connector.getState());

      IBuffer replacementBuffer = enqueue(connector, connector.getControlChannel());
      assertTrue("Replacement-generation send was not submitted", replacement.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      replacement.complete();
      awaitReleased(replacementBuffer);
      assertEquals("The failed provider was aborted more than once", 1, failedConnection.abortCount.get()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testThrowingSubmissionWithoutCallbackReleasesBufferOnceAndDeactivates() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    connection.blockSubmission = true;
    connection.throwBlockedSubmission = true;
    TestConnector connector = new TestConnector();
    CountingBufferProvider bufferProvider = new CountingBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.getConfig().setBufferProvider(bufferProvider);
    connector.setConnection(connection);
    LifecycleUtil.activate(connector);

    IChannel channel = connector.openInverse((short)-14);
    IBuffer buffer = enqueue(connector, channel);
    bufferProvider.track(buffer);
    try
    {
      assertTrue("Provider submission did not block", connection.submissionEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      connection.allowSubmission.countDown();
      assertTrue("Provider did not throw before accepting the buffer", connection.submissionOutcomeReady.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitInactive(connector);
      awaitReleased(buffer);

      assertNull("A rejected throwing submission must not deliver a callback", connection.lastCallback); //$NON-NLS-1$
      assertEquals("The buffer was not released exactly once", 1, bufferProvider.retainedBuffers.get()); //$NON-NLS-1$
      assertTrue(connection.aborted);
      assertFalse(connector.isActive());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testRemoteCloseGracefulCloseAndReactivationIgnoreOldCallbacks() throws Exception
  {
    MemoryConnection firstConnection = new MemoryConnection();
    TestConnector connector = activate(firstConnection);
    firstConnection.closeRemotely(null);
    awaitInactive(connector);

    MemoryConnection secondConnection = new MemoryConnection();
    connector.setConnection(secondConnection);
    LifecycleUtil.activate(connector);
    assertEquals(ConnectorState.CONNECTED, connector.getState());
    firstConnection.listener.ready();
    firstConnection.listener.writable();
    firstConnection.listener.closed(new IllegalStateException("stale")); //$NON-NLS-1$
    assertTrue(connector.isActive());

    IBuffer pending = enqueue(connector, connector.getControlChannel());
    assertTrue("Queued message was not submitted", secondConnection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    connector.closeGracefully();
    assertFalse("Provider close began before queued messages completed", secondConnection.gracefulCloseRequested); //$NON-NLS-1$
    secondConnection.complete();
    awaitInactive(connector);
    assertTrue(secondConnection.gracefulCloseRequested);
    assertReleased(pending);
  }

  public void testFailureDuringActivationCleansUpAndAllowsNewConnection() throws Exception
  {
    MemoryConnection failedConnection = new MemoryConnection();
    failedConnection.closeDuringStart = new IllegalStateException("activation failed"); //$NON-NLS-1$
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.setConnection(failedConnection);
    try
    {
      LifecycleUtil.activate(connector);
      fail("Activation succeeded after the provider closed during start"); //$NON-NLS-1$
    }
    catch (RuntimeException expected)
    {
      assertFalse(connector.isActive());
      assertEquals(LifecycleState.INACTIVE, connector.getLifecycleState());
      assertTrue(failedConnection.aborted);
    }

    MemoryConnection replacement = new MemoryConnection();
    connector.setConnection(replacement);
    LifecycleUtil.activate(connector);
    assertEquals(ConnectorState.CONNECTED, connector.getState());
    LifecycleUtil.deactivate(connector);
  }

  public void testCancelledStartReturnCannotCompleteNewerActivation() throws Exception
  {
    MemoryConnection oldConnection = new MemoryConnection();
    oldConnection.blockStart = true;
    oldConnection.autoReadyOnStart = false;
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.setConnection(oldConnection);
    AtomicReference<Throwable> oldActivationFailure = new AtomicReference<>();
    Thread oldActivation = new Thread(() -> activateAndCapture(connector, oldActivationFailure), "old-message-activation"); //$NON-NLS-1$
    oldActivation.start();
    MemoryConnection newConnection = new MemoryConnection();
    newConnection.blockStart = true;
    newConnection.autoReadyOnStart = false;
    Thread newActivation = null;
    AtomicReference<Throwable> newActivationFailure = new AtomicReference<>();
    try
    {
      assertTrue("Old provider start did not block", oldConnection.startEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      LifecycleUtil.deactivate(connector);
      connector.setConnection(newConnection);
      newActivation = new Thread(() -> activateAndCapture(connector, newActivationFailure), "new-message-activation"); //$NON-NLS-1$
      newActivation.start();
      assertTrue("New provider start did not block", newConnection.startEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      oldConnection.allowStart.countDown();
      oldActivation.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("Old activation did not return", oldActivation.isAlive()); //$NON-NLS-1$
      assertNull(oldActivationFailure.get());
      assertTrue(oldConnection.aborted);

      oldConnection.listener.ready();
      newConnection.listener.ready();
      assertEquals("The old start return completed activation B", LifecycleState.ACTIVATING, connector.getLifecycleState()); //$NON-NLS-1$

      newConnection.allowStart.countDown();
      newActivation.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("New activation did not return", newActivation.isAlive()); //$NON-NLS-1$
      assertNull(newActivationFailure.get());
      assertEquals(LifecycleState.ACTIVE, connector.getLifecycleState());
      assertEquals(ConnectorState.CONNECTED, connector.getState());
      assertTrue(newConnection.requested.get() > 0);
    }
    finally
    {
      oldConnection.allowStart.countDown();
      newConnection.allowStart.countDown();
      oldActivation.join(TimeUnit.SECONDS.toMillis(5));
      if (newActivation != null)
      {
        newActivation.join(TimeUnit.SECONDS.toMillis(5));
      }

      LifecycleUtil.deactivate(connector);
    }

    assertTrue(newConnection.aborted);
  }

  public void testCancelledStartExceptionCannotFailNewerActivation() throws Exception
  {
    MemoryConnection oldConnection = new MemoryConnection();
    oldConnection.blockStart = true;
    oldConnection.autoReadyOnStart = false;
    oldConnection.startFailure = new IllegalStateException("obsolete start failure"); //$NON-NLS-1$
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.setConnection(oldConnection);
    AtomicReference<Throwable> oldActivationFailure = new AtomicReference<>();
    Thread oldActivation = new Thread(() -> activateAndCapture(connector, oldActivationFailure), "old-message-activation"); //$NON-NLS-1$
    oldActivation.start();
    MemoryConnection newConnection = new MemoryConnection();
    newConnection.blockStart = true;
    newConnection.autoReadyOnStart = false;
    Thread newActivation = null;
    AtomicReference<Throwable> newActivationFailure = new AtomicReference<>();
    try
    {
      assertTrue("Old provider start did not block", oldConnection.startEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      LifecycleUtil.deactivate(connector);
      connector.setConnection(newConnection);
      newActivation = new Thread(() -> activateAndCapture(connector, newActivationFailure), "new-message-activation"); //$NON-NLS-1$
      newActivation.start();
      assertTrue("New provider start did not block", newConnection.startEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      oldConnection.allowStart.countDown();
      oldActivation.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("Old activation did not return", oldActivation.isAlive()); //$NON-NLS-1$
      assertNotNull("The controlled obsolete exception was not propagated", oldActivationFailure.get()); //$NON-NLS-1$
      assertTrue(oldConnection.aborted);
      assertEquals("The obsolete exception changed the new lifecycle attempt", LifecycleState.ACTIVATING, connector.getLifecycleState()); //$NON-NLS-1$
      oldConnection.listener.ready();
      assertEquals("A stale ready callback changed the new lifecycle attempt", LifecycleState.ACTIVATING, connector.getLifecycleState()); //$NON-NLS-1$
      LifecycleUtil.activate(connector);
      assertEquals("Repeated activation entered while B owned the pending attempt", 1, newConnection.startCalls.get()); //$NON-NLS-1$

      newConnection.allowStart.countDown();
      newActivation.join(TimeUnit.SECONDS.toMillis(5));
      assertFalse("New activation did not return", newActivation.isAlive()); //$NON-NLS-1$
      assertNull(newActivationFailure.get());
      newConnection.listener.ready();
      assertEquals(LifecycleState.ACTIVE, connector.getLifecycleState());
      assertEquals(ConnectorState.CONNECTED, connector.getState());

      LifecycleUtil.deactivate(connector);
      MemoryConnection thirdConnection = new MemoryConnection();
      connector.setConnection(thirdConnection);
      LifecycleUtil.activate(connector);
      assertEquals(LifecycleState.ACTIVE, connector.getLifecycleState());
      LifecycleUtil.deactivate(connector);
    }
    finally
    {
      oldConnection.allowStart.countDown();
      newConnection.allowStart.countDown();
      oldActivation.join(TimeUnit.SECONDS.toMillis(5));
      if (newActivation != null)
      {
        newActivation.join(TimeUnit.SECONDS.toMillis(5));
      }

      LifecycleUtil.deactivate(connector);
    }

    assertTrue(newConnection.aborted);
  }

  private static void activateAndCapture(TestConnector connector, AtomicReference<Throwable> failure)
  {
    try
    {
      LifecycleUtil.activate(connector);
    }
    catch (Throwable ex)
    {
      failure.set(ex);
    }
  }

  public void testImmediateCompletionAndDeferredShutdownOwnership() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    connection.completeImmediately = true;
    TestConnector connector = activate(connection);
    try
    {
      IBuffer buffer = enqueue(connector, connector.getControlChannel());
      assertTrue("Immediate send was not submitted", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitReleased(buffer);
      assertReleased(buffer);

      connection.completeImmediately = false;
      connection.deferAbortCompletions = true;
      IBuffer pending = enqueue(connector, connector.getControlChannel());
      assertTrue("Pending send was not recorded", connection.secondPendingSend.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      LifecycleUtil.deactivate(connector);
      assertTrue("Abort was not requested", connection.aborted); //$NON-NLS-1$
      assertFalse("Borrowed buffer was released before its terminal callback", isReleased(pending)); //$NON-NLS-1$
      connection.complete(); // A stale callback must not release the buffer a second time.
      awaitReleased(pending);
      assertReleased(pending);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testClosedChannelLateFailureAfterConnectorShutdown() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    connection.deferAbortCompletions = true;
    TestConnector connector = activate(connection);
    IChannel channel = connector.openInverse((short)-12);
    IBuffer buffer = enqueue(connector, channel);
    assertTrue("Channel send was not submitted", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    channel.close();
    LifecycleUtil.deactivate(connector);
    assertFalse("Closed channel released storage still borrowed by the provider", isReleased(buffer)); //$NON-NLS-1$
    connection.fail(new IllegalStateException("cancelled")); //$NON-NLS-1$
    awaitReleased(buffer);
    assertReleased(buffer);
  }

  public void testSubmissionAndShutdownPreserveOwnershipUntilOutcome() throws Exception
  {
    for (int scenario = 0; scenario < 3; ++scenario)
    {
      boolean reject = scenario == 0;
      MemoryConnection connection = new MemoryConnection();
      connection.blockSubmission = true;
      connection.rejectBlockedSubmission = reject;
      connection.completeBlockedSubmission = scenario == 2;
      connection.deferAbortCompletions = true;
      TestConnector connector = activate(connection);
      IBuffer buffer = enqueue(connector, connector.getControlChannel());
      assertTrue("Provider submission did not block", connection.submissionEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      LifecycleUtil.deactivate(connector);
      assertFalse("Shutdown released a buffer while send() was in progress", isReleased(buffer)); //$NON-NLS-1$
      connection.allowSubmission.countDown();

      if (reject)
      {
        awaitReleased(buffer);
      }
      else
      {
        assertTrue("Accepted submission was not recorded", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
        if (!connection.completeBlockedSubmission)
        {
          assertFalse("Accepted buffer was released before completion", isReleased(buffer)); //$NON-NLS-1$
          connection.complete();
        }

        awaitReleased(buffer);
      }

      assertReleased(buffer);
    }
  }

  public void testPumpNotificationsDoNotSubmitConcurrently() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    connection.blockSubmission = true;
    TestConnector connector = activate(connection);
    try
    {
      IBuffer first = enqueue(connector, connector.getControlChannel());
      IBuffer second = enqueue(connector, connector.getControlChannel());
      assertTrue("First send did not enter provider", connection.submissionEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      connection.writable();
      assertEquals("A second submission began while the first was active", 1, connection.sendCount.get()); //$NON-NLS-1$
      assertFalse("A rejected send was reported as successful", isReleased(first)); //$NON-NLS-1$

      connection.allowSubmission.countDown();
      assertTrue("First send was not accepted", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      connection.complete();
      awaitReleased(first);
      assertTrue("Second buffer was not submitted after completion", connection.secondAttempt.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(second, connection.lastBuffer);
    }
    finally
    {
      connection.allowSubmission.countDown();
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testOldGenerationCompletionOnlyCleansItsOwnBuffer() throws Exception
  {
    MemoryConnection oldConnection = new MemoryConnection();
    oldConnection.deferAbortCompletions = true;
    TestConnector connector = activate(oldConnection);
    IBuffer oldBuffer = enqueue(connector, connector.getControlChannel());
    assertTrue("Old connection did not accept a send", oldConnection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    LifecycleUtil.deactivate(connector);

    MemoryConnection newConnection = new MemoryConnection();
    connector.setConnection(newConnection);
    LifecycleUtil.activate(connector);
    assertEquals(ConnectorState.CONNECTED, connector.getState());
    IBuffer newBuffer = enqueue(connector, connector.getControlChannel());
    assertTrue("New connection was affected by an old callback", newConnection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    assertFalse("New connection buffer was released by the old callback", isReleased(newBuffer)); //$NON-NLS-1$

    oldConnection.complete();
    awaitReleased(oldBuffer);
    assertFalse("Old callback altered new generation ownership", isReleased(newBuffer)); //$NON-NLS-1$
    newConnection.complete();
    awaitReleased(newBuffer);
    LifecycleUtil.deactivate(connector);
  }

  public void testOldBlockedSubmissionResultsDoNotAffectNewGeneration() throws Exception
  {
    for (int scenario = 0; scenario < 2; ++scenario)
    {
      boolean throwFromSubmission = scenario == 1;
      MemoryConnection oldConnection = new MemoryConnection();
      oldConnection.blockSubmission = true;
      oldConnection.rejectBlockedSubmission = !throwFromSubmission;
      oldConnection.throwBlockedSubmission = throwFromSubmission;
      oldConnection.deferAbortCompletions = true;
      TestConnector connector = activate(oldConnection);
      IBuffer oldBuffer = enqueue(connector, connector.getControlChannel());
      assertTrue("Old provider submission did not block", oldConnection.submissionEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

      LifecycleUtil.deactivate(connector);
      MemoryConnection newConnection = new MemoryConnection();
      connector.setConnection(newConnection);
      LifecycleUtil.activate(connector);
      assertEquals(ConnectorState.CONNECTED, connector.getState());

      oldConnection.allowSubmission.countDown();
      if (throwFromSubmission)
      {
        assertTrue("Old provider did not record its ambiguous submission", oldConnection.submissionOutcomeReady.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      }

      IBuffer newBuffer = enqueue(connector, connector.getControlChannel());
      assertTrue("Obsolete submission result blocked the new provider", newConnection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(newBuffer, newConnection.lastBuffer);
      assertTrue("New connector generation was deactivated by an old submission result", connector.isActive()); //$NON-NLS-1$

      awaitReleased(oldBuffer);

      newConnection.complete();
      awaitReleased(newBuffer);
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testCompletionBeforeSubmissionReturnDuringShutdownAndChannelClose() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    connection.blockSubmission = true;
    connection.completeBlockedSubmission = true;
    TestConnector connector = activate(connection);
    IChannel channel = connector.openInverse((short)-13);
    IBuffer buffer = enqueue(connector, channel);
    assertTrue("Provider submission did not block", connection.submissionEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$

    channel.close();
    LifecycleUtil.deactivate(connector);
    assertFalse("Buffer was released before provider completion", isReleased(buffer)); //$NON-NLS-1$
    connection.allowSubmission.countDown();
    assertTrue("Accepted submission did not return", connection.sent.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
    awaitReleased(buffer);
    assertReleased(buffer);
  }

  public void testConfiguredNegotiatorSuccessAndFailure() throws Exception
  {
    TestConnector successful = new TestConnector();
    successful.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    successful.setConnection(new MemoryConnection());
    successful.getConfig().setNegotiator(createNegotiator(true));
    LifecycleUtil.activate(successful);
    assertEquals(ConnectorState.CONNECTED, successful.getState());
    successful.waitForConnection(5000);
    assertEquals(ConnectorState.CONNECTED, successful.getState());
    LifecycleUtil.deactivate(successful);

    TestConnector failed = new TestConnector();
    failed.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    failed.setConnection(new MemoryConnection());
    failed.getConfig().setNegotiator(createNegotiator(false));
    LifecycleUtil.activate(failed);
    try
    {
      failed.waitForConnection(5000);
      fail("Failed negotiation reported a connected state"); //$NON-NLS-1$
    }
    catch (ConnectorException expected)
    {
      assertNotNull(expected.getCause());
    }
    finally
    {
      LifecycleUtil.deactivate(failed);
    }
  }

  public void testNegotiationControlSendDuringDeferredActivationAndPendingApplicationBuffer() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = new TestConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.setConnection(connection);
    connector.getConfig().setNegotiator(createDeferredNegotiator());

    try
    {
      LifecycleUtil.activate(connector);
      assertEquals(ConnectorState.NEGOTIATING, connector.getState());
      assertFalse(connector.isActive());
      assertTrue("Negotiation control buffer was not submitted during deferred activation", //$NON-NLS-1$
          connection.firstAttempt.await(5, TimeUnit.SECONDS));

      IBuffer challenge = connection.lastBuffer;
      assertEquals(SchedulingConnector.CONTROL_CHANNEL_ID, challenge.getChannelID());
      connection.complete();
      awaitReleased(challenge);

      IChannel applicationChannel = connector.openInverse((short)-30);
      IBuffer applicationBuffer = enqueue(connector, applicationChannel);
      assertFalse("Application traffic was submitted before negotiation completed", //$NON-NLS-1$
          connection.secondAttempt.await(200, TimeUnit.MILLISECONDS));
      assertFalse("Pending application buffer was released during negotiation", isReleased(applicationBuffer)); //$NON-NLS-1$

      connection.receive(negotiationFrame(), () -> {
      });
      connector.waitForConnection(5000);
      assertTrue(connector.isActive());
      assertTrue("Pending application traffic did not resume after activation", //$NON-NLS-1$
          connection.secondPendingSend.await(5, TimeUnit.SECONDS));
      assertSame(applicationBuffer, connection.lastBuffer);

      connection.complete();
      awaitReleased(applicationBuffer);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  private static Negotiator<IChallengeResponse.State, IChallengeResponse.Event> createDeferredNegotiator()
  {
    return new Negotiator<>(IChallengeResponse.State.class, IChallengeResponse.Event.class, IChallengeResponse.State.INITIAL, IChallengeResponse.State.SUCCESS,
        IChallengeResponse.State.FAILURE, IChallengeResponse.Event.START, IChallengeResponse.Event.BUFFER, true)
    {
      {
        init(IChallengeResponse.State.INITIAL, IChallengeResponse.Event.START, (context, state, event, buffer) -> {
          ByteBuffer challenge = context.getBuffer();
          challenge.put((byte)0x42);
          context.transmitBuffer(challenge);
          context.setState(IChallengeResponse.State.CHALLENGE);
        });
        init(IChallengeResponse.State.CHALLENGE, IChallengeResponse.Event.BUFFER,
            (context, state, event, buffer) -> context.setState(IChallengeResponse.State.SUCCESS));
      }

      @Override
      protected IChallengeResponse.State getState(INegotiationContext context)
      {
        return (IChallengeResponse.State)context.getState();
      }

      @Override
      protected void setState(INegotiationContext context, IChallengeResponse.State state)
      {
        context.setState(state);
      }
    };
  }

  private static ByteBuffer negotiationFrame()
  {
    ByteBuffer frame = ByteBuffer.allocate(IBuffer.HEADER_SIZE + 2).order(ByteOrder.BIG_ENDIAN);
    frame.position(IBuffer.HEADER_SIZE);
    frame.put(SchedulingConnector.CONTROL_OPCODE_NEGOTIATION).put((byte)0x43);
    int messageSize = frame.position();
    frame.putShort(IBuffer.CHANNEL_ID_POS, SchedulingConnector.CONTROL_CHANNEL_ID);
    frame.putShort(IBuffer.PAYLOAD_SIZE_POS, (short)(messageSize - IBuffer.HEADER_SIZE));
    frame.position(0);
    frame.limit(messageSize);
    return frame;
  }

  private static Negotiator<IChallengeResponse.State, IChallengeResponse.Event> createNegotiator(boolean success)
  {
    IChallengeResponse.State target = success ? IChallengeResponse.State.SUCCESS : IChallengeResponse.State.FAILURE;
    return new Negotiator<>(IChallengeResponse.State.class, IChallengeResponse.Event.class, IChallengeResponse.State.INITIAL, IChallengeResponse.State.SUCCESS,
        IChallengeResponse.State.FAILURE, IChallengeResponse.Event.START, IChallengeResponse.Event.BUFFER, true)
    {
      {
        init(IChallengeResponse.State.INITIAL, IChallengeResponse.Event.START, target);
      }

      @Override
      protected IChallengeResponse.State getState(INegotiationContext context)
      {
        return (IChallengeResponse.State)context.getState();
      }

      @Override
      protected void setState(INegotiationContext context, IChallengeResponse.State state)
      {
        context.setState(state);
      }
    };
  }

  public void testReceiveLeaseAndDemandReplenishment() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    AtomicReference<IBuffer> delivered = new AtomicReference<>();
    CountDownLatch handled = new CountDownLatch(1);
    try
    {
      IChannel channel = connector.openInverse((short)-5);
      channel.setReceiveHandler(buffer -> {
        delivered.set(buffer);
        buffer.release();
        buffer.release();
        handled.countDown();
      });

      assertEquals(1, connection.requested.get());
      AtomicInteger released = new AtomicInteger();
      ByteBuffer frame = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      frame.putShort((short)-5).putShort((short)1).put((byte)0x5A).flip();
      connection.receive(frame, released::incrementAndGet);
      assertTrue("Frame was not dispatched", handled.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertNotNull(delivered.get());
      assertEquals(1, released.get());
      awaitRequests(connection, 2);
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }

    AtomicInteger lateRelease = new AtomicInteger();
    connection.receive(ByteBuffer.allocate(IBuffer.HEADER_SIZE), lateRelease::incrementAndGet);
    assertEquals("Shutdown must not request more messages", 2, connection.requested.get()); //$NON-NLS-1$
    assertEquals(1, lateRelease.get());
  }

  public void testReceiveContinuesWithRetainedFramesAndCopiesOnPressure() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection, 10, 10);
    AtomicReference<IBuffer> firstFrame = new AtomicReference<>();
    AtomicReference<IBuffer> secondFrame = new AtomicReference<>();
    AtomicReference<IBuffer> thirdFrame = new AtomicReference<>();
    AtomicReference<IBuffer> fourthFrame = new AtomicReference<>();
    AtomicInteger retainedFrames = new AtomicInteger();
    AtomicInteger progressFrames = new AtomicInteger();
    CountDownLatch received = new CountDownLatch(4);
    AtomicInteger providerReleases = new AtomicInteger();
    try
    {
      IChannel retainedChannel = connector.openInverse((short)-16);
      retainedChannel.setReceiveHandler(buffer -> {
        if (retainedFrames.getAndIncrement() == 0)
        {
          firstFrame.set(buffer);
        }
        else
        {
          secondFrame.set(buffer);
        }

        received.countDown();
      });

      IChannel progressChannel = connector.openInverse((short)-17);
      progressChannel.setReceiveHandler(buffer -> {
        if (progressFrames.getAndIncrement() == 0)
        {
          thirdFrame.set(buffer);
        }
        else
        {
          fourthFrame.set(buffer);
        }

        received.countDown();
      });

      ByteBuffer first = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      first.putShort((short)-16).putShort((short)1).put((byte)0x5A).flip();
      connection.receive(first, providerReleases::incrementAndGet);
      awaitRequests(connection, 2);

      ByteBuffer second = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      second.putShort((short)-16).putShort((short)1).put((byte)0x6B).flip();
      connection.receive(second, providerReleases::incrementAndGet);
      awaitRequests(connection, 3);

      ByteBuffer third = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      third.putShort((short)-17).putShort((short)1).put((byte)0x7C).flip();
      connection.receive(third, providerReleases::incrementAndGet);
      awaitRequests(connection, 4);

      ByteBuffer fourth = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      fourth.putShort((short)-17).putShort((short)1).put((byte)0x2D).flip();
      connection.receive(fourth, providerReleases::incrementAndGet);
      assertTrue("Other-channel messages must progress while the first channel retains its frames", received.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitRequests(connection, 5);

      assertNotNull(firstFrame.get());
      assertNotNull(secondFrame.get());
      assertNotNull(thirdFrame.get());
      assertNotNull(fourthFrame.get());
      assertEquals("Receive demand must continue independently of Net4j buffer release", 5, connection.requested.get()); //$NON-NLS-1$
      assertEquals("Pressure copies must release provider storage immediately", 2, providerReleases.get()); //$NON-NLS-1$
      assertEquals((byte)0x5A, firstFrame.get().getByteBuffer().get(IBuffer.HEADER_SIZE));
      assertEquals((byte)0x6B, secondFrame.get().getByteBuffer().get(IBuffer.HEADER_SIZE));
      third.put(IBuffer.HEADER_SIZE, (byte)0x00);
      assertEquals("Copied messages must no longer depend on provider storage", (byte)0x7C, thirdFrame.get().getByteBuffer().get(IBuffer.HEADER_SIZE)); //$NON-NLS-1$

      firstFrame.get().release();
      secondFrame.get().release();
      thirdFrame.get().release();
      fourthFrame.get().release();
      assertEquals(4, providerReleases.get());
    }
    finally
    {
      for (IBuffer buffer : new IBuffer[] { firstFrame.get(), secondFrame.get(), thirdFrame.get(), fourthFrame.get() })
      {
        if (buffer != null && !isReleased(buffer))
        {
          buffer.release();
        }
      }

      LifecycleUtil.deactivate(connector);
    }
  }

  public void testBlockingHandlerDoesNotPreventOtherChannelProgress() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection, 5, 20);
    CountDownLatch blockedHandlerEntered = new CountDownLatch(1);
    CountDownLatch unblockHandler = new CountDownLatch(1);
    CountDownLatch otherChannelHandled = new CountDownLatch(1);
    AtomicReference<IBuffer> blockedBuffer = new AtomicReference<>();
    try
    {
      IChannel blockedChannel = connector.openInverse((short)-18);
      blockedChannel.setReceiveHandler(buffer -> {
        blockedBuffer.set(buffer);
        blockedHandlerEntered.countDown();
        try
        {
          if (!unblockHandler.await(5, TimeUnit.SECONDS))
          {
            throw new AssertionError("The other channel did not unblock the handler"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new AssertionError(ex);
        }
        finally
        {
          buffer.release();
        }
      });

      IChannel progressChannel = connector.openInverse((short)-19);
      progressChannel.setReceiveHandler(buffer -> {
        buffer.release();
        unblockHandler.countDown();
        otherChannelHandled.countDown();
      });

      ByteBuffer blocked = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      blocked.putShort((short)-18).putShort((short)1).put((byte)0x1A).flip();
      connection.receive(blocked, () -> {
      });
      assertTrue("The first channel handler did not block", blockedHandlerEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitRequests(connection, 2);

      ByteBuffer progress = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      progress.putShort((short)-19).putShort((short)1).put((byte)0x1B).flip();
      connection.receive(progress, () -> {
      });
      assertTrue("The second channel did not run while the first handler was blocked", otherChannelHandled.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("The blocked handler did not return", unblockHandler.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertNotNull(blockedBuffer.get());
    }
    finally
    {
      unblockHandler.countDown();
      IBuffer buffer = blockedBuffer.get();
      if (buffer != null && !isReleased(buffer))
      {
        buffer.release();
      }

      LifecycleUtil.deactivate(connector);
    }
  }

  public void testCopyBudgetExhaustionFailsConnectionAndReleasesQueuedBuffers() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection, 0, 5);
    AtomicReference<IBuffer> retained = new AtomicReference<>();
    AtomicInteger providerReleases = new AtomicInteger();
    CountDownLatch firstDelivered = new CountDownLatch(1);
    try
    {
      IChannel channel = connector.openInverse((short)-20);
      channel.setReceiveHandler(buffer -> {
        retained.set(buffer);
        firstDelivered.countDown();
      });

      ByteBuffer first = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      first.putShort((short)-20).putShort((short)1).put((byte)0x2A).flip();
      connection.receive(first, providerReleases::incrementAndGet);
      assertTrue("The first copied frame was not delivered", firstDelivered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitRequests(connection, 2);

      ByteBuffer second = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      second.putShort((short)-20).putShort((short)1).put((byte)0x2B).flip();
      connection.receive(second, providerReleases::incrementAndGet);
      awaitInactive(connector);

      assertTrue("The connection was not aborted after copy-memory exhaustion", connection.aborted); //$NON-NLS-1$
      assertEquals("The rejected frame's provider lease was not released", 2, providerReleases.get()); //$NON-NLS-1$
      assertNotNull(retained.get());
      assertEquals("The retained application buffer remains owned until its handler releases it", BufferState.GETTING, retained.get().getState()); //$NON-NLS-1$
      assertEquals((byte)0x2A, retained.get().getByteBuffer().get(IBuffer.HEADER_SIZE));
      retained.get().release();
      assertEquals("The retained copy was not released exactly once", BufferState.RELEASED, retained.get().getState()); //$NON-NLS-1$
    }
    finally
    {
      IBuffer buffer = retained.get();
      if (buffer != null && !isReleased(buffer))
      {
        buffer.release();
      }

      LifecycleUtil.deactivate(connector);
    }
  }

  public void testCloseReleasesPendingReceiveDispatchButRetainsActiveBuffer() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection, 20, 20);
    CountDownLatch handlerEntered = new CountDownLatch(1);
    CountDownLatch releaseHandler = new CountDownLatch(1);
    CountDownLatch handlerFinished = new CountDownLatch(1);
    AtomicInteger firstProviderRelease = new AtomicInteger();
    AtomicInteger secondProviderRelease = new AtomicInteger();
    try
    {
      IChannel channel = connector.openInverse((short)-21);
      channel.setReceiveHandler(buffer -> {
        try
        {
          handlerEntered.countDown();

          if (!releaseHandler.await(5, TimeUnit.SECONDS))
          {
            throw new AssertionError("The test did not release the active receive handler"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new AssertionError(ex);
        }
        finally
        {
          buffer.release();
          handlerFinished.countDown();
        }
      });

      ByteBuffer first = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      first.putShort((short)-21).putShort((short)1).put((byte)0x31).flip();
      connection.receive(first, firstProviderRelease::incrementAndGet);
      assertTrue("The first receive handler did not block", handlerEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      awaitRequests(connection, 2);

      ByteBuffer second = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      second.putShort((short)-21).putShort((short)1).put((byte)0x32).flip();
      connection.receive(second, secondProviderRelease::incrementAndGet);

      LifecycleUtil.deactivate(connector);
      assertEquals("Closing did not release the queued receive buffer", 1, secondProviderRelease.get()); //$NON-NLS-1$
      assertEquals("Closing released the buffer still owned by the active handler", 0, firstProviderRelease.get()); //$NON-NLS-1$
      releaseHandler.countDown();
      assertTrue("The active receive handler did not finish", handlerFinished.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("The active provider buffer was not released exactly once", 1, firstProviderRelease.get()); //$NON-NLS-1$
    }
    finally
    {
      releaseHandler.countDown();
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testCloseDuringCopyReleasesAcceptedProviderLeaseOnce() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection, 0, 5);
    CountDownLatch providerReleaseStarted = new CountDownLatch(1);
    CountDownLatch finishProviderRelease = new CountDownLatch(1);
    AtomicReference<IBuffer> delivered = new AtomicReference<>();
    AtomicInteger providerReleases = new AtomicInteger();
    Thread receiveThread = new Thread(() -> {
      ByteBuffer frame = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      frame.putShort((short)-22).putShort((short)1).put((byte)0x41).flip();
      connection.receive(frame, () -> {
        providerReleases.incrementAndGet();
        providerReleaseStarted.countDown();
        try
        {
          finishProviderRelease.await();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ex);
        }
      });
    }, "Receive-copy-race"); //$NON-NLS-1$

    try
    {
      IChannel channel = connector.openInverse((short)-22);
      channel.setReceiveHandler(delivered::set);
      receiveThread.start();
      assertTrue("The copied frame did not reach provider release", providerReleaseStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      LifecycleUtil.deactivate(connector);
      finishProviderRelease.countDown();
      receiveThread.join(5000);
      assertFalse("The receive callback did not finish after shutdown", receiveThread.isAlive()); //$NON-NLS-1$
      assertEquals("Copying released the provider payload more than once", 1, providerReleases.get()); //$NON-NLS-1$
      assertNull("A copied frame was delivered after its generation had closed", delivered.get()); //$NON-NLS-1$
      assertEquals("Shutdown allowed another receive request", 1, connection.requested.get()); //$NON-NLS-1$
    }
    finally
    {
      finishProviderRelease.countDown();
      LifecycleUtil.deactivate(connector);
      receiveThread.join(1000);
    }
  }

  public void testUnknownChannelAndTruncatedFrameReleaseOwnership() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    try
    {
      AtomicInteger unknownRelease = new AtomicInteger();
      ByteBuffer unknown = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      unknown.putShort((short)55).putShort((short)1).put((byte)0x11).flip();
      connection.receive(unknown, unknownRelease::incrementAndGet);
      awaitRequests(connection, 2);
      assertEquals(1, unknownRelease.get());

      AtomicInteger truncatedRelease = new AtomicInteger();
      connection.receive(ByteBuffer.allocate(3), truncatedRelease::incrementAndGet);
      awaitInactive(connector);
      assertEquals(1, truncatedRelease.get());
      assertEquals("Truncated input must not replenish receive demand", 2, connection.requested.get()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testInvalidChannelIDReleasesAndAborts() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    try
    {
      AtomicInteger released = new AtomicInteger();
      ByteBuffer frame = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      frame.putShort(IBuffer.NO_CHANNEL).putShort((short)1).put((byte)0x44).flip();
      connection.receive(frame, released::incrementAndGet);
      awaitInactive(connector);
      assertEquals(1, released.get());
      assertEquals("Invalid frame must not receive new demand", 1, connection.requested.get()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testReceiveHandlerExceptionReleasesBuffer() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    try
    {
      IChannel channel = connector.openInverse((short)-11);
      channel.setReceiveHandler(buffer -> {
        throw new IllegalStateException("handler failed"); //$NON-NLS-1$
      });

      AtomicInteger released = new AtomicInteger();
      ByteBuffer frame = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      frame.putShort((short)-11).putShort((short)1).put((byte)0x22).flip();
      connection.receive(frame, released::incrementAndGet);
      awaitInactive(connector);
      assertEquals(1, released.get());
      assertEquals("An accepted frame replenishes demand before its handler fails", 2, connection.requested.get()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testMalformedFrameReleasesStorageAndStopsConnection() throws Exception
  {
    MemoryConnection connection = new MemoryConnection();
    TestConnector connector = activate(connection);
    try
    {
      AtomicInteger released = new AtomicInteger();
      ByteBuffer frame = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN);
      frame.putShort((short)1).putShort((short)2).put((byte)0x5A).flip();
      connection.receive(frame, released::incrementAndGet);
      awaitInactive(connector);
      assertEquals(1, released.get());
      assertEquals("Malformed input must not receive new demand", 1, connection.requested.get()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  private TestConnector activate(MemoryConnection connection) throws Exception
  {
    return activate(connection, -1, -1);
  }

  private TestConnector activate(MemoryConnection connection, long zeroCopyBudget, long copyBudget) throws Exception
  {
    TestConnector connector = zeroCopyBudget < 0 ? new TestConnector() : new TestConnector(zeroCopyBudget, copyBudget);
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)128));
    connector.setConnection(connection);
    LifecycleUtil.activate(connector);
    assertEquals(ConnectorState.CONNECTED, connector.getState());
    return connector;
  }

  private static IBuffer enqueue(TestConnector connector, IChannel channel)
  {
    IBuffer buffer = connector.provideBuffer();
    buffer.startPutting(channel.getID()).put((byte)0x42);
    channel.sendBuffer(buffer);
    return buffer;
  }

  private static ByteBuffer registrationFrame(short channelID)
  {
    byte[] protocolID = "test.protocol".getBytes(StandardCharsets.UTF_8); //$NON-NLS-1$
    ByteBuffer frame = ByteBuffer.allocate(IBuffer.HEADER_SIZE + 1 + Short.BYTES + Integer.BYTES + Short.BYTES + protocolID.length).order(ByteOrder.BIG_ENDIAN);
    frame.position(IBuffer.HEADER_SIZE);
    frame.put((byte)2);
    frame.putShort(channelID);
    frame.putInt(0);
    frame.putShort((short)protocolID.length);
    frame.put(protocolID);
    int messageSize = frame.position();
    frame.putShort(IBuffer.CHANNEL_ID_POS, (short)0);
    frame.putShort(IBuffer.PAYLOAD_SIZE_POS, (short)(messageSize - IBuffer.HEADER_SIZE));
    frame.position(0);
    frame.limit(messageSize);
    return frame;
  }

  private static ByteBuffer dataFrame(short channelID)
  {
    ByteBuffer frame = ByteBuffer.allocate(IBuffer.HEADER_SIZE + 1).order(ByteOrder.BIG_ENDIAN);
    frame.position(IBuffer.HEADER_SIZE);
    frame.put((byte)0x5A);
    frame.putShort(IBuffer.CHANNEL_ID_POS, channelID);
    frame.putShort(IBuffer.PAYLOAD_SIZE_POS, (short)1);
    frame.position(0);
    frame.limit(IBuffer.HEADER_SIZE + 1);
    return frame;
  }

  private static ByteBuffer signalFrame(short channelID, int correlationID)
  {
    ByteBuffer frame = ByteBuffer.allocate(IBuffer.HEADER_SIZE + Integer.BYTES + 1).order(ByteOrder.BIG_ENDIAN);
    frame.position(IBuffer.HEADER_SIZE);
    frame.putInt(correlationID).put((byte)0x5A);
    int messageSize = frame.position();
    frame.putShort(IBuffer.CHANNEL_ID_POS, channelID);
    frame.putShort(IBuffer.PAYLOAD_SIZE_POS, (short)(messageSize - IBuffer.HEADER_SIZE));
    frame.position(0);
    frame.limit(messageSize);
    return frame;
  }

  private static String acknowledgementError(IBuffer buffer)
  {
    if (buffer == null)
    {
      return "missing"; //$NON-NLS-1$
    }

    ByteBuffer byteBuffer = buffer.getByteBuffer().duplicate().order(ByteOrder.BIG_ENDIAN);
    byteBuffer.position(IBuffer.HEADER_SIZE + Byte.BYTES + Short.BYTES);
    int length = byteBuffer.getShort();
    if (length < 0)
    {
      return "<null>"; //$NON-NLS-1$
    }

    byte[] bytes = new byte[length];
    byteBuffer.get(bytes);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private static void awaitReleased(IBuffer buffer) throws InterruptedException
  {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (buffer.getState() != BufferState.RELEASED && buffer.getState() != BufferState.DISPOSED && System.nanoTime() < deadline)
    {
      Thread.yield();
    }

    assertTrue(buffer.getState() == BufferState.RELEASED || buffer.getState() == BufferState.DISPOSED);
  }

  private static void assertReleased(IBuffer buffer)
  {
    assertTrue(buffer.getState() == BufferState.RELEASED || buffer.getState() == BufferState.DISPOSED);
  }

  private static void awaitInactive(TestConnector connector) throws InterruptedException
  {
    assertTrue("Connector did not deactivate", LifecycleUtil.waitFor(connector, TimeUnit.SECONDS.toMillis(5), LifecycleState.INACTIVE)); //$NON-NLS-1$
  }

  private static void awaitRequests(MemoryConnection connection, int expected) throws InterruptedException
  {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (connection.requested.get() < expected && System.nanoTime() < deadline)
    {
      Thread.yield();
    }

    assertEquals(expected, connection.requested.get());
  }

  private static boolean isReleased(IBuffer buffer)
  {
    return buffer.getState() == BufferState.RELEASED || buffer.getState() == BufferState.DISPOSED;
  }

  /**
   * Supplies the negotiation context required by connector activation.
   *
   * @author Eike Stepper
   */
  private static final class TestConnector extends MessageConnector
  {
    private boolean skipPeerRegistration;

    private CountDownLatch protocolCreationEntered;

    private CountDownLatch allowProtocolCreation;

    private CountDownLatch dataFrameReceived;

    private CountDownLatch independentDataFrameReceived;

    private TestConnector()
    {
    }

    private TestConnector(long zeroCopyBudget, long copyBudget)
    {
      super(zeroCopyBudget, copyBudget);
    }

    private void setSkipPeerRegistration(boolean skipPeerRegistration)
    {
      this.skipPeerRegistration = skipPeerRegistration;
    }

    @Override
    protected void registerChannelWithPeer(short channelID, long timeout, IProtocol<?> protocol) throws ChannelException
    {
      if (!skipPeerRegistration)
      {
        super.registerChannelWithPeer(channelID, timeout, protocol);
      }
    }

    private void blockProtocolCreation(CountDownLatch entered, CountDownLatch allow, CountDownLatch received)
    {
      protocolCreationEntered = entered;
      allowProtocolCreation = allow;
      dataFrameReceived = received;
    }

    private void setIndependentDataFrameReceived(CountDownLatch received)
    {
      independentDataFrameReceived = received;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <INFRA_STRUCTURE> IProtocol<INFRA_STRUCTURE> createProtocol(String type, INFRA_STRUCTURE infraStructure)
    {
      CountDownLatch entered = protocolCreationEntered;
      CountDownLatch allow = allowProtocolCreation;
      if (entered != null)
      {
        entered.countDown();
        try
        {
          if (!allow.await(5, TimeUnit.SECONDS))
          {
            throw new AssertionError("The test did not release control-channel protocol creation"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new AssertionError(ex);
        }
      }

      return (IProtocol<INFRA_STRUCTURE>)Proxy.newProxyInstance(IProtocol2.class.getClassLoader(), new Class<?>[] { IProtocol2.class },
          (proxy, method, args) -> {
            if ("getType".equals(method.getName())) //$NON-NLS-1$
            {
              return type;
            }

            if ("getVersion".equals(method.getName())) //$NON-NLS-1$
            {
              return 0;
            }

            if ("handleBuffer".equals(method.getName())) //$NON-NLS-1$
            {
              ((IBuffer)args[0]).release();
              CountDownLatch received = type == null ? independentDataFrameReceived : dataFrameReceived;
              if (received != null)
              {
                received.countDown();
              }
            }

            return null;
          });
    }

    public IChannel openInverse(short channelID)
    {
      return inverseOpenChannel(channelID, null, 0);
    }
  }

  /**
   * A real signal protocol used to verify connector failure propagation to a pending confirmation.
   *
   * @author Eike Stepper
   */
  private static final class TestSignalProtocol extends SignalProtocol<Object>
  {
    private TestSignalProtocol()
    {
      super("test.signal"); //$NON-NLS-1$
    }
  }

  /**
   * Pauses the first channel lookup after returning the channel, exposing removal before receive-queue creation.
   *
   * @author Eike Stepper
   */
  private static final class BlockingChannelMap extends ConcurrentHashMap<Short, IChannel>
  {
    private static final long serialVersionUID = 1L;

    private final short blockedChannelID;

    private final CountDownLatch channelResolved;

    private final CountDownLatch continueLookup;

    private final CountDownLatch channelRemoved;

    private final AtomicInteger lookupBlocked = new AtomicInteger();

    private BlockingChannelMap(short blockedChannelID, CountDownLatch channelResolved, CountDownLatch continueLookup, CountDownLatch channelRemoved)
    {
      this.blockedChannelID = blockedChannelID;
      this.channelResolved = channelResolved;
      this.continueLookup = continueLookup;
      this.channelRemoved = channelRemoved;
    }

    @Override
    public IChannel get(Object key)
    {
      IChannel channel = super.get(key);
      if (channel != null && key.equals(blockedChannelID) && lookupBlocked.compareAndSet(0, 1))
      {
        channelResolved.countDown();
        try
        {
          if (!continueLookup.await(5, TimeUnit.SECONDS))
          {
            throw new IllegalStateException("The test did not release channel lookup"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      return channel;
    }

    @Override
    public boolean remove(Object key, Object value)
    {
      boolean removed = super.remove(key, value);
      if (removed && key.equals(blockedChannelID))
      {
        channelRemoved.countDown();
      }

      return removed;
    }
  }

  /**
   * An in-memory provider that exposes deterministic send and receive completion controls to the tests.
   *
   * @author Eike Stepper
   */
  private static final class MemoryConnection implements IMessageConnection
  {
    private final AtomicInteger requested = new AtomicInteger();

    private final AtomicInteger sendCount = new AtomicInteger();

    private final AtomicInteger startCalls = new AtomicInteger();

    private final AtomicInteger abortCount = new AtomicInteger();

    private final CountDownLatch firstAttempt = new CountDownLatch(1);

    private final CountDownLatch sent = new CountDownLatch(1);

    private final CountDownLatch secondAttempt = new CountDownLatch(2);

    private final CountDownLatch firstPendingSend = new CountDownLatch(1);

    private final CountDownLatch secondPendingSend = new CountDownLatch(1);

    private final Queue<PendingSend> pending = new ArrayDeque<>();

    private volatile Listener listener;

    private volatile IBuffer lastBuffer;

    private volatile boolean rejectNext;

    private volatile boolean completeImmediately;

    private volatile boolean aborted;

    private volatile boolean gracefulCloseRequested;

    private volatile boolean deferAbortCompletions;

    private volatile RuntimeException abortFailure;

    private volatile RuntimeException requestFailure;

    private volatile IMessageConnection.SendCallback lastCallback;

    private volatile Throwable closeDuringStart;

    private volatile RuntimeException startFailure;

    private volatile boolean blockStart;

    private volatile boolean autoReadyOnStart = true;

    private final CountDownLatch startEntered = new CountDownLatch(1);

    private final CountDownLatch allowStart = new CountDownLatch(1);

    private volatile boolean blockSubmission;

    private volatile boolean rejectBlockedSubmission;

    private volatile boolean throwBlockedSubmission;

    private volatile boolean completeBlockedSubmission;

    private final CountDownLatch submissionEntered = new CountDownLatch(1);

    private final CountDownLatch allowSubmission = new CountDownLatch(1);

    private final CountDownLatch submissionOutcomeReady = new CountDownLatch(1);

    @Override
    public void start(Listener listener)
    {
      startCalls.incrementAndGet();
      this.listener = listener;
      startEntered.countDown();
      if (blockStart)
      {
        try
        {
          allowStart.await();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      if (startFailure != null)
      {
        throw startFailure;
      }

      if (closeDuringStart == null)
      {
        if (autoReadyOnStart)
        {
          listener.ready();
        }
      }
      else
      {
        listener.closed(closeDuringStart);
      }
    }

    @Override
    public boolean send(IBuffer buffer, SendCallback callback)
    {
      sendCount.incrementAndGet();
      firstAttempt.countDown();
      if (blockSubmission)
      {
        submissionEntered.countDown();
        try
        {
          allowSubmission.await();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }

        if (rejectBlockedSubmission)
        {
          return false;
        }

        if (throwBlockedSubmission)
        {
          lastBuffer = buffer;
          submissionOutcomeReady.countDown();
          throw new IllegalStateException("submission failed after shutdown"); //$NON-NLS-1$
        }

        if (completeBlockedSubmission)
        {
          lastBuffer = buffer;
          lastCallback = callback;
          callback.failed(new IllegalStateException("cancelled during shutdown")); //$NON-NLS-1$
          sent.countDown();
          return true;
        }
      }

      if (rejectNext)
      {
        rejectNext = false;
        return false;
      }

      lastBuffer = buffer;
      lastCallback = callback;
      sent.countDown();
      secondAttempt.countDown();
      if (completeImmediately)
      {
        callback.completed();
      }
      else
      {
        synchronized (pending)
        {
          pending.add(new PendingSend(callback));
        }

        if (sendCount.get() == 1)
        {
          firstPendingSend.countDown();
        }
        else if (sendCount.get() == 2)
        {
          secondPendingSend.countDown();
        }
      }

      return true;
    }

    @Override
    public void request(long messages)
    {
      requested.addAndGet((int)messages);
      RuntimeException failure = requestFailure;
      if (failure != null)
      {
        requestFailure = null;
        throw failure;
      }
    }

    @Override
    public void closeGracefully()
    {
      gracefulCloseRequested = true;
      listener.closed(null);
    }

    @Override
    public void abort()
    {
      abortCount.incrementAndGet();
      aborted = true;
      if (abortFailure != null)
      {
        throw abortFailure;
      }

      if (!deferAbortCompletions)
      {
        PendingSend[] sends;
        synchronized (pending)
        {
          sends = pending.toArray(new PendingSend[pending.size()]);
          pending.clear();
        }

        for (PendingSend send : sends)
        {
          send.callback.failed(new IllegalStateException("aborted")); //$NON-NLS-1$
        }
      }
    }

    private void writable()
    {
      listener.writable();
    }

    private void complete()
    {
      PendingSend send;
      synchronized (pending)
      {
        send = pending.poll();
      }

      if (send != null)
      {
        send.callback.completed();
      }
    }

    private void receive(ByteBuffer data, Runnable releaseAction)
    {
      listener.received(data, releaseAction);
    }

    private void fail(Throwable failure)
    {
      IMessageConnection.SendCallback callback = lastCallback;
      if (callback != null)
      {
        synchronized (pending)
        {
          pending.removeIf(send -> send.callback == callback);
        }

        callback.failed(failure);
      }
    }

    private void closeRemotely(Throwable cause)
    {
      listener.closed(cause);
    }
  }

  /**
   * Records one accepted in-memory send and its completion callback.
   *
   * @author Eike Stepper
   */
  private static final class PendingSend
  {
    private final IMessageConnection.SendCallback callback;

    private PendingSend(IMessageConnection.SendCallback callback)
    {
      this.callback = callback;
    }
  }

  /**
   * Counts the release of buffers supplied for the focused ownership test.
   *
   * @author Eike Stepper
   */
  private static final class CountingBufferProvider implements IBufferProvider
  {
    private final IBufferProvider delegate;

    private final AtomicInteger retainedBuffers = new AtomicInteger();

    private volatile IBuffer trackedBuffer;

    private CountingBufferProvider(IBufferProvider delegate)
    {
      this.delegate = delegate;
    }

    @Override
    public short getBufferCapacity()
    {
      return delegate.getBufferCapacity();
    }

    @Override
    public IBuffer provideBuffer()
    {
      IBuffer buffer = delegate.provideBuffer();
      ((InternalBuffer)buffer).setBufferProvider(this);
      return buffer;
    }

    private void track(IBuffer buffer)
    {
      trackedBuffer = buffer;
    }

    @Override
    public void retainBuffer(IBuffer buffer)
    {
      if (buffer == trackedBuffer)
      {
        retainedBuffers.incrementAndGet();
      }

      delegate.retainBuffer(buffer);
    }
  }

}
