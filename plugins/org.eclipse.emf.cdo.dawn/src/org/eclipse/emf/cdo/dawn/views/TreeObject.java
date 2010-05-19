/*******************************************************************************
 * Copyright (c) 2009 - 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.views;


/**
 * @author Martin Fluegge
 */
public class TreeObject
{
  protected String name;

  private TreeParent parent;

  private Object obj;

  public TreeObject(String name, Object o)
  {
    this.name = name;
    obj = o;
  }

  public Object getObj()
  {
    return obj;
  }

  public void setObj(Object obj)
  {
    this.obj = obj;
  }

  public Object getName()
  {
    return obj;
  }

  public void setParent(TreeParent parent)
  {
    this.parent = parent;
  }

  public TreeParent getParent()
  {
    return parent;
  }

  @Override
  public String toString()
  {
    return name;
  }
}
