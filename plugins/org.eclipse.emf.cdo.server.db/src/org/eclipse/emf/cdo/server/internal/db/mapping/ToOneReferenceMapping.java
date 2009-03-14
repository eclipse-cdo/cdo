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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class ToOneReferenceMapping extends AttributeMapping.AMObject
{
  public ToOneReferenceMapping(ClassMapping classMapping, EStructuralFeature feature)
  {
    super(classMapping, feature);
  }

  @Override
  public Long getRevisionValue(InternalCDORevision revision)
  {
    CDOID id = (CDOID)super.getRevisionValue(revision);
    return CDOIDUtil.getLong(id);
  }
}
