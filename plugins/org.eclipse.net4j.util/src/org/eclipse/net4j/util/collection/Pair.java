/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.collection;

/**
 * @author Eike Stepper
 */
public class Pair<T1, T2>
{
  private T1 element1;

  private T2 element2;

  public Pair()
  {
  }

  public Pair(T1 element1, T2 element2)
  {
    this.element1 = element1;
    this.element2 = element2;
  }

  public final T1 getElement1()
  {
    return element1;
  }

  public void setElement1(T1 element1)
  {
    this.element1 = element1;
  }

  public final T2 getElement2()
  {
    return element2;
  }

  public void setElement2(T2 element2)
  {
    this.element2 = element2;
  }
}
