/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 * @author Martin Fluegge
 * @since 4.0
 */
public class JVMConnectorWizard extends ElementWizard implements ModifyListener
{
  private Text acceptorNameText;

  public JVMConnectorWizard()
  {
  }

  @Override
  protected void create(Composite parent)
  {
    acceptorNameText = addText(parent, "Acceptor Name:");
    acceptorNameText.addModifyListener(this);
  }

  public void modifyText(ModifyEvent e)
  {
    String acceptorName = acceptorNameText.getText();
    if (acceptorName.length() == 0)
    {
      setValidationError(acceptorNameText, "Acceptor name is empty.");
      return;
    }

    setResultDescription(acceptorName);
    setValidationOK();
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
