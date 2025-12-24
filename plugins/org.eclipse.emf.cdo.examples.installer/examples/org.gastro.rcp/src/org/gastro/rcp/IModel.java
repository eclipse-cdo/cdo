/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.rcp;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.notify.AdapterFactory;

import org.gastro.business.BusinessDay;
import org.gastro.inventory.Restaurant;
import org.gastro.inventory.Station;

/**
 * @author Eike Stepper
 */
public interface IModel
{
  public static final IModel INSTANCE = org.gastro.internal.rcp.Model.INSTANCE;

  public AdapterFactory getAdapterFactory();

  public Restaurant getRestaurant();

  public BusinessDay getBusinessDay();

  public Station getStation();

  public <T extends CDOObject> Object modify(T object, ITransactionalOperation<T> operation);

  /**
   * @author Eike Stepper
   */
  public interface ITransactionalOperation<T extends CDOObject>
  {
    public Object execute(T object);
  }
}
