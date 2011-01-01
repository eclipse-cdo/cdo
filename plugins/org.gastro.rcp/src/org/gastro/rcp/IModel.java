/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
