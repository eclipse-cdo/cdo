/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.net4j.internal.ui.container;

import org.eclipse.net4j.internal.ui.bundle.OM;
import org.eclipse.net4j.tcp.TCPUtil.ConnectorData;
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
public class TCPConnectorWizard extends ElementWizard implements ModifyListener
{
  private Text hostText;

  private Text portText;

  public TCPConnectorWizard()
  {
  }

  @Override
  protected void create(Composite parent)
  {
    hostText = addText(parent, "Host:");
    hostText.addModifyListener(this);

    portText = addText(parent, "Port:");
    portText.addModifyListener(this);

    String description = getDefaultDescription();
    if (description != null)
    {
      try
      {
        ConnectorData data = new ConnectorData(description);
        hostText.setText(data.getHost());
        portText.setText(Integer.toString(data.getPort()));
      }
      catch (NoClassDefFoundError error)
      {
        OM.LOG.error(error);
      }
    }
  }

  @Override
  public void modifyText(ModifyEvent e)
  {
    String host = hostText.getText();
    if (host.length() == 0)
    {
      setValidationError(hostText, "Host name is empty.");
      return;
    }

    String port = portText.getText();
    if (port.length() != 0)
    {
      host += ":" + port;
    }

    setResultDescription(host);
    setValidationError(hostText, null);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ElementWizardFactory
  {
    public Factory()
    {
      super(ConnectorFactory.PRODUCT_GROUP, "tcp");
    }

    @Override
    public TCPConnectorWizard create(String description) throws ProductCreationException
    {
      return new TCPConnectorWizard();
    }
  }
}
