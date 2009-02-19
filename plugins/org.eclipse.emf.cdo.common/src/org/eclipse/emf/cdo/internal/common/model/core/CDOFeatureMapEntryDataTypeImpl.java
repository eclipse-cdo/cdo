/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common.model.core;

import org.eclipse.emf.cdo.common.model.core.CDOFeatureMapEntryDataType;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjustable;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;

/**
 * @author Simon McDuff
 */
public class CDOFeatureMapEntryDataTypeImpl implements CDOFeatureMapEntryDataType, CDOReferenceAdjustable
{
  private String uri;

  private Object object;

  public CDOFeatureMapEntryDataTypeImpl(String uri, Object object)
  {
    this.uri = uri;
    this.object = object;
  }

  public String getURI()
  {
    return uri;
  }

  public Object getObject()
  {
    return object;
  }

  public void adjustReferences(CDOReferenceAdjuster revisionAdjuster)
  {
    object = revisionAdjuster.adjustReference(object);
  }
}
