/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * A revision providing {@link HashMap}.
 *
 * @author Eike Stepper
 * @since 4.18
 */
public class MapRevisionProvider extends HashMap<CDOID, CDORevision> implements CDORevisionProvider
{
  private static final long serialVersionUID = 1L;

  public MapRevisionProvider()
  {
  }

  public MapRevisionProvider(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }

  public MapRevisionProvider(int initialCapacity)
  {
    super(initialCapacity);
  }

  public MapRevisionProvider(Map<? extends CDOID, ? extends CDORevision> m)
  {
    super(m);
  }

  @Override
  public CDORevision getRevision(CDOID id)
  {
    return get(id);
  }
}
