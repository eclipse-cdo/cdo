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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadPackageRequest extends CDOClientRequest<Object>
{
  private CDOPackageImpl cdoPackage;

  public LoadPackageRequest(IChannel channel, CDOPackageImpl cdoPackage)
  {
    super(channel, CDOProtocolConstants.SIGNAL_LOAD_PACKAGE);
    this.cdoPackage = cdoPackage;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(cdoPackage.getPackageURI());
  }

  @Override
  protected Object confirming(ExtendedDataInputStream in) throws IOException
  {
    cdoPackage.read(in);
    return null;
  }
}
