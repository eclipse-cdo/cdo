/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j;

import org.eclipse.net4j.IBufferProvider;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.internal.util.factory.Factory;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class BufferProviderFactory extends Factory<IBufferProvider>
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.bufferProviders";

  public static final String TYPE = "default";

  public static final short BUFFER_CAPACITY = 4096;

  public BufferProviderFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public IBufferProvider create(String description)
  {
    return Net4jUtil.createBufferPool(BUFFER_CAPACITY);
  }

  public static IBufferProvider get(IManagedContainer container)
  {
    return (IBufferProvider)container.getElement(PRODUCT_GROUP, TYPE, null);
  }
}
