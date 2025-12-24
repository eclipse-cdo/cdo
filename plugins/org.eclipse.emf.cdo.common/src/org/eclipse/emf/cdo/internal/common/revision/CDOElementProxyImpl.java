/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.revision.CDOElementProxy;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CDOElementProxyImpl implements CDOElementProxy
{
  private int index;

  public CDOElementProxyImpl(int index)
  {
    this.index = index;
  }

  @Override
  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOElementProxy[{0}]", index); //$NON-NLS-1$
  }
}
