/*
 * Copyright (c) 2014, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.net4j.util.event.IEvent;

import java.util.List;

/**
 * An {@link IEvent event} fired from a {@link CDORevisionManager revision manager} when a new {@link CDORevision revision} has
 * been loaded.
 *
 * @author Esteban Dugueperoux
 * @since 4.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionsLoadedEvent extends IEvent
{
  @Override
  public CDORevisionManager getSource();

  public List<? extends CDORevision> getPrimaryLoadedRevisions();

  public List<? extends CDORevision> getAdditionalLoadedRevisions();

  public int getPrefetchDepth();
}
