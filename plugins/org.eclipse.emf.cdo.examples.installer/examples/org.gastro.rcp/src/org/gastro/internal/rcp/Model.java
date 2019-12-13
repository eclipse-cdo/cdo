/*
 * Copyright (c) 2009-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
package org.gastro.internal.rcp;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.gastro.business.BusinessDay;
import org.gastro.business.BusinessFactory;
import org.gastro.inventory.InventoryFactory;
import org.gastro.inventory.MenuCard;
import org.gastro.inventory.Restaurant;
import org.gastro.inventory.Station;
import org.gastro.rcp.IConfiguration;
import org.gastro.rcp.IModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Eike Stepper
 */
public class Model extends Lifecycle implements IModel
{
  public static final Model INSTANCE = new Model();

  private final ComposedAdapterFactory adapterFactory;

  private CDONet4jSession session;

  private CDOView view;

  private Restaurant restaurant;

  private BusinessDay businessDay;

  private Station station;

  private Model()
  {
    adapterFactory = new ComposedAdapterFactory(EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());
  }

  @Override
  public AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  @Override
  public synchronized Restaurant getRestaurant()
  {
    if (restaurant == null)
    {
      String name = IConfiguration.INSTANCE.getRestaurant();
      String path = name + "/inventory";
      if (!view.hasResource(path))
      {
        CDOTransaction transaction = session.openTransaction();
        Restaurant restaurant = InventoryFactory.eINSTANCE.createRestaurant();
        restaurant.setName(name);

        try
        {
          CDOResource resource = transaction.createResource(path);
          resource.getContents().add(restaurant);
          transaction.commit();
        }
        catch (CommitException ex)
        {
          throw WrappedException.wrap(ex);
        }
        finally
        {
          transaction.close();
        }
      }

      CDOResource resource = view.getResource(path);
      restaurant = (Restaurant)resource.getContents().get(0);
    }

    return restaurant;
  }

  @Override
  public synchronized BusinessDay getBusinessDay()
  {
    if (businessDay == null)
    {
      Restaurant restaurant = getRestaurant();
      Date date = IConfiguration.INSTANCE.getBusinessDay();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      String path = restaurant.getName() + "/" + formatter.format(date);
      if (!view.hasResource(path))
      {
        CDOTransaction transaction = session.openTransaction();
        Restaurant txRestaurant = transaction.getObject(restaurant);
        EList<MenuCard> menuCards = txRestaurant.getMenuCards();
        if (menuCards.isEmpty())
        {
          MenuCard menuCard = InventoryFactory.eINSTANCE.createMenuCard();
          menuCard.setTitle("Untitled");
          menuCards.add(menuCard);
        }

        BusinessDay businessDay = BusinessFactory.eINSTANCE.createBusinessDay();
        businessDay.setDate(date);
        businessDay.setMenuCard(menuCards.get(0));

        try
        {
          CDOResource resource = transaction.createResource(path);
          resource.getContents().add(businessDay);
          transaction.commit();
        }
        catch (CommitException ex)
        {
          throw WrappedException.wrap(ex);
        }
        finally
        {
          transaction.close();
        }
      }

      CDOResource resource = view.getResource(path);
      businessDay = (BusinessDay)resource.getContents().get(0);
    }

    return businessDay;
  }

  @Override
  public synchronized Station getStation()
  {
    if (station == null)
    {
      String id = IConfiguration.INSTANCE.getStation();
      for (Station station : getRestaurant().getStations())
      {
        if (station.getStationID().equalsIgnoreCase(id))
        {
          this.station = station;
          break;
        }
      }
    }

    return station;
  }

  @Override
  public <T extends CDOObject> Object modify(T object, ITransactionalOperation<T> operation)
  {
    CDOTransaction transaction = session.openTransaction();

    try
    {
      T transactionalObject = transaction.getObject(object);
      Object result = operation.execute(transactionalObject);
      transaction.commit();

      if (result instanceof CDOObject)
      {
        return view.getObject((CDOObject)result);
      }

      return result;
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      transaction.close();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    String server = IConfiguration.INSTANCE.getServer();
    String repository = IConfiguration.INSTANCE.getRepository();

    IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp", server);

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(repository);

    session = config.openNet4jSession();
    view = session.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    session.close();
    session = null;
    view = null;
    restaurant = null;
    station = null;
    adapterFactory.dispose();
    super.doDeactivate();
  }
}
