/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadPackagesIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadPackagesIndication.class);

  private String packageUnitID;

  public LoadPackagesIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_PACKAGES);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    packageUnitID = in.readCDOPackageURI();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read packageUnitID: {0}", packageUnitID);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalCDOPackageRegistry packageRegistry = getRepository().getPackageRegistry();
    EPackage ePackage = packageRegistry.getEPackage(packageUnitID);
    if (ePackage == null)
    {
      throw new IllegalStateException("Package unit not found: " + packageUnitID);
    }

    CDOModelUtil.writePackage(out, ePackage, true, packageRegistry);
  }
}
