/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.lissome;

import org.eclipse.emf.cdo.server.IStoreAccessor;

/**
 * A {@link IStoreAccessor store accessor} for CDO's proprietary Lissome store.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILissomeStoreAccessor extends IStoreAccessor, IStoreAccessor.DurableLocking2
{
  @Override
  public ILissomeStore getStore();
}
