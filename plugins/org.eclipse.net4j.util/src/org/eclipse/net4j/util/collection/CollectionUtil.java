/*
 * Copyright (c) 2015, 2018, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Various static helper methods.
 *
 * @author Eike Stepper
 * @since 3.5
 */
public final class CollectionUtil
{
  private CollectionUtil()
  {
  }

  public static <T> Iterator<T> dump(Iterator<T> it)
  {
    List<T> list = new ArrayList<>();
    while (it.hasNext())
    {
      list.add(it.next());
    }

    System.out.println(list);
    return list.iterator();
  }

  /**
   * @since 3.16
   */
  public static <T> boolean addNotNull(Collection<? super T> c, T e)
  {
    if (e != null)
    {
      return c.add(e);
    }

    return false;
  }
}
