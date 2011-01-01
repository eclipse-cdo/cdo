/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.db4o.IDB4OStore;
import org.eclipse.emf.cdo.server.internal.db4o.bundle.OM;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OStoreFactory implements IStoreFactory
{
  public DB4OStoreFactory()
  {
  }

  public String getStoreType()
  {
    return IDB4OStore.TYPE;
  }

  public IStore createStore(Element storeConfig)
  {
    try
    {
      String dataFilePath = getFilePath(storeConfig);
      int port = getPort(storeConfig);
      return new DB4OStore(dataFilePath, port);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return null;
  }

  protected int getPort(Element storeConfig)
  {
    NodeList ooConfig = storeConfig.getElementsByTagName("ooData"); //$NON-NLS-1$
    Element ooElement = (Element)ooConfig.item(0);
    String port = ooElement.getAttribute("port"); //$NON-NLS-1$
    if (port == null)
    {
      throw new IllegalArgumentException("DB4O port not defined"); //$NON-NLS-1$
    }

    return Integer.parseInt(port);
  }

  protected String getFilePath(Element storeConfig)
  {
    NodeList ooConfig = storeConfig.getElementsByTagName("ooData"); //$NON-NLS-1$
    Element ooElement = (Element)ooConfig.item(0);
    String filePath = ooElement.getAttribute("path"); //$NON-NLS-1$
    if (filePath == null)
    {
      throw new IllegalArgumentException("DB4O file path not defined"); //$NON-NLS-1$
    }

    return filePath;
  }
}
