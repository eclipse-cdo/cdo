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

import java.util.ArrayList;

/**
 * @author Martin Fluegge
 */
public class TreeParent extends TreeObject
{
  private ArrayList<TreeObject> children;

  public TreeParent(String name, Object obj)
  {
    super(name, obj);
    children = new ArrayList<TreeObject>();
  }

  public void addChild(TreeObject child)
  {
    children.add(child);
    child.setParent(this);
  }

  public void removeChild(TreeObject child)
  {
    children.remove(child);
    child.setParent(null);
  }

  public TreeObject[] getChildren()
  {
    return children.toArray(new TreeObject[children.size()]);
  }

  public boolean hasChildren()
  {
    return children.size() > 0;
  }

  @Override
  public String toString()
  {
    return name + "(" + getChildren().length + ")";
  }
}
