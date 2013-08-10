/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common;

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionUnchunker;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;

import org.eclipse.net4j.util.io.ExtendedDataOutput;


/**
 * Maintenance workaround.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public final class CDODataUtil
{

  /**
   * @since 4.2
   */
  public static CDODataOutput createCDODataOutput(ExtendedDataOutput extendedDataOutputStream,
      final CDOPackageRegistry packageRegistry, final CDOIDProvider idProvider, final CDORevisionUnchunker unchunker)
  {
    return new CDODataOutputImpl(extendedDataOutputStream)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }
  
      @Override
      public CDOIDProvider getIDProvider()
      {
        return idProvider;
      }
  
      @Override
      public CDORevisionUnchunker getRevisionUnchunker()
      {
        return unchunker;
      }
    };
  }
}
