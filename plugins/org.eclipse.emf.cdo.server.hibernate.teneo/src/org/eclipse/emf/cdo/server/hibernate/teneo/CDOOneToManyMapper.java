/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEReference;
import org.eclipse.emf.teneo.hibernate.hbannotation.HbCascadeType;
import org.eclipse.emf.teneo.hibernate.mapper.OneToManyMapper;
import org.eclipse.emf.teneo.simpledom.Element;
import org.eclipse.emf.teneo.util.StoreUtil;

import java.util.List;

/**
 * Prevent delete-orphan.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 * @since 4.2
 */
public class CDOOneToManyMapper extends OneToManyMapper
{

  private static ThreadLocal<Boolean> isMapMappedAsList = new ThreadLocal<Boolean>();

  @Override
  protected void addCascades(Element associationElement, List<HbCascadeType> cascades, boolean addDeleteOrphan)
  {
    super.addCascades(associationElement, cascades, isMapMappedAsList.get() ? addDeleteOrphan : false);
  }

  @Override
  public void process(PAnnotatedEReference paReference)
  {
    // is a map and is not mapped as true map
    isMapMappedAsList.set(StoreUtil.isMap(paReference.getModelEReference()) && !getHbmContext().isMapEMapAsTrueMap());
    try
    {
      super.process(paReference);
    }
    finally
    {
      isMapMappedAsList.set(null);
    }
  }
}
