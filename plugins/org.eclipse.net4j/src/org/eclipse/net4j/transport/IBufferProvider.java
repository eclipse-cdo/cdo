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
package org.eclipse.net4j.transport;

/**
 * @author Eike Stepper
 */
public interface IBufferProvider
{
  public short getBufferCapacity();

  public IBuffer provideBuffer();

  public void retainBuffer(IBuffer buffer);

  /**
   * @author Eike Stepper
   */
  public interface Introspection extends IBufferProvider
  {
    public long getProvidedBuffers();

    public long getRetainedBuffers();
  }
}