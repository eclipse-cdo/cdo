/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.helper.Progress;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.p2.ui.ProvisioningUI;

import java.net.URI;

/**
 * @author Eike Stepper
 */
public class P2
{
  private static final SetupContext CONTEXT = Activator.getDefault();

  public static void registerUpdateLocations() throws Exception
  {
    Setup setup = CONTEXT.getSetup();
    for (P2Repository p2Repository : setup.getUpdateLocations())
    {
      URI location = new URI(p2Repository.getUrl());
      Progress.log().addLine("Registering update location: " + location);
      addRepository(location);
    }
  }

  private static void addRepository(URI location) throws Exception
  {
    ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();
    provisioningUI.loadMetadataRepository(location, false, new NullProgressMonitor());
    provisioningUI.loadArtifactRepository(location, false, new NullProgressMonitor());
  }
}
