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
package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public class Activator implements BundleActivator
{
  public void start(BundleContext context) throws Exception
  {
    AbstractOMPlatform.systemContext = context;
    AbstractOMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    Net4j.BUNDLE.setBundleContext(context);
  }

  public void stop(BundleContext context) throws Exception
  {
    Net4j.BUNDLE.setBundleContext(null);
    AbstractOMPlatform.systemContext = null;
  }
}
