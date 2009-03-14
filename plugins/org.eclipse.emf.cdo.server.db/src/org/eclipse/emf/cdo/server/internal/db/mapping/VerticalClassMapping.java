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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerticalClassMapping extends ClassMapping
{
  private List<IClassMapping> superMappings;

  public VerticalClassMapping(VerticalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass, null);
    for (EClass superType : eClass.getESuperTypes())
    {
      IClassMapping superMapping = mappingStrategy.getClassMapping(superType);
      if (superMapping != null)
      {
        if (superMappings == null)
        {
          superMappings = new ArrayList<IClassMapping>(0);
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

  public boolean hasFullRevisionInfo()
  {
    return false;
  }

  public List<IClassMapping> getSuperMappings()
  {
    return superMappings;
  }

  @Override
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, OMMonitor monitor)
  {
    if (superMappings != null)
    {
      try
      {
        monitor.begin(1 + superMappings.size());
        super.writeRevision(accessor, revision, monitor.fork());
        for (IClassMapping superMapping : superMappings)
        {
          superMapping.writeRevision(accessor, revision, monitor.fork());
        }
      }
      finally
      {
        monitor.done();
      }
    }
    else
    {
      super.writeRevision(accessor, revision, monitor);
    }
  }

  @Override
  protected void checkDuplicateResources(IDBStoreAccessor accessor, CDORevision revision) throws IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
}
