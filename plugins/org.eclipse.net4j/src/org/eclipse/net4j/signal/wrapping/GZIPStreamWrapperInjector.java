/*
 * Copyright (c) 2007, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal.wrapping;

import org.eclipse.net4j.util.io.GZIPStreamWrapper;

/**
 * An {@link StreamWrapperInjector injector} that injects {@link GZIPStreamWrapper} instances.
 *
 * @author Eike Stepper
 */
public class GZIPStreamWrapperInjector extends StreamWrapperInjector
{
  public static final GZIPStreamWrapper STREAM_WRAPPER = new GZIPStreamWrapper();

  public GZIPStreamWrapperInjector(String protocolID)
  {
    super(protocolID, STREAM_WRAPPER);
  }
}
