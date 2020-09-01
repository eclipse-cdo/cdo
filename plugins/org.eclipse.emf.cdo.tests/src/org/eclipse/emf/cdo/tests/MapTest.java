/*
 * Copyright (c) 2010-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Note that if you receive errors in the test class or if you worked on anything related to maps, it is highly
 * recommended to comment in the methods <i>testDynamicMaps()</i> and <i>getEDataType(EStructuralFeature feature, String
 * type)</i> below and re-run the test. Or run MapDynamicTest as local JUnit test run. If you do not need the test to be
 * integrated into our test framework the second approach is recommended because it gives you a detailed overview over
 * the passing/failing tests.
 *
 * @author Martin Fluegge
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapTest extends AbstractCDOTest
{
  protected static EClass mapContainerEClass;

  protected static Map<EDataType, List> dummyData = new HashMap<>();

  protected static int count;

  public void testTransientMap() throws Exception
  {
    PropertiesMapEntryValue value1 = getModel6Factory().createPropertiesMapEntryValue();
    value1.setLabel("value1");

    PropertiesMapEntryValue value2 = getModel6Factory().createPropertiesMapEntryValue();
    value2.setLabel("value2");

    PropertiesMap propertiesMap = getModel6Factory().createPropertiesMap();
    propertiesMap.setLabel("TransientMap");
    propertiesMap.getTransientMap().put("key1", value1);
    propertiesMap.getTransientMap().put("key2", value2);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(propertiesMap);

    transaction.commit();
    assertEquals(false, EMFUtil.isPersistent(getModel6Package().getPropertiesMap_TransientMap()));
  }

  public void testIntegerStringMap() throws Exception
  {
    Map<Integer, String> objects = new HashMap<>();
    objects.put(1, "1");
    objects.put(2, "2");
    objects.put(3, "3");
    objects.put(4, "4");

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_IntegerToStringMap(), objects, null);
  }

  public void testStringStringMap() throws Exception
  {
    Map<String, String> objects = new HashMap<>();
    objects.put("Eike", "Stepper");
    objects.put("Simon", "McDuff");
    objects.put("Stefan", "Winkler");
    objects.put("Victor", "Roldan Betancort");
    objects.put("Andr�", "Dietisheim");
    objects.put("Ibrahim", "Sallam");
    objects.put("Martin", "Fl�gge");
    objects.put("Caspar", "De Groot");
    objects.put("Martin", "Taal");

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_StringToStringMap(), objects, null);
  }

  public void testStringVATMap() throws Exception
  {
    Map<String, VAT> objects = new HashMap<>();
    objects.put("VAT0", VAT.VAT0);
    objects.put("VAT7", VAT.VAT7);
    objects.put("VAT15", VAT.VAT15);

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_StringToVATMap(), objects, null);
  }

  public void testStringToAddressContainmentMap() throws Exception
  {
    Map<String, Address> objects = new HashMap<>();

    objects.put("address1", getModel1Factory().createAddress());
    objects.put("address2", getModel1Factory().createAddress());
    objects.put("address3", getModel1Factory().createAddress());

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_StringToAddressContainmentMap(), objects, null);
  }

  public void testStringToAddressReferenceMap() throws Exception
  {
    Map<String, Address> objects = new HashMap<>();

    objects.put("address1", getModel1Factory().createAddress());
    objects.put("address2", getModel1Factory().createAddress());
    objects.put("address3", getModel1Factory().createAddress());

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_StringToAddressReferenceMap(), objects, null);
  }

  public void testEObjectToEObjectMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<>();

    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_EObjectToEObjectMap(), objects, null);
  }

  public void testEObjectToEObjectKeyContainedMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<>();

    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_EObjectToEObjectKeyContainedMap(), objects, null);
  }

  public void testEObjectToEObjectValueContainedMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<>();

    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_EObjectToEObjectValueContainedMap(), objects, null);
  }

  public void testEObjectToEObjectBothContainedMap() throws Exception
  {
    Map<Order, OrderDetail> objects = new HashMap<>();

    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());
    objects.put(getModel1Factory().createPurchaseOrder(), getModel1Factory().createOrderDetail());

    testMap(getModel2Factory().createMapHolder(), getModel2Package().getMapHolder_EObjectToEObjectBothContainedMap(), objects, null);
  }

  /**
   * Insert the following two methods if you want to run the dynamically generated maps on the CDO test platform. This
   * test is commented out by default because it takes quite a long time.
   */
  // public void testDynamicMaps() throws Exception
  // {
  // EPackage dynamicMapEPackge = createPackage();
  // EFactory dynamicMapEFactoryInstance = dynamicMapEPackge.getEFactoryInstance();
  //
  // for (EStructuralFeature mapFeature : mapContainerEClass.getEAllStructuralFeatures())
  // {
  // EObject mapContainer = dynamicMapEFactoryInstance.create(mapContainerEClass);
  // System.out.println(mapFeature);
  // if (mapFeature.getName().endsWith("Map"))
  // {
  // Map objects = new HashMap();
  // List key = dummyData.get(getEDataType(mapFeature, "key"));
  // List value = dummyData.get(getEDataType(mapFeature, "value"));
  // System.out.println("Testing " + mapFeature.getName() + "key: " + key + " / value: " + value);
  // for (int i = 0; i < 3; i++)
  // {
  // objects.put(key.get(i), value.get(i));
  // }
  //
  // // do the actual test
  // testMap(mapContainer, (EReference)mapFeature, objects, dynamicMapEPackge);
  // }
  // }
  // }

  // private EDataType getEDataType(EStructuralFeature feature, String type) throws Exception
  // {
  // EClass eType = (EClass)feature.getEType();
  // for (EStructuralFeature f : eType.getEAllStructuralFeatures())
  // {
  // if (f.getName().equals(type))
  // {
  // return (EDataType)f.getEType();
  // }
  // }
  //
  // throw new Exception("Could not find " + type + " for " + feature);
  // }

  private void testMap(EObject container, EReference feature, Map objects, EPackage epackage) throws Exception
  {
    boolean keyIsReference = false;
    boolean valueIsReference = false;
    boolean keyNotContained = false;
    boolean valueNotContained = false;

    String resourceName = getResourcePath("/test1" + count++);

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
      if (epackage != null)
      {
        session.getPackageRegistry().putEPackage(epackage);
      }

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(resourceName);

      EMap map = (EMap)container.eGet(feature);

      for (Object key : objects.keySet())
      {
        map.put(key, objects.get(key));
      }

      compareSimple(objects, map);

      resource.getContents().add(container);
      if (keyNotContained && keyIsReference) // avoid dangling references if needed
      {
        for (Object key : map.keySet())
        {
          if (key instanceof EObject)
          {
            resource.getContents().add((EObject)key);
          }
        }
      }

      if (valueNotContained && valueIsReference) // avoid dangling references if needed
      {
        for (Object value : map.values())
        {
          if (value instanceof EObject)
          {
            resource.getContents().add((EObject)value);
          }
        }
      }

      compareSimple(objects, map);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      if (epackage != null)
      {
        session.getPackageRegistry().putEPackage(epackage);
      }

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(resourceName);

      EObject mapHolder = resource.getContents().get(0);

      for (EStructuralFeature e : mapHolder.eClass().getEAllStructuralFeatures())
      {
        System.out.println(e);
      }

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
      if (epackage != null)
      {
        session.getPackageRegistry().putEPackage(epackage);
      }

      CDOResource resource = transaction.getResource(resourceName);

      EObject mapHolder = resource.getContents().get(0);

      EMap map = (EMap)mapHolder.eGet(feature);

      compare(objects, keyIsReference, valueIsReference, map);

      assertEquals(map.size(), objects.size() - 1);

      map.clear();

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      if (epackage != null)
      {
        session.getPackageRegistry().putEPackage(epackage);
      }

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(resourceName);

      EObject mapHolder = resource.getContents().get(0);

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

          if (valueID == valueMapID)
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

          if (valueID == valueMapID)
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

  public static EPackage createPackage()
  {
    EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
    EcorePackage theCorePackage = EcorePackage.eINSTANCE;

    mapContainerEClass = theCoreFactory.createEClass();
    mapContainerEClass.setName("MapContainer");

    EPackage dynamicMapEPackage = theCoreFactory.createEPackage();
    dynamicMapEPackage.setName("DynamicMapPackage");
    dynamicMapEPackage.setNsPrefix("dynamicmap");
    dynamicMapEPackage.setNsURI("http:///org.mftech.examples.emf.dynamic.map");

    dynamicMapEPackage.getEClassifiers().add(mapContainerEClass);

    // ++++++++++++++ create dynamic
    // TODO provide Reference mapping
    Map<EDataType, Boolean> dataTypes = new HashMap<>();
    dataTypes.put(theCorePackage.getEBigDecimal(), true);
    dataTypes.put(theCorePackage.getEBigInteger(), true);
    dataTypes.put(theCorePackage.getEBoolean(), true);
    dataTypes.put(theCorePackage.getEBooleanObject(), true);
    dataTypes.put(theCorePackage.getEByte(), true);
    // dataTypes.put(theCorePackage.getEByteArray(), true);
    dataTypes.put(theCorePackage.getEByteObject(), true);
    dataTypes.put(theCorePackage.getEChar(), true);
    dataTypes.put(theCorePackage.getECharacterObject(), true);
    dataTypes.put(theCorePackage.getEDate(), true);
    // // dataTypes.put(theCorePackage.getEDiagnosticChain(), true);
    dataTypes.put(theCorePackage.getEDouble(), true);
    dataTypes.put(theCorePackage.getEDoubleObject(), true);
    dataTypes.put(theCorePackage.getEFloat(), true);
    dataTypes.put(theCorePackage.getEFloatObject(), true);
    dataTypes.put(theCorePackage.getEInt(), true);
    dataTypes.put(theCorePackage.getEIntegerObject(), true);
    dataTypes.put(theCorePackage.getEJavaObject(), true);
    dataTypes.put(theCorePackage.getEJavaClass(), true);
    dataTypes.put(theCorePackage.getELong(), true);
    dataTypes.put(theCorePackage.getELongObject(), true);
    // dataTypes.put(theCorePackage.getEMap(), true);
    dataTypes.put(theCorePackage.getEShort(), true);
    dataTypes.put(theCorePackage.getEShortObject(), true);
    dataTypes.put(theCorePackage.getEString(), true);

    System.out.println("Start");

    int i = 0;
    for (EDataType keyDataType : dataTypes.keySet())
    {
      String keyName = keyDataType.getName();
      for (EDataType valueDataType : dataTypes.keySet())
      {

        String valueName = valueDataType.getName();
        String mapName = keyName + "To" + valueName + "Map";

        EClass mapEClass = theCoreFactory.createEClass();
        mapEClass.setName(mapName);
        mapEClass.setInstanceTypeName("java.util.Map$Entry");

        EStructuralFeature key;
        if (dataTypes.get(keyDataType))
        {
          key = theCoreFactory.createEAttribute();
        }
        else
        {
          key = theCoreFactory.createEReference();
        }

        key.setName("key");
        key.setEType(keyDataType);

        // EStructuralFeature value = theCoreFactory.createEAttribute();
        EStructuralFeature value;
        if (dataTypes.get(valueDataType))
        {
          value = theCoreFactory.createEAttribute();
        }
        else
        {
          value = theCoreFactory.createEReference();
        }

        value.setName("value");
        value.setEType(valueDataType);

        mapEClass.getEStructuralFeatures().add(key);
        mapEClass.getEStructuralFeatures().add(value);

        EReference containerMapFeature = theCoreFactory.createEReference();

        containerMapFeature.setName(mapName);
        containerMapFeature.setEType(mapEClass);
        containerMapFeature.setResolveProxies(false);
        containerMapFeature.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
        containerMapFeature.setContainment(true);

        dynamicMapEPackage.getEClassifiers().add(mapEClass);
        mapContainerEClass.getEStructuralFeatures().add(containerMapFeature);

        System.out.println("Created " + mapName);
        i++;
      }
    }

    System.out.println("Created " + i + " maps.");

    initDummyData(theCorePackage);
    CDOUtil.prepareDynamicEPackage(dynamicMapEPackage);
    return dynamicMapEPackage;

  }

  public static void initDummyData(EcorePackage theCorePackage)
  {
    List data = new ArrayList();
    data.add("A");
    data.add("B");
    data.add("C");
    dummyData.put(theCorePackage.getEString(), data);

    data = new ArrayList();
    data.add(new BigInteger("11111111111111"));
    data.add(new BigInteger("22222222222222"));
    data.add(new BigInteger("333333333333333"));
    dummyData.put(theCorePackage.getEBigInteger(), data);

    data = new ArrayList();
    data.add(new BigDecimal("44444444444444"));
    data.add(new BigDecimal("55555555555555"));
    data.add(new BigDecimal("66666666666666"));
    dummyData.put(theCorePackage.getEBigDecimal(), data);

    data = new ArrayList();
    data.add(true);
    data.add(false);
    data.add(true);
    dummyData.put(theCorePackage.getEBoolean(), data);

    data = new ArrayList();
    data.add(Boolean.valueOf(true));
    data.add(Boolean.valueOf("false"));
    data.add(Boolean.valueOf(true));
    dummyData.put(theCorePackage.getEBooleanObject(), data);

    data = new ArrayList();
    data.add((byte)1);
    data.add((byte)2);
    data.add((byte)3);
    dummyData.put(theCorePackage.getEByte(), data);

    // TODO create additional dummy Data
    // dummyData.put(theCorePackage.getEByteArray(), data);

    data = new ArrayList();
    data.add(Byte.valueOf((byte)1));
    data.add(Byte.valueOf("2"));
    data.add(Byte.valueOf((byte)3));
    dummyData.put(theCorePackage.getEByteObject(), data);

    data = new ArrayList();
    data.add('a');
    data.add((char)99);
    data.add('c');
    dummyData.put(theCorePackage.getEChar(), data);

    data = new ArrayList();
    data.add(Character.valueOf('a'));
    data.add(Character.valueOf((char)99));
    data.add(Character.valueOf('c'));
    dummyData.put(theCorePackage.getECharacterObject(), data);

    data = new ArrayList();
    data.add(new Date());
    data.add(new Date());
    data.add(new Date());
    dummyData.put(theCorePackage.getEDate(), data);

    // TODO create additional dummy Data
    // dummyData.put(theCorePackage.getEDiagnosticChain(), data);

    data = new ArrayList();
    data.add(111.111D);
    data.add(222.222D);
    data.add(333.333D);
    dummyData.put(theCorePackage.getEDouble(), data);

    data = new ArrayList();
    data.add(Double.valueOf(444.444));
    data.add(Double.valueOf("555.555"));
    data.add(Double.valueOf(666.666));
    dummyData.put(theCorePackage.getEDoubleObject(), data);

    data = new ArrayList();
    data.add(111.0001f);
    data.add(222.0002f);
    data.add(333.0003f);
    dummyData.put(theCorePackage.getEFloat(), data);

    data = new ArrayList();
    data.add(Float.valueOf(444.0004f));
    data.add(Float.valueOf((float)555.0005d));
    data.add(Float.valueOf("666.0006"));
    dummyData.put(theCorePackage.getEFloatObject(), data);

    data = new ArrayList();
    data.add(11);
    data.add(22);
    data.add(33);
    dummyData.put(theCorePackage.getEInt(), data);

    data = new ArrayList();
    data.add(Integer.valueOf(44));
    data.add(Integer.valueOf("55"));
    data.add(Integer.valueOf(66));
    dummyData.put(theCorePackage.getEIntegerObject(), data);

    data = new ArrayList();
    data.add(Long.valueOf(1));
    data.add(new Date());
    data.add(Integer.valueOf(2));
    dummyData.put(theCorePackage.getEJavaObject(), data);

    data = new ArrayList();
    data.add(Object.class);
    data.add(Date.class);
    data.add(Long.class);
    dummyData.put(theCorePackage.getEJavaClass(), data);

    data = new ArrayList();
    data.add(10101010101L);
    data.add(20202020202L);
    data.add(30303030303L);
    dummyData.put(theCorePackage.getELong(), data);

    data = new ArrayList();
    data.add(Long.valueOf(40404040404L));
    data.add(Long.valueOf("5050505050505"));
    data.add(Long.valueOf(6060606060606L));
    dummyData.put(theCorePackage.getELongObject(), data);

    data = new ArrayList();

    Map map = new HashMap();
    map.put(1, 2);
    map.put(3, 4);
    data.add(map);
    map = new HashMap();
    map.put("A", "B");
    map.put("C", "D");
    data.add(map);
    map = new HashMap();
    map.put('a', 'b');
    map.put('c', 'd');
    data.add(map);
    dummyData.put(theCorePackage.getEMap(), data);

    data = new ArrayList();
    data.add((short)111);
    data.add((short)222);
    data.add((short)333);
    dummyData.put(theCorePackage.getEShort(), data);

    data = new ArrayList();
    data.add(Short.valueOf((short)111));
    data.add(Short.valueOf((short)222));
    data.add(Short.valueOf((short)333));
    dummyData.put(theCorePackage.getEShortObject(), data);
  }
}
