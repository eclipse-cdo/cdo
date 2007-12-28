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

import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.db.IClassMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends MappingStrategy
{
  public HorizontalMappingStrategy()
  {
  }

  public String getType()
  {
    return "horizontal";
  }

  public boolean hasEfficientTypeLookup()
  {
    return false;
  }

  @Override
  protected IClassMapping createClassMapping(CDOClass cdoClass)
  {
    if (cdoClass.isAbstract())
    {
      return null;
    }

    return new HorizontalClassMapping(this, cdoClass);
  }

  @Override
  protected List<CDOClass> getClassesWithObjectInfo()
  {
    List<CDOClass> result = new ArrayList<CDOClass>();
    IPackageManager packageManager = getStore().getRepository().getPackageManager();
    for (CDOPackage cdoPackage : packageManager.getPackages())
    {
      for (CDOClass cdoClass : cdoPackage.getConcreteClasses())
      {
        if (!cdoClass.isRoot())
        {
          result.add(cdoClass);
        }
      }
    }

    return result;
  }
}
