/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryProperties;

import org.eclipse.net4j.util.AdapterUtil;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 */
public final class RepositoryPropertyPage extends AbstractPropertyPage<CDORepository>
{
  public RepositoryPropertyPage()
  {
    super(CDORepositoryProperties.INSTANCE, CDORepositoryProperties.CATEGORY_REPOSITORY, "id", "type", "label", "folder");
  }

  @Override
  protected CDORepository convertElement(IAdaptable element)
  {
    if (element instanceof CDOCheckout)
    {
      return ((CDOCheckout)element).getRepository();
    }

    return AdapterUtil.adapt(element, CDORepository.class);
  }

  @Override
  protected Control createControl(Composite parent, String name, String label, String description, String value)
  {
    if ("folder".equals(name))
    {
      return createLink(parent, name, label, description, value);
    }

    return super.createControl(parent, name, label, description, value);
  }
}