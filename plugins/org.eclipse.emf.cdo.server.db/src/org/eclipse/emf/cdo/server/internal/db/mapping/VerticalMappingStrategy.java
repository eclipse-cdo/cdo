/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;

import org.eclipse.emf.ecore.EClass;

import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerticalMappingStrategy extends MappingStrategy
{
  private RootClassMapping rootClassMapping;

  public VerticalMappingStrategy()
  {
    throw new UnsupportedOperationException();
  }

  public String getType()
  {
    return "vertical";
  }

  public RootClassMapping getRootClassMapping()
  {
    if (rootClassMapping == null)
    {
      rootClassMapping = new RootClassMapping(this);
    }

    return rootClassMapping;
  }

  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected IClassMapping createClassMapping(EClass eClass)
  {
    return new VerticalClassMapping(this, eClass);
  }

  @Override
  protected List<EClass> getClassesWithObjectInfo()
  {
    return Collections.singletonList(rootClassMapping.getEClass());
  }

  @Override
  protected String[] getResourceQueries(CDOID folderID, String name, boolean exactMatch)
  {
    throw new UnsupportedOperationException();
  }
}
