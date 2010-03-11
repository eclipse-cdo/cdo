/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.MapHolder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapTest extends AbstractCDOTest
{
  // TODO clarify the problems with the default
  public void testStringStringMap_old() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");

      MapHolder mapHolder = getModel2Factory().createMapHolder();

      mapHolder.getStringToStringMap().put("Martin", "Fluegge");

      resource.getContents().add(mapHolder);

      transaction.commit();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/test1");

      MapHolder mapHolder = (MapHolder)resource.getContents().get(0);

      assertEquals(true, mapHolder.getStringToStringMap().keySet().contains("Martin"));
      assertEquals(true, mapHolder.getStringToStringMap().values().contains("Fluegge"));

      transaction.close();
    }
  }

  public void testIntegerStringMap() throws Exception
  {
    Map<Integer, String> objects = new HashMap<Integer, String>();
    objects.put(1, "1");
    objects.put(2, "2");
    objects.put(3, "3");
    objects.put(4, "4");

    testMap(getModel2Package().getMapHolder_IntegerToStringMap(), objects);
  }

  public void testStringStringMap() throws Exception
  {
    Map<String, String> objects = new HashMap<String, String>();
    objects.put("Eike", "Stepper");
    objects.put("Simon", "McDuff");
    objects.put("Stefan", "Winkler");
    objects.put("Victor", "Roldan Betancort");
    objects.put("André", "Dietisheim");
    objects.put("Ibrahim", "Sallam");
    objects.put("Martin", "Flügge");
    objects.put("Caspar", "De Groot");
    objects.put("Martin", "Taal");

    testMap(getModel2Package().getMapHolder_StringToStringMap(), objects);
  }

  public void testStringVATMap() throws Exception
  {
    Map<String, VAT> objects = new HashMap<String, VAT>();
    objects.put("VAT0", VAT.VAT0);
    objects.put("VAT7", VAT.VAT7);
    objects.put("VAT15", VAT.VAT15);

    testMap(getModel2Package().getMapHolder_StringToVATMap(), objects);
  }

  public void testStringToAddressContainmentMap() throws Exception
  {
    Map<String, Address> objects = new HashMap<String, Address>();

    objects.put("address1", getModel1Factory().createAddress());
    objects.put("address2", getModel1Factory().createAddress());
    objects.put("address3", getModel1Factory().createAddress());

    testMap(getModel2Package().getMapHolder_StringToAddressContainmentMap(), objects);
  }

  public void testStringToAddressReferenceMap() throws Exception
  {
    Map<String, Address> objects = new HashMap<String, Address>();

    objects.put("address1", getModel1Factory().createAddress());
    objects.put("address2", getModel1Factory().createAddress());
    objects.put("address3", getModel1Factory().createAddress());

    testMap(getModel2Package().getMapHolder_StringToAddressReferenceMap(), objects);
  }

  public void testEObjectToEObjectMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<Order, OrderDetail>();

    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Package().getMapHolder_EObjectToEObjectMap(), objects);
  }

  public void testEObjectToEObjectKeyContainedMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<Order, OrderDetail>();

    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Package().getMapHolder_EObjectToEObjectKeyContainedMap(), objects);
  }

  public void testEObjectToEObjectValueContainedMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<Order, OrderDetail>();

    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Package().getMapHolder_EObjectToEObjectValueContainedMap(), objects);
  }

  public void testEObjectToEObjectBothContainedMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<Order, OrderDetail>();

    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Package().getMapHolder_EObjectToEObjectBothContainedMap(), objects);
  }

  private void testMap(EReference feature, Map objects) throws Exception
  {
    boolean keyIsReference = false;
    boolean valueIsReference = false;
    boolean keyNotContained = false;
    boolean valueNotContained = false;

    EClass eType = (EClass)feature.getEType();
    for (EStructuralFeature f : eType.getEAllStructuralFeatures())
    {
      if (f.getName().equals("key"))
      {
        if (f instanceof EReference)
        {
          keyIsReference = true;
          if (!((EReferenceImpl)f).isContainment())
          {
            keyNotContained = true;
          }
        }
      }
      else if (f.getName().equals("value"))
      {
        if (f instanceof EReference)
        {
          valueIsReference = true;
          if (!((EReferenceImpl)f).isContainment())
          {
            valueNotContained = true;
          }
        }
      }
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");

      MapHolder mapHolder = getModel2Factory().createMapHolder();

      EMap map = (EMap)mapHolder.eGet(feature);

      for (Object key : objects.keySet())
      {
        map.put(key, objects.get(key));
      }

      compareSimple(objects, map);

      resource.getContents().add(mapHolder);
      if (keyNotContained) // avoid dangling references if needed
      {
        for (Object key : map.keySet())
        {
          resource.getContents().add((EObject)key);
        }
      }
      if (valueNotContained) // avoid dangling references if needed
      {
        for (Object value : map.values())
        {
          resource.getContents().add((EObject)value);
        }
      }

      compareSimple(objects, map);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/test1");

      MapHolder mapHolder = (MapHolder)resource.getContents().get(0);

      EMap map = (EMap)mapHolder.eGet(feature);

      compare(objects, keyIsReference, valueIsReference, map);

      assertEquals(map.size(), objects.size());

      map.remove(0);

      assertEquals(map.size(), objects.size() - 1);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/test1");

      MapHolder mapHolder = (MapHolder)resource.getContents().get(0);

      EMap map = (EMap)mapHolder.eGet(feature);

      compare(objects, keyIsReference, valueIsReference, map);

      assertEquals(map.size(), objects.size() - 1);

      map.clear();

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/test1");

      MapHolder mapHolder = (MapHolder)resource.getContents().get(0);

      EMap map = (EMap)mapHolder.eGet(feature);
      assertEquals(map.size(), 0);

      transaction.close();
      session.close();
    }
  }

  private void compareSimple(Map objects, EMap map)
  {
    for (Object key : objects.keySet())
    {
      assertEquals(true, map.keySet().contains(key));
      assertEquals(true, map.values().contains(objects.get(key)));
    }
  }

  /**
   * This one looks a bit complicated but we must make sure the reference objects a compared by CDO IDs. Because the
   * test case is designed to be as generic as possible to allow easy enhancement.
   */
  private void compare(Map objects, boolean keyIsReference, boolean valueIsReference, EMap map)
  {
    for (Object key : map.keySet())
    {

      if (!keyIsReference)
      {
        assertEquals(true, objects.keySet().contains(key));
      }
      else
      {
        boolean foundObjectWithSameID = false;
        for (Object keyMap : objects.keySet())
        {
          CDOID valueID = CDOUtil.getCDOObject((EObject)key).cdoID();
          CDOID valueMapID = CDOUtil.getCDOObject((EObject)keyMap).cdoID();

          if (valueID.equals(valueMapID))
          {
            foundObjectWithSameID = true;
          }
        }
        if (!foundObjectWithSameID)
        {
          fail("key reference with CDOID could not be found");
        }
      }
      if (!valueIsReference)
      {
        assertEquals(true, objects.keySet().contains(key));
      }
      else
      {
        boolean foundObjectWithSameID = false;
        for (Object valueMap : objects.values())
        {
          CDOID valueID = CDOUtil.getCDOObject((EObject)map.get(key)).cdoID();
          CDOID valueMapID = CDOUtil.getCDOObject((EObject)valueMap).cdoID();

          if (valueID.equals(valueMapID))
          {
            foundObjectWithSameID = true;
          }
        }
        if (!foundObjectWithSameID)
        {
          fail("value reference with CDOID could not be found");
        }
      }
    }
  }
}
