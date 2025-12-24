/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.cache;

/**
 * @author Eike Stepper
 */
public interface ICacheProbe
{
  public boolean isDisposed();

  public void elementCached(int elementSize);

  public void elementEvicted(int elementSize);

  public void elementReconstructed(long reconstructionTime);

  public int getElementCount();

  public long getCacheSize();

  public long getAverageElementSize();

  public long getReconstructionCost();
}
