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

import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * The section form part presenting the {@link User}s defined in the security realm.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class UsersSection extends TableSection<User>
{
  public UsersSection(EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(User.class, SecurityPackage.Literals.USER, domain, adapterFactory);
  }

  @Override
  protected String getTitle()
  {
    return Messages.UsersSection_0;
  }

  @Override
  protected EReference getDropReference(EObject target, EObject objectToDrop)
  {
    if (objectToDrop instanceof Group)
    {
      return SecurityPackage.Literals.USER__GROUPS;
    }

    if (objectToDrop instanceof Role)
    {
      return SecurityPackage.Literals.ASSIGNEE__ROLES;
    }

    return null;
  }
}
