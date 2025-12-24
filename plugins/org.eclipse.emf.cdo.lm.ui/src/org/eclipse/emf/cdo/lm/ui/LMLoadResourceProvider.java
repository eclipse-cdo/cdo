/*
 * Copyright (c) 2022-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui;

import org.eclipse.emf.cdo.lm.internal.client.LMResourceSetConfigurer;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;
import org.eclipse.emf.cdo.lm.ui.dialogs.SelectModuleResourcesDialog;
import org.eclipse.emf.cdo.ui.CDOLoadResourceProvider;
import org.eclipse.emf.cdo.ui.CDOLoadResourceProvider.ImageProvider;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LMLoadResourceProvider implements CDOLoadResourceProvider, ImageProvider
{
  public LMLoadResourceProvider()
  {
  }

  @Override
  public String getButtonText(ResourceSet resourceSet)
  {
    return "&Modules...";
  }

  @Override
  public Image getButtonImage(ResourceSet resourceSet)
  {
    return ExtendedImageRegistry.INSTANCE.getImage(LMEditPlugin.getPlugin().getImage("full/obj16/Module"));
  }

  @Override
  public boolean canHandle(ResourceSet resourceSet)
  {
    LMResourceSetConfigurer.Result result = LMResourceSetConfigurer.Result.of(resourceSet);
    return result instanceof LMResourceSetConfigurer.CheckoutResult;
  }

  @Override
  public List<URI> browseResources(ResourceSet resourceSet, Shell shell, boolean multi)
  {
    LMResourceSetConfigurer.Result result = LMResourceSetConfigurer.Result.of(resourceSet);
    if (result instanceof LMResourceSetConfigurer.CheckoutResult)
    {
      LMResourceSetConfigurer.CheckoutResult checkoutResult = (LMResourceSetConfigurer.CheckoutResult)result;

      SelectModuleResourcesDialog dialog = new SelectModuleResourcesDialog(shell, multi, checkoutResult);
      if (dialog.open() == SelectModuleResourcesDialog.OK)
      {
        return new ArrayList<>(dialog.getURIs());
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends CDOLoadResourceProvider.Factory
  {
    public static final String TYPE = "lm";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public CDOLoadResourceProvider create(String description) throws ProductCreationException
    {
      return new LMLoadResourceProvider();
    }
  }
}
