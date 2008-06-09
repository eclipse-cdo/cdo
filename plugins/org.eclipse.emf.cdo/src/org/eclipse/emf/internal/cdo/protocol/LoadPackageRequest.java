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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadPackageRequest extends CDOClientRequest<Object>
{
  private CDOPackage cdoPackage;

  public LoadPackageRequest(IChannel channel, CDOPackage cdoPackage)
  {
    super(channel);
    this.cdoPackage = cdoPackage;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_PACKAGE;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(cdoPackage.getPackageURI());
  }

  @Override
  protected Object confirming(ExtendedDataInputStream in) throws IOException
  {
    ((InternalCDOPackage)cdoPackage).read(in);
    return null;
  }
}
