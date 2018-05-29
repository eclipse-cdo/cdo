/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;

import org.hibernate.mapping.Component;
import org.hibernate.tuple.Instantiator;

import java.io.Serializable;

/**
 * Instantiates {@link CDOFeatureMapEntry}.
 *
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 */

public class FeatureMapEntryInstantiator implements Instantiator
{
  private static final long serialVersionUID = -1219767393020090471L;

  public FeatureMapEntryInstantiator(Component component)
  {
  }

  public Object instantiate()
  {
    final CDOFeatureMapEntry fme = CDORevisionUtil.createCDOFeatureMapEntry();
    return fme;
  }

  public Object instantiate(Serializable id)
  {
    return instantiate();
  }

  public boolean isInstance(Object object)
  {
    return CDOFeatureMapEntry.class.isInstance(object);
  }
}
