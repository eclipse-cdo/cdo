/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;

import org.eclipse.internal.net4j.buffer.BufferFactory;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLBufferFactory extends BufferFactory
{
  private SSLEngineManager sslEngineManager;

  public SSLBufferFactory(short bufferCapacity)
  {
    super(bufferCapacity);
  }

  public SSLBufferFactory(SSLEngineManager sslEngineManager)
  {
    this(Net4jUtil.DEFAULT_BUFFER_CAPACITY);
    this.sslEngineManager = sslEngineManager;
  }

  @Override
  protected IBuffer doProvideBuffer()
  {
    return new SSLBuffer(this, getBufferCapacity(), sslEngineManager);
  }
}
