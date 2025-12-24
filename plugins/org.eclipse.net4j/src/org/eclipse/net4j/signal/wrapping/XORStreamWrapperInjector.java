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

import org.eclipse.net4j.util.io.XORStreamWrapper;

/**
 * An {@link StreamWrapperInjector injector} that injects {@link XORStreamWrapper} instances.
 *
 * @author Eike Stepper
 */
public class XORStreamWrapperInjector extends StreamWrapperInjector
{
  public XORStreamWrapperInjector(String protocolID, int[] key)
  {
    super(protocolID, new XORStreamWrapper(key));
  }
}
