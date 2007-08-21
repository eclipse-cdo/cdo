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
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends StandardMappingStrategy
{
  public HorizontalMappingStrategy()
  {
  }

  public String getType()
  {
    return "horizontal";
  }

  @Override
  protected IDBField mapFeature(CDOClass cdoClass, CDOFeature cdoFeature, Set<IDBTable> affectedTables)
  {
    if (cdoClass.isAbstract())
    {
      return null;
    }

    if (cdoFeature.isReference())
    {
      if (cdoFeature.isMany())
      {
        return mapReference(cdoClass, cdoFeature, getToMany());
      }
      else
      {
        switch (getToOne())
        {
        case LIKE_ATTRIBUTES:
          return mapAttribute(cdoClass, cdoFeature);
        case LIKE_TO_MANY_REFERENCES:
          return mapReference(cdoClass, cdoFeature, getToMany());
        default:
          throw new IllegalArgumentException("Invalid mapping: " + getToOne());
        }
      }
    }
    else
    {
      if (cdoFeature.isMany())
      {
        throw new UnsupportedOperationException();
      }

      return mapAttribute(cdoClass, cdoFeature);
    }
  }
}
