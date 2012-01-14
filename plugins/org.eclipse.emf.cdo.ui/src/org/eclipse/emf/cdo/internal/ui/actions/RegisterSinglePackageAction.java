/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RegisterSinglePackageAction extends RegisterPackagesAction
{
  private String packageURI;

  private EPackage.Registry registry = EPackage.Registry.INSTANCE;

  public RegisterSinglePackageAction(IWorkbenchPage page, CDOSession session, String packageURI)
  {
    super(page, packageURI,
        Messages.getString("RegisterSinglePackageAction.0") + packageURI, getDescriptor(packageURI), session); //$NON-NLS-1$
    this.packageURI = packageURI;
  }

  @Override
  protected List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session)
  {
    EPackage ePackage = registry.getEPackage(packageURI);
    if (ePackage != null)
    {
      return Collections.singletonList(ePackage);
    }

    return Collections.emptyList();
  }

  private static ImageDescriptor getDescriptor(String nsURI)
  {
    Type type = CDOPackageTypeRegistry.INSTANCE.lookup(nsURI);
    switch (type)
    {
    case LEGACY:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_LEGACY);

    case NATIVE:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_NATIVE);

    case DYNAMIC:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_DYNAMIC);

    case UNKNOWN:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_UNKNOWN);

    default:
      return null;
    }
  }
}
