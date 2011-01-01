/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.container.examples;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.container.ElementWizard;
import org.eclipse.net4j.util.ui.container.ElementWizardComposite;
import org.eclipse.net4j.util.ui.container.ElementWizardFactory;
import org.eclipse.net4j.util.ui.container.IElementWizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class Snippet1
{
  public static void main(String[] args)
  {
    IPluginContainer container = IPluginContainer.INSTANCE;
    String PG = "test.connectors";

    container.registerFactory(new Factory(PG, "tcp")
    {
      public Object create(String description) throws ProductCreationException
      {
        return null;
      }
    });

    container.registerFactory(new Factory(PG, "jvm")
    {
      public Object create(String description) throws ProductCreationException
      {
        return null;
      }
    });

    container.registerFactory(new Factory(PG, "http")
    {
      public Object create(String description) throws ProductCreationException
      {
        return null;
      }
    });

    container.registerFactory(new ElementWizardFactory(PG, "http")
    {
      @Override
      public IElementWizard create(String description) throws ProductCreationException
      {
        return new ElementWizard()
        {
          @Override
          protected void create(Composite parent)
          {
            addText(parent, "Server:");
            addCombo(parent, "Protocol:", "http", "https");
            addText(parent, "Resource:");
          }
        };
      }
    });

    container.registerFactory(new ElementWizardFactory(PG, "tcp")
    {
      @Override
      public IElementWizard create(String description) throws ProductCreationException
      {
        return new ElementWizard()
        {
          @Override
          protected void create(Composite parent)
          {
            Label l1 = new Label(parent, SWT.NONE);
            l1.setText("Hostname:");
            l1.setLayoutData(UIUtil.createGridData(false, false));

            Text t1 = new Text(parent, SWT.BORDER);
            t1.setLayoutData(UIUtil.createGridData(true, false));
          }
        };
      }
    });

    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout(1, false));

    new ElementWizardComposite.WithCombo(shell, SWT.NONE, PG, "Type:");

    shell.pack();
    shell.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }

    display.dispose();
  }
}
