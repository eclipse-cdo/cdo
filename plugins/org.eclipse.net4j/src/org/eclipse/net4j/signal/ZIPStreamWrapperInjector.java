/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.io.ZIPStreamWrapper;

/**
 * @author Eike Stepper
 */
public class ZIPStreamWrapperInjector extends StreamWrapperInjector
{
  public static final ZIPStreamWrapper STREAM_WRAPPER = new ZIPStreamWrapper();

  public ZIPStreamWrapperInjector(String protocolID)
  {
    super(protocolID, STREAM_WRAPPER);
  }
}
