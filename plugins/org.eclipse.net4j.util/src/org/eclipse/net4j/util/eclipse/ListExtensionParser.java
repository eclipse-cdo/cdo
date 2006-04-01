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


import java.util.ArrayList;
import java.util.List;


public class ListExtensionParser extends ExtensionParser
{
  protected List list;

  public ListExtensionParser()
  {
  }

  public ListExtensionParser(List list)
  {
    this.list = list;
  }

  public List getList()
  {
    if (list == null)
    {
      list = new ArrayList();
    }

    return list;
  }

  public void setList(List list)
  {
    this.list = list;
  }

  protected void validate(Element elementData)
  {
    super.validate(elementData);
    if (contextStack.isEmpty())
    {
      getList().add(elementData);
    }
  }
}