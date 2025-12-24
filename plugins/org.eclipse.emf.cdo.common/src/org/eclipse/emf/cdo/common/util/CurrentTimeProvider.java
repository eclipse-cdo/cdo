/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

/**
 * @author Eike Stepper
 * @since 4.6
 */
public class CurrentTimeProvider implements CDOTimeProvider
{
  public static final CDOTimeProvider INSTANCE = new CurrentTimeProvider();

  private CurrentTimeProvider()
  {
  }

  @Override
  public long getTimeStamp()
  {
    return System.currentTimeMillis();
  }
}
