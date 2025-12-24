/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 259402
 *    Stefan Winkler - redesign (prepared statements)
 *    Stefan Winkler - bug 276926
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.IObjectTypeMapper;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class AbstractObjectTypeMapper extends Lifecycle implements IObjectTypeMapper
{
  private IMappingStrategy mappingStrategy;

  private IMetaDataManager metaDataManager;

  public AbstractObjectTypeMapper()
  {
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
  }

  public IMetaDataManager getMetaDataManager()
  {
    return metaDataManager;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(mappingStrategy, "mappingStrategy"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    metaDataManager = getMappingStrategy().getStore().getMetaDataManager();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    metaDataManager = null;
  }
}
