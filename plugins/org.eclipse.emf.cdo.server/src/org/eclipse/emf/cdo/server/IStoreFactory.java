/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Creates {@link IStore stores}.
 *
 * @author Eike Stepper
 */
public interface IStoreFactory
{
  public String getStoreType();

  /**
   * @since 4.0
   */
  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig);

  /**
   * An extension interface for {@link IStoreFactory store factories} that want to receive parameters.
   *
   * @author Eike Stepper
   * @since 4.10
   * @deprecated As of 4.20 use {@link org.eclipse.net4j.util.ParameterAware}.
   */
  @Deprecated
  public interface ParameterAware extends org.eclipse.net4j.util.ParameterAware
  {
    @Override
    public void setParameters(Map<String, String> parameters);
  }
}
