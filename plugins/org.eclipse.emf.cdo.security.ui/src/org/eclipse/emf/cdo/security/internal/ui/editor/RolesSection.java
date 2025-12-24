/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.cdo.security.Assignee;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * The form section part presenting the {@link Role}s defined in the security realm.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class RolesSection extends TableSection<Role>
{
  public RolesSection(EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(Role.class, SecurityPackage.Literals.ROLE, domain, adapterFactory);
  }

  @Override
  protected String getTitle()
  {
    return Messages.RolesSection_0;
  }

  @Override
  protected EReference getDropReference(EObject target, EObject objectToDrop)
  {
    if (objectToDrop instanceof Assignee)
    {
      return SecurityPackage.Literals.ROLE__ASSIGNEES;
    }

    return null;
  }
}
