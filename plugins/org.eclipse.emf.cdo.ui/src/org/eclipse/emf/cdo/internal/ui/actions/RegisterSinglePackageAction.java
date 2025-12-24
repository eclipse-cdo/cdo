/*
 * Copyright (c) 2007-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
    super(page, packageURI, Messages.getString("RegisterSinglePackageAction.0") + packageURI, getDescriptor(packageURI), //$NON-NLS-1$
        session);
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
