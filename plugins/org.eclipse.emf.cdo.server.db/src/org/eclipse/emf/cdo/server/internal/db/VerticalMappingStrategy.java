/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;

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

  public CDOClassRef readObjectType(IDBStoreReader storeReader, CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected IClassMapping createClassMapping(CDOClass cdoClass)
  {
    return new VerticalClassMapping(this, cdoClass);
  }

  @Override
  protected List<CDOClass> getClassesWithObjectInfo()
  {
    return Collections.singletonList(rootClassMapping.getCDOClass());
  }

  @Override
  protected String[] getResourceQueries(CDOID folderID, String name, boolean exactMatch)
  {
    // TODO: implement VerticalMappingStrategy.getResourceQueries(folderID, name, exactMatch)
    throw new UnsupportedOperationException();
  }
}
