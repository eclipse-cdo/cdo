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

import org.eclipse.emf.teneo.hibernate.hbannotation.HbCascadeType;
import org.eclipse.emf.teneo.hibernate.mapper.OneToManyMapper;
import org.eclipse.emf.teneo.simpledom.Element;

import java.util.List;

/**
 * Prevent delete-orphan.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 * @since 4.1
 */
public class CDOOneToManyMapper extends OneToManyMapper
{
  @Override
  protected void addCascades(Element associationElement, List<HbCascadeType> cascades, boolean addDeleteOrphan)
  {
    super.addCascades(associationElement, cascades, false);
  }
}
