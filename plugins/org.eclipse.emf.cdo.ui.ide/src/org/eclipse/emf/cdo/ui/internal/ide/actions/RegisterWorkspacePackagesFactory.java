/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.internal.ui.dialogs.PackageRegistryDialog;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.widgets.CustomizeableComposite.CompositeCustomizer;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class RegisterWorkspacePackagesFactory extends Factory
{
  private static final String TYPE = "RegisterWorkspacePackages";

  public RegisterWorkspacePackagesFactory()
  {
    super(PackageRegistryDialog.PRODUCT_GROUP, TYPE);
  }

  public CompositeCustomizer create(String description) throws ProductCreationException
  {
    return new CompositeCustomizer()
    {
      public void customize(Composite composite, Object data)
      {
        final PackageRegistryDialog dialog = (PackageRegistryDialog)data;

        Button button = dialog.createButton(composite, -1, Messages.getString("PackageRegistryDialog.6"), false); //$NON-NLS-1$
        button.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            new RegisterWorkspacePackagesAction(dialog.getPage(), dialog.getSession())
            {
              @Override
              protected void postRegistration(List<EPackage> ePackages)
              {
                dialog.refreshViewer();
              }
            }.run();
          }
        });
      }
    };
  }
}
