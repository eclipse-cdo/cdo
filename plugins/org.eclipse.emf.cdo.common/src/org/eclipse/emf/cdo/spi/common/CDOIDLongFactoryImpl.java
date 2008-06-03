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
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryProvider;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;

import org.eclipse.net4j.util.io.ExtendedDataInput;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public class CDOIDLongFactoryImpl implements CDOIDObjectFactory
{
  private LibraryHandler LIBRARY_HANDLER = new LibraryHandler();

  public CDOIDLongFactoryImpl()
  {
  }

  public CDOIDObject createCDOIDObject(ExtendedDataInput in)
  {
    return new CDOIDLongImpl();
  }

  public LibraryHandler getLibraryHandler()
  {
    return LIBRARY_HANDLER;
  }

  /**
   * @author Eike Stepper
   */
  public static final class LibraryHandler extends CDOIDLibraryDescriptorImpl implements CDOIDLibraryProvider
  {
    private LibraryHandler()
    {
      super(CDOIDLongFactoryImpl.class.getName(), null);
    }

    public int getSize(String libraryName)
    {
      throw new UnsupportedOperationException();
    }

    public InputStream getContents(String libraryName) throws IOException
    {
      throw new UnsupportedOperationException();
    }
  }
}
