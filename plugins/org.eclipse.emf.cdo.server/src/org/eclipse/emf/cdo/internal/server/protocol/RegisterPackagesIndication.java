/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("unused")
public class RegisterPackagesIndication extends CDOServerIndication
{
  public RegisterPackagesIndication()
  {
    super(CDOProtocolConstants.REGISTER_PACKAGES_SIGNAL);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    CDOPackageManagerImpl packageManager = getPackageManager();
    int size = in.readInt();
    CDOPackageImpl[] newPackages = new CDOPackageImpl[size];
    for (int i = 0; i < size; i++)
    {
      newPackages[i] = new CDOPackageImpl(packageManager, in);
      packageManager.addPackage(newPackages[i]);
    }

    for (int i = 0; i < size; i++)
    {
      newPackages[i].initialize();
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
  }
}
