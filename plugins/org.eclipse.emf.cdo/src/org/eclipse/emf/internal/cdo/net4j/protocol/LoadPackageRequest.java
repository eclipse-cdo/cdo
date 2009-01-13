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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackage;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadPackageRequest extends CDOClientRequest<Object>
{
  private CDOPackage cdoPackage;

  private boolean onlyEcore;

  public LoadPackageRequest(CDOClientProtocol protocol, CDOPackage cdoPackage, boolean onlyEcore)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_PACKAGE);
    this.cdoPackage = cdoPackage;
    this.onlyEcore = onlyEcore;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOPackageURI(cdoPackage.getPackageURI());
    out.writeBoolean(onlyEcore);
  }

  @Override
  protected Object confirming(CDODataInput in) throws IOException
  {
    if (onlyEcore)
    {
      String ecore = in.readString();
      ((InternalCDOPackage)cdoPackage).setEcore(ecore);
    }
    else
    {
      in.readCDOPackage(cdoPackage);
    }

    return null;
  }
}
