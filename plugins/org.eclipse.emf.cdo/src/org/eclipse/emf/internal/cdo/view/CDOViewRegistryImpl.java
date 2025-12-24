/*
 * Copyright (c) 2012, 2015, 2016, 2019, 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewRegistry;

import org.eclipse.emf.internal.cdo.util.AbstractRegistry;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOViewRegistryImpl extends AbstractRegistry<CDOView, CDOViewRegistry.Registration> implements CDOViewRegistry
{
  public static final CDOViewRegistryImpl INSTANCE = new CDOViewRegistryImpl();

  public CDOViewRegistryImpl()
  {
  }

  @Override
  public CDOView[] getViews()
  {
    return getRegisteredElements();
  }

  @Override
  public CDOView getView(int id)
  {
    return getElement(id);
  }

  @Override
  protected CDOView[] newArray(int size)
  {
    return new CDOView[size];
  }

  @Override
  protected Registration[] newRegistrationArray(int size)
  {
    return new Registration[size];
  }

  @Override
  protected Registration newRegistration(int id, CDOView view)
  {
    return new RegistrationImpl(id, view);
  }

  @Override
  protected int getRegisteredID(Registration registration)
  {
    return registration.getID();
  }

  @Override
  protected CDOView getRegisteredElement(Registration registration)
  {
    return registration.getView();
  }

  void register(CDOView view)
  {
    registerElement(view);
  }

  void deregister(CDOView view)
  {
    deregisterElement(view);
  }

  /**
   * @author Eike Stepper
   */
  private static final class RegistrationImpl implements Registration
  {
    private final int id;

    private final CDOView view;

    public RegistrationImpl(int id, CDOView view)
    {
      this.id = id;
      this.view = view;
    }

    @Override
    public int getID()
    {
      return id;
    }

    @Override
    public CDOView getView()
    {
      return view;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("ViewRegistration[id=");
      builder.append(id);
      builder.append(", session=");
      builder.append(view.getSession());
      builder.append(", view=");
      builder.append(view);
      builder.append("]");
      return builder.toString();
    }
  }
}
