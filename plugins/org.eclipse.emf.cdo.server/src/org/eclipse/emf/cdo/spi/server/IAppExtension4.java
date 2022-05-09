/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import java.util.Comparator;

/**
 * An optional extension of the {@link IAppExtension} interface for {@link #getPriority() prioritized} app extensions.
 *
 * @author Eike Stepper
 * @since 4.12
 */
public interface IAppExtension4 extends IAppExtension
{
  public static final int PRIORITY_NETWORK = 10;

  public static final int PRIORITY_SECURITY = 100;

  public static final int PRIORITY_DEFAULT = 1000;

  public static final Comparator<IAppExtension> COMPARATOR = (e1, e2) -> {
    int p1 = getPriority(e1);
    int p2 = getPriority(e2);
    return Integer.compare(p1, p2);
  };

  public int getPriority();

  public static int getPriority(IAppExtension appExtension)
  {
    if (appExtension instanceof IAppExtension4)
    {
      return ((IAppExtension4)appExtension).getPriority();
    }

    return PRIORITY_DEFAULT;
  }
}
