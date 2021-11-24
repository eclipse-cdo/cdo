/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.internal.ui.bundle;

import org.eclipse.emf.cdo.transfer.ui.TransferLabelProvider;
import org.eclipse.emf.cdo.transfer.ui.swt.TransferDetailsComposite.UnmappedModelsLabelProvider;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;
import org.eclipse.net4j.util.ui.UIUtil;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.transfer.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator
  {
    public static Activator INSTANCE;

    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      INSTANCE = this;

      UIUtil.syncExec(() -> {
        // Bug 577425: If the classes below are first accessed from a non-UI thread SWT throws "Invalid thread access".
        TransferLabelProvider.GRAY.isDisposed();
        UnmappedModelsLabelProvider.GRAY.isDisposed();
      });
    }

    @Override
    protected void doStop() throws Exception
    {
      INSTANCE = null;
    }
  }
}
