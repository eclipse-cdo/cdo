/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.jvm.IJVMChannel;

import org.eclipse.spi.net4j.Channel;

/**
 * @author Eike Stepper
 */
public class JVMChannel extends Channel implements IJVMChannel
{
  private JVMChannel peer;

  public JVMChannel()
  {
  }

  @Override
  public final JVMChannel getPeer()
  {
    return peer;
  }

  public final void setPeer(JVMChannel peer)
  {
    this.peer = peer;
  }
}
