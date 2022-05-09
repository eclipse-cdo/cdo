/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ModuleElement;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.SystemElement;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.Input.ObjectModifier;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public class LMHistoryInputObjectModifier implements CommitHistoryComposite.Input.ObjectModifier
{
  public LMHistoryInputObjectModifier()
  {
  }

  @Override
  public Object modifyObject(EObject object)
  {
    OMPreference<Boolean> pref = OM.PREF_SHOW_MODULE_HISTORY;
    if (pref.getValue())
    {
      if (object instanceof ModuleElement)
      {
        Module module = ((ModuleElement)object).getModule();
        return getModuleSession(module);
      }

      if (object instanceof Module)
      {
        return getModuleSession((Module)object);
      }
    }

    if (object instanceof SystemElement)
    {
      System system = ((SystemElement)object).getSystem();
      return getSystemSession(system);
    }

    if (object instanceof org.eclipse.emf.cdo.lm.System)
    {
      return getSystemSession((org.eclipse.emf.cdo.lm.System)object);
    }

    return null;
  }

  public static CDOSession getSystemSession(System system)
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(system);
    if (systemDescriptor != null)
    {
      return systemDescriptor.getSystemRepository().getSession();
    }

    return null;
  }

  public static CDOSession getModuleSession(Module module)
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(module);
    if (systemDescriptor != null)
    {
      CDORepository moduleRepository = systemDescriptor.getModuleRepository(module.getName());
      if (moduleRepository != null)
      {
        return moduleRepository.getSession();
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends CommitHistoryComposite.Input.ObjectModifier.Factory
  {
    public static final String TYPE = "lm";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public ObjectModifier create(String description) throws ProductCreationException
    {
      return new LMHistoryInputObjectModifier();
    }
  }
}
