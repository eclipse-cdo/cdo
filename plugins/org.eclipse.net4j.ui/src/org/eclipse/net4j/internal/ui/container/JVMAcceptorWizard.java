/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.container.ElementWizard;
import org.eclipse.net4j.util.ui.container.ElementWizardFactory;

import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class JVMAcceptorWizard extends ElementWizard implements ModifyListener
{
  private Text acceptorNameText;

  public JVMAcceptorWizard()
  {
  }

  @Override
  protected void create(Composite parent)
  {
    acceptorNameText = addText(parent, "Acceptor Name:");
    acceptorNameText.addModifyListener(this);

    String description = getDefaultDescription();
    if (description != null)
    {
      acceptorNameText.setText(description);
    }
  }

  @Override
  public void modifyText(ModifyEvent e)
  {
    String acceptorName = acceptorNameText.getText();
    if (acceptorName.length() == 0)
    {
      setValidationError(acceptorNameText, "Acceptor name is empty.");
      return;
    }

    try
    {
      for (Object element : getContainer().getElements(AcceptorFactory.PRODUCT_GROUP, "jvm"))
      {
        if (element instanceof org.eclipse.net4j.jvm.IJVMAcceptor)
        {
          org.eclipse.net4j.jvm.IJVMAcceptor acceptor = (org.eclipse.net4j.jvm.IJVMAcceptor)element;
          if (ObjectUtil.equals(acceptor.getName(), acceptorName))
          {
            setValidationError(acceptorNameText, "Duplicate acceptor name.");
            return;
          }
        }
      }
    }
    catch (NoClassDefFoundError error)
    {
      // Ignore.
    }

    setResultDescription(acceptorName);
    setValidationError(acceptorNameText, null);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ElementWizardFactory
  {
    public Factory()
    {
      super(AcceptorFactory.PRODUCT_GROUP, "jvm");
    }

    @Override
    public JVMAcceptorWizard create(String description) throws ProductCreationException
    {
      return new JVMAcceptorWizard();
    }
  }
}
