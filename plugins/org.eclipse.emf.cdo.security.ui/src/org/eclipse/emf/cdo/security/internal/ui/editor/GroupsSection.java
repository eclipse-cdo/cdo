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
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * The section presenting the {@link Group}s defined in the security realm.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class GroupsSection extends TableSection<Group>
{
  public GroupsSection(EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(Group.class, SecurityPackage.Literals.GROUP, domain, adapterFactory);
  }

  @Override
  protected String getTitle()
  {
    return Messages.GroupsSection_0;
  }

  @Override
  protected EReference getDropReference(EObject target, EObject objectToDrop)
  {
    if (objectToDrop instanceof User)
    {
      return SecurityPackage.Literals.GROUP__USERS;
    }

    return SecurityPackage.Literals.ASSIGNEE__ROLES;
  }
}
