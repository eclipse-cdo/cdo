/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.net4j.internal.ui.container;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.container.ElementWizard;
import org.eclipse.net4j.util.ui.container.ElementWizardFactory;

import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 * @author Martin Fluegge
 * @since 4.0
 */
public class JVMConnectorWizard extends ElementWizard
{
  public JVMConnectorWizard()
  {
  }

  @Override
  protected void create(Composite parent)
  {
    addText(parent, "Acceptor Name:");
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ElementWizardFactory
  {
    public Factory()
    {
      super(ConnectorFactory.PRODUCT_GROUP, "jvm");
    }

    @Override
    public JVMConnectorWizard create(String description) throws ProductCreationException
    {
      return new JVMConnectorWizard();
    }
  }
}
