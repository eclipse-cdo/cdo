/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.net4j.util.eclipse;


import java.util.HashMap;
import java.util.Map;


public class ElementRegistry extends HashMap
{
  /**
   * 
   */
  private static final long serialVersionUID = 3762810506670847288L;

  public ElementRegistry()
  {
    super();
  }

  public ElementRegistry(int initialCapacity)
  {
    super(initialCapacity);
  }

  public ElementRegistry(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }

  public ElementRegistry(Map m)
  {
    super(m);
  }

  public Object put(Item value)
  {
    Object key = value.registryKey();
    return super.put(key, value);
  }


  public interface Item
  {
    public Object registryKey();
  }
}