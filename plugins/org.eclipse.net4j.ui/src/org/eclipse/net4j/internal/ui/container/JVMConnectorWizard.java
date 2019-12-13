/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.jvm.JVMAcceptorFactory;
import org.eclipse.net4j.internal.ui.bundle.OM;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.container.ElementWizard;
import org.eclipse.net4j.util.ui.container.ElementWizardFactory;

import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    List<String> choices = new ArrayList<String>();

    try
    {
      Object[] elements = getContainer().getElements(JVMAcceptorFactory.PRODUCT_GROUP, JVMAcceptorFactory.TYPE);
      for (Object object : elements)
      {
        if (object instanceof IJVMAcceptor)
        {
          IJVMAcceptor acceptor = (IJVMAcceptor)object;
          choices.add(acceptor.getName());
        }
      }
    }
    catch (NoClassDefFoundError error)
    {
      OM.LOG.error(error);
    }

    if (!choices.isEmpty())
    {
      Collections.sort(choices);

      final Combo acceptorCombo = addCombo(parent, "Acceptor:", choices);
      acceptorCombo.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          String acceptorName = acceptorCombo.getText();
          setResultDescription(acceptorName);
        }
      });

      String description = getDefaultDescription();
      if (description != null)
      {
        acceptorCombo.setText(description);
      }
    }
    else
    {
      final Text acceptorNameText = addText(parent, "Acceptor Name:");
      acceptorNameText.addModifyListener(new ModifyListener()
      {
        @Override
        public void modifyText(ModifyEvent e)
        {
          String acceptorName = acceptorNameText.getText();
          if (acceptorName.length() == 0)
          {
            setValidationError(acceptorNameText, "Acceptor name is empty.");
            return;
          }

          setResultDescription(acceptorName);
          setValidationError(acceptorNameText, null);
        }
      });

      String description = getDefaultDescription();
      if (description != null)
      {
        acceptorNameText.setText(description);
      }
    }
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
