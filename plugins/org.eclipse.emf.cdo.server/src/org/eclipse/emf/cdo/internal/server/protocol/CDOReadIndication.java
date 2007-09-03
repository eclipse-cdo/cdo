/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.server.StoreUtil;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreReader;

import org.eclipse.net4j.stream.BufferInputStream;
import org.eclipse.net4j.stream.BufferOutputStream;

/**
 * @author Eike Stepper
 */
public abstract class CDOReadIndication extends CDOServerIndication
{
  public CDOReadIndication()
  {
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    IStore store = getStore();
    IStoreReader storeReader = store.getReader(getSession());

    try
    {
      StoreUtil.setReader(storeReader);
      super.execute(in, out);
    }
    finally
    {
      storeReader.release();
      StoreUtil.setReader(null);
    }
  }
}
