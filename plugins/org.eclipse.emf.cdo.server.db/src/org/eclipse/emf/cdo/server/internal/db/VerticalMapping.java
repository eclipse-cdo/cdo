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
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerticalMapping extends IDInfoMapping
{
  private List<IMapping> superMappings;

  public VerticalMapping(IMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    super(mappingStrategy, cdoClass);
    for (CDOClass superType : cdoClass.getSuperTypes())
    {
      IMapping superMapping = mappingStrategy.getMapping(superType);
      if (superMapping != null)
      {
        if (superMappings == null)
        {
          superMappings = new ArrayList(0);
        }

        superMappings.add(superMapping);
      }
    }
  }

  public List<IMapping> getSuperMappings()
  {
    return superMappings;
  }

  @Override
  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    super.writeRevision(storeAccessor, revision);
    writeAttributes(storeAccessor, revision);
    writeReferences(storeAccessor, revision);
    if (superMappings != null)
    {
      for (IMapping superMapping : superMappings)
      {
        superMapping.writeRevision(storeAccessor, revision);
      }
    }
  }

  protected void writeAttributes(IStoreWriter writer, CDORevisionImpl revision)
  {
  }

  protected void writeReferences(IStoreWriter writer, CDORevisionImpl revision)
  {
  }
}
