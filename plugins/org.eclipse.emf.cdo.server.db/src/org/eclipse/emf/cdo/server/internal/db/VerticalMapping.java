/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerticalMapping extends ValueMapping
{
  private List<IMapping> superMappings;

  public VerticalMapping(VerticalMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    super(mappingStrategy, cdoClass, cdoClass.getFeatures());
    for (CDOClass superType : cdoClass.getSuperTypes())
    {
      IMapping superMapping = mappingStrategy.getMapping(superType);
      if (superMapping != null)
      {
        if (superMappings == null)
        {
          superMappings = new ArrayList<IMapping>(0);
        }

        superMappings.add(superMapping);
      }
    }
  }

  @Override
  public VerticalMappingStrategy getMappingStrategy()
  {
    return (VerticalMappingStrategy)super.getMappingStrategy();
  }

  @Override
  protected boolean hasFullRevisionInfo()
  {
    return false;
  }

  public List<IMapping> getSuperMappings()
  {
    return superMappings;
  }

  @Override
  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    super.writeRevision(storeAccessor, revision);
    if (superMappings != null)
    {
      for (IMapping superMapping : superMappings)
      {
        superMapping.writeRevision(storeAccessor, revision);
      }
    }
  }
}
