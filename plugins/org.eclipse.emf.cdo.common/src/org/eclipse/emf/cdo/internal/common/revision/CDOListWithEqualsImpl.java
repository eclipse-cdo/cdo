/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision;

/**
 * The built-in equals-equality CDO list.
 *
 *  @author Eike Stepper
 */
public class CDOListWithEqualsImpl extends CDOListImpl
{
  private static final long serialVersionUID = 1L;

  public CDOListWithEqualsImpl(int initialCapacity, int size)
  {
    super(initialCapacity, size);
  }

  @Override
  protected boolean useEquals()
  {
    return true;
  }
}
