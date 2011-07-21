/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalBranchingMappingStrategyWithRanges extends AbstractHorizontalMappingStrategy
{
  private boolean copyOnBranch;

  public HorizontalBranchingMappingStrategyWithRanges()
  {
  }

  public boolean hasAuditSupport()
  {
    return true;
  }

  public boolean hasBranchingSupport()
  {
    return true;
  }

  public boolean hasDeltaSupport()
  {
    return true;
  }

  public boolean shallCopyOnBranch()
  {
    return copyOnBranch;
  }

  @Override
  public IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalBranchingClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingListTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  public IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingFeatureMapTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  public String getListJoin(String attrTable, String listTable)
  {
    String join = super.getListJoin(attrTable, listTable);
    join += " AND " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_ADDED;
    join += "<=" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
    join += " AND (" + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
    join += " IS NULL OR " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
    join += ">" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
    join += ") AND " + attrTable + "." + CDODBSchema.ATTRIBUTES_BRANCH;
    join += "=" + listTable + "." + CDODBSchema.LIST_REVISION_BRANCH;
    return join;
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    String value = getProperties().get(CDODBUtil.PROP_COPY_ON_BRANCH);
    copyOnBranch = value == null ? false : Boolean.valueOf(value);
  }
}
