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

import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDUtil;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

/**
 * @author Eike Stepper
 */
public class ToOneReferenceMapping extends AttributeMapping.AMObject
{
  public ToOneReferenceMapping(ClassMapping classMapping, CDOFeature feature)
  {
    super(classMapping, feature);
  }

  @Override
  protected Long getRevisionValue(InternalCDORevision revision)
  {
    CDOID id = (CDOID)super.getRevisionValue(revision);
    return CDOIDUtil.getLong(id);
  }
}
