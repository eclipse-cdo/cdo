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
package org.eclipse.emf.cdo.releng.setup.product;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @author Eike Stepper
 */
public class Application implements IApplication
{
  public Object start(IApplicationContext context) throws Exception
  {
    SetupDialog dialog = new SetupDialog(null);
    dialog.open();
    return 0;
  }

  public void stop()
  {
  }
}
