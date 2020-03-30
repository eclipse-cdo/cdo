/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buffer;

/**
 * Provides clients with the ability to pass {@link IBuffer}s in for further buffer handling.
 *
 * @author Eike Stepper
 */
public interface IBufferHandler
{
  /**
   * Handles an {@link IBuffer} and possibly releases it.
   * <p>
   * The implementor of this method takes over the ownership of
   * the buffer. Care must be taken to properly {@link IBuffer#release() release} the buffer if the ownership is not
   * explicitly passed on to some further party.
   *
   * @param buffer
   *          The buffer to be handled.
   */
  public void handleBuffer(IBuffer buffer);
}
