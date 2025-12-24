/**
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.trace.aspectj;

import org.eclipse.net4j.internal.trace.BufferTracer;
import org.eclipse.net4j.internal.trace.BufferTracer.Call;

import java.util.Stack;

/**
 * @author Eike Stepper
 */
public aspect Aspect
{
  pointcut objectMethods() :
    execution(public * *.getClass()) ||
    execution(public * *.toString()) ||
    execution(public * *.equals(Object)) ||
    execution(public * *.hashCode()) ||
    execution(public * *.notify()) ||
    execution(public * *.notifyAll()) ||
    execution(public * *.wait()) ||
    execution(public * *.wait(long, int)) ||
    execution(public * *.wait(long)) ||
    execution(* *.clone()) ||
    execution(* *.finalize());

  pointcut unwantedMethods() :
    execution(* org.eclipse.internal.net4j.buffer.BufferPool.createBufferRef()) ||
    execution(* org.eclipse.internal.net4j.buffer.Buffer.getCapacity()) ||
    execution(* org.eclipse.internal.net4j.buffer.Buffer.getLimit()) ||
    execution(* org.eclipse.internal.net4j.buffer.Buffer.getPosition()) ||
    execution(* org.eclipse.internal.net4j.buffer.Buffer.hasRemaining()) ||
    execution(* org.eclipse.internal.net4j.buffer.Buffer.isEOS()) ||
    execution(* org.eclipse.internal.net4j.buffer.Buffer.isCCAM()) ||
    execution(* org.eclipse.net4j.buffer.BufferOutputStream.ensureBufferPrivate()) ||
    execution(* org.eclipse.net4j.buffer.BufferOutputStream.throwExceptionOnError()) ||
    execution(* org.eclipse.net4j.buffer.BufferOutputStream.flushIfFilled());

  pointcut relevantMethods(Object target) : target(target) &&
    !objectMethods() &&
    !unwantedMethods() &&
    (
      execution(* org.eclipse.internal.net4j.buffer.Buffer.*(..)) ||
      execution(* org.eclipse.internal.net4j.buffer.BufferPool.*(..)) ||
      execution(* org.eclipse.net4j.buffer.BufferInputStream.*(..)) ||
      execution(* org.eclipse.net4j.buffer.BufferOutputStream.*(..)) ||
      execution(* org.eclipse.spi.net4j.Channel.*(..)) ||
      execution(* org.eclipse.spi.net4j.Channel.SendQueue.*(..)) ||
      execution(* org.eclipse.spi.net4j.Channel.ReceiveSerializer2.*(..)) ||
      execution(* org.eclipse.net4j.channel.ChannelInputStream.*(..)) ||
      execution(* org.eclipse.net4j.channel.ChannelOutputStream.*(..)) ||
      execution(* org.eclipse.spi.net4j.ChannelMultiplexer.*(..)) ||
      execution(* org.eclipse.spi.net4j.Connector.*(..)) ||
      execution(* org.eclipse.spi.net4j.Protocol.*(..)) ||
      execution(* org.eclipse.net4j.signal.SignalProtocol.*(..)) ||
      execution(* org.eclipse.net4j.signal.SignalProtocol.SignalInputStream.*(..)) ||
      execution(* org.eclipse.net4j.signal.SignalProtocol.SignalOutputStream.*(..)) ||
      execution(* org.eclipse.net4j.signal.Signal.*(..)) ||
      execution(* org.eclipse.net4j.signal.SignalActor.*(..)) ||
      execution(* org.eclipse.net4j.signal.SignalReactor.*(..)) ||
      execution(* org.eclipse.net4j.internal.jvm.JVMConnector.*(..)) ||
      execution(* org.eclipse.net4j.internal.jvm.JVMChannel.*(..)) ||
      execution(* org.eclipse.net4j.internal.tcp.TCPConnector.*(..)) ||
      execution(* org.eclipse.net4j.internal.tcp.TCPClientConnector.*(..)) ||
      execution(* org.eclipse.net4j.internal.tcp.TCPServerConnector.*(..))
     );

  Object around(Object target) : relevantMethods(target)
  {
    String what = thisJoinPointStaticPart.getSignature().getName();
    Stack<Call> callStack = BufferTracer.execution(target, what, null);

    try
    {
      return proceed(target);
    }
    finally
    {
      BufferTracer.execution(target, what, callStack);
    }
  }

  before(Thread thread, String name) :
    execution(public static void org.eclipse.net4j.util.concurrent.ConcurrencyUtil.setThreadName(..)) &&
    args(thread, name)
  {
    BufferTracer.setThreadName(thread, name);
  }
}
