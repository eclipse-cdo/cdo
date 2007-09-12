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
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

/**
 * @author Eike Stepper
 */
public class ToOneReferenceMapping extends AttributeMapping.AMObject
{
  public ToOneReferenceMapping(ValueMapping valueMapping, CDOFeature feature)
  {
    super(valueMapping, feature);
  }

  @Override
  protected Long getRevisionValue(CDORevisionImpl revision)
  {
    CDOID id = (CDOID)super.getRevisionValue(revision);
    return id.getValue();
  }
}
