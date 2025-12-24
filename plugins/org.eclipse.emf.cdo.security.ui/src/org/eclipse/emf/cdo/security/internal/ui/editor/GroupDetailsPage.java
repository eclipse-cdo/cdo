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
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * The details page for a {@link Group} selection.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class GroupDetailsPage extends AbstractDetailsPage<Group>
{
  public GroupDetailsPage(EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(Group.class, SecurityPackage.Literals.GROUP, domain, adapterFactory);
  }

  @Override
  protected void createContents(Composite parent, FormToolkit toolkit)
  {
    super.createContents(parent, toolkit);

    text(parent, toolkit, Messages.GroupDetailsPage_0, SecurityPackage.Literals.ASSIGNEE__ID);

    space(parent, toolkit);

    oneToMany(parent, toolkit, Messages.GroupDetailsPage_1, SecurityPackage.Literals.GROUP__USERS);

    oneToMany(parent, toolkit, Messages.GroupDetailsPage_2, SecurityPackage.Literals.ASSIGNEE__ROLES);
  }
}
