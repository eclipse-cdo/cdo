/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.objectivity;

import org.eclipse.emf.cdo.server.IStoreChunkReader;

/**
 * A {@link IStoreChunkReader chunk reader} for for CDO's Objecivity back-end integration.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjectivityStoreChunkReader extends IStoreChunkReader
{
  /**
   * @since 2.0
   */
  public IObjectivityStoreAccessor getAccessor();
}
