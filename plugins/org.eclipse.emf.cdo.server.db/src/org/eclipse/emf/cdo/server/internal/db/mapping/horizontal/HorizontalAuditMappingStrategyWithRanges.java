/*
 * Copyright (c) 2010-2013, 2018, 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;

import org.eclipse.net4j.db.DBUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalAuditMappingStrategyWithRanges extends HorizontalAuditMappingStrategy
{
  public HorizontalAuditMappingStrategyWithRanges()
  {
  }

  @Override
  public boolean hasDeltaSupport()
  {
    return true;
  }

  @Override
  protected IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalAuditClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new AuditListTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  protected String modifyListJoin(String attrTable, String listTable, String join)
  {
    join += " AND " + listTable + "." + DBUtil.quoted(MappingNames.LIST_REVISION_VERSION_ADDED);
    join += "<=" + attrTable + "." + DBUtil.quoted(MappingNames.ATTRIBUTES_VERSION);
    join += " AND (" + listTable + "." + DBUtil.quoted(MappingNames.LIST_REVISION_VERSION_REMOVED);
    join += " IS NULL OR " + listTable + "." + DBUtil.quoted(MappingNames.LIST_REVISION_VERSION_REMOVED);
    join += ">" + attrTable + "." + DBUtil.quoted(MappingNames.ATTRIBUTES_VERSION) + ")";
    return join;
  }
}
