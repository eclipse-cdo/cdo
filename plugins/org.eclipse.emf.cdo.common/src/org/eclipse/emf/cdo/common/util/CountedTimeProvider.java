/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Eike Stepper
 * @since 4.6
 */
public final class CountedTimeProvider implements CDOTimeProvider
{
  private final AtomicLong counter;

  public CountedTimeProvider()
  {
    this(0L);
  }

  public CountedTimeProvider(long initialValue)
  {
    counter = new AtomicLong(initialValue);
  }

  @Override
  public long getTimeStamp()
  {
    return counter.getAndIncrement();
  }
}
