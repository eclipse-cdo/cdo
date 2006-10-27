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
package org.eclipse.net4j.transport;

/**
 * @author Eike Stepper
 */
public interface BufferHandler
{
  /**
   * Handles a {@link Buffer} and optionally releases it. The implementor of
   * this method takes over the ownership of the buffer. Care must be taken to
   * properly {@link Buffer#release() release} the buffer if the ownership is
   * not explicitely passed to some further party.
   * <p>
   */
  public void handleBuffer(Buffer buffer);
}