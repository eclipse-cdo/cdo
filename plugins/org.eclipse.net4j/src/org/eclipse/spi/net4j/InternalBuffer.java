/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalBuffer extends IBuffer
{
  public void setBufferProvider(IBufferProvider bufferProvider);

  public void dispose();
}
