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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadPackageIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadPackageIndication.class);

  private CDOPackageImpl cdoPackage;

  public LoadPackageIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_PACKAGE;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    String packageURI = in.readString();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read packageURI: {0}", packageURI);
    }

    cdoPackage = getPackageManager().lookupPackage(packageURI);
    if (cdoPackage == null)
    {
      throw new ImplementationError("CDO package not found: " + packageURI);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing package: {0}", cdoPackage);
    }

    cdoPackage.write(out);
  }
}
