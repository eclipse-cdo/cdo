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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;

import org.eclipse.net4j.util.io.ExtendedDataInput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDORevisionUtil
{
  public static final Object UNINITIALIZED = new Uninitialized();

  private CDORevisionUtil()
  {
  }

  public static CDORevision create(CDORevisionResolver revisionResolver, CDOClass cdoClass, CDOID id)
  {
    return new CDORevisionImpl(revisionResolver, cdoClass, id);
  }

  public static CDORevision copy(CDORevision source)
  {
    return new CDORevisionImpl((CDORevisionImpl)source);
  }

  public static CDORevision read(ExtendedDataInput in, CDORevisionResolver revisionResolver,
      CDOPackageManager packageManager) throws IOException
  {
    return new CDORevisionImpl(in, revisionResolver, packageManager);
  }

  public static Object remapID(Object value, Map<CDOIDTemp, CDOID> idMappings)
  {
    return CDORevisionImpl.remapID(value, idMappings);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Uninitialized
  {
    public Uninitialized()
    {
    }

    @Override
    public String toString()
    {
      return "UNINITIALIZED";
    }
  }
}
