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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RegisterPackagesRequest extends CDOClientRequest
{
  private Collection<CDOPackageImpl> newPackages;

  public RegisterPackagesRequest(IChannel channel, Collection<CDOPackageImpl> newPackages)
  {
    super(channel, CDOProtocolConstants.REGISTER_PACKAGES_SIGNAL);
    this.newPackages = newPackages;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(newPackages.size());
    for (CDOPackageImpl newPackage : newPackages)
    {
      newPackage.write(out);
    }
  }

  @Override
  protected Object confirming(ExtendedDataInputStream in) throws IOException
  {
    Set<String> knownPackages = getSession().getPackageURIs();
    for (CDOPackageImpl newPackage : newPackages)
    {
      knownPackages.add(newPackage.getPackageURI());
    }

    return null;
  }
}
