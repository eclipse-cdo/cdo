/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0.
 */
package org.eclipse.emf.cdo.internal.common.revision;

/**
 * The built-in equals-equality CDO list with element proxies.
 *
 * @author Eike Stepper
 */
public class CDOListWithElementProxiesWithEqualsImpl extends CDOListWithElementProxiesImpl
{
  private static final long serialVersionUID = 1L;

  public CDOListWithElementProxiesWithEqualsImpl(int initialCapacity, int size, int initialChunk)
  {
    super(initialCapacity, size, initialChunk);
  }

  @Override
  protected boolean useEquals()
  {
    return true;
  }
}
