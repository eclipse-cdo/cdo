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

import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IClassMapping;

import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerticalMappingStrategy extends MappingStrategy
{
  private RootClassMapping rootMapping;

  public VerticalMappingStrategy()
  {
    throw new UnsupportedOperationException();
  }

  public String getType()
  {
    return "vertical";
  }

  public boolean hasEfficientTypeLookup()
  {
    return true;
  }

  public RootClassMapping getRootMapping()
  {
    if (rootMapping == null)
    {
      rootMapping = new RootClassMapping(this);
    }

    return rootMapping;
  }

  @Override
  protected IClassMapping createClassMapping(CDOClass cdoClass)
  {
    return new VerticalClassMapping(this, cdoClass);
  }

  @Override
  protected List<CDOClass> getClassesWithObjectInfo()
  {
    return Collections.singletonList(rootMapping.getCDOClass());
  }
}
