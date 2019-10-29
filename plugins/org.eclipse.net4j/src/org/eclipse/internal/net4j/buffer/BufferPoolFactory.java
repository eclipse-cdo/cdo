/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.om.OMPlatform;

/**
 * @author Eike Stepper
 */
public class BufferPoolFactory extends Factory
{
  public static final short BUFFER_CAPACITY = 4096;

  public static final String PRODUCT_GROUP = "org.eclipse.net4j.bufferProviders"; //$NON-NLS-1$

  public static final String TYPE = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.buffer.BufferPoolFactory.type", "default"); //$NON-NLS-1$

  public static final String DESCRIPTION = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.buffer.BufferPoolFactory.description");

  public BufferPoolFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public IBufferPool create(String description)
  {
    return Net4jUtil.createBufferPool(BUFFER_CAPACITY);
  }

  public static IBufferPool get(IManagedContainer container)
  {
    return (IBufferPool)container.getElement(PRODUCT_GROUP, TYPE, DESCRIPTION);
  }
}
