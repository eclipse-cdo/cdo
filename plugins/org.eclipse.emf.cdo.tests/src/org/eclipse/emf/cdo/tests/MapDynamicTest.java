/*
 * Copyright (c) 2010-2012, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Note! This class should never be attached to our the suites until this notification is removed. Otherwise the test
 * run will fail because this test cases is not yet integrated into our test framework! Only run this test as local
 * JUnit test case.
 *
 * @author Martin Fluegge
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapDynamicTest extends MapTest
{
  private EPackage dynamicPackage;

  private Map objects;

  private EStructuralFeature feature;

  private final EObject container;

  public MapDynamicTest(EObject mapContainer, EReference mapFeature, Map objects2, EPackage dynamicMapEPackge)
  {
    container = mapContainer;
    feature = mapFeature;
    objects = objects2;
    dynamicPackage = dynamicMapEPackge;
    super.setName(feature.getName() + "Test");
  }

  @Override
  protected void runTest() throws Throwable
  {
    testMap(container, (EReference)feature, objects, dynamicPackage);
  }

  public static Test suite() throws Exception
  {
    TestSuite suite = new TestSuite(MapDynamicTest.class.getName());

    EPackage dynamicMapEPackge = createPackage();
    EFactory dynamicMapEFactoryInstance = dynamicMapEPackge.getEFactoryInstance();

    for (EStructuralFeature mapFeature : mapContainerEClass.getEAllStructuralFeatures())
    {
      EObject mapContainer = dynamicMapEFactoryInstance.create(mapContainerEClass);
      System.out.println(mapFeature);
      if (mapFeature.getName().endsWith("Map"))
      {

        Map objects = new HashMap();
        List key = dummyData.get(getEDataType(mapFeature, "key"));
        List value = dummyData.get(getEDataType(mapFeature, "value"));
        System.out.println("Testing " + mapFeature.getName() + "key: " + key + " / value: " + value);
        for (int i = 0; i < 3; i++)
        {
          objects.put(key.get(i), value.get(i));
        }

        // do the actual test
        suite.addTest(new MapDynamicTest(mapContainer, (EReference)mapFeature, objects, dynamicMapEPackge));
      }
    }

    return suite;
  }

  private static EDataType getEDataType(EStructuralFeature feature, String type) throws Exception
  {
    EClass eType = (EClass)feature.getEType();
    for (EStructuralFeature f : eType.getEAllStructuralFeatures())
    {
      if (f.getName().equals(type))
      {
        return (EDataType)f.getEType();
      }
    }

    throw new Exception("Could not find " + type + " for " + feature);
  }

  private void testMap(EObject container, EReference feature, Map objects, EPackage epackage) throws Exception
  {
    boolean keyIsReference = false;
    boolean valueIsReference = false;
    boolean keyNotContained = false;
    boolean valueNotContained = false;

    String resourceName = "/test1" + count++;

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
      CDOResource resource = transaction.getResource(getResourcePath(resourceName));

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

      CDOResource resource = transaction.getResource(getResourcePath(resourceName));

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
      CDOResource resource = transaction.getResource(getResourcePath(resourceName));

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

  // private static EPackage createPackage()
  // {
  //
  // EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
  // EcorePackage theCorePackage = EcorePackage.eINSTANCE;
  //
  // mapContainerEClass = theCoreFactory.createEClass();
  // mapContainerEClass.setName("MapContainer");
  //
  // EPackage dynamicMapEPackage = theCoreFactory.createEPackage();
  // dynamicMapEPackage.setName("DynamicMapPackage");
  // dynamicMapEPackage.setNsPrefix("dynamicmap");
  // dynamicMapEPackage.setNsURI("http:///org.mftech.examples.emf.dynamic.map");
  //
  // dynamicMapEPackage.getEClassifiers().add(mapContainerEClass);
  //
  // // ++++++++++++++ create dynamic
  // // TODO provide Reference mapping
  // Map<EDataType, Boolean> dataTypes = new HashMap<EDataType, Boolean>();
  // dataTypes.put(theCorePackage.getEBigDecimal(), true);
  // dataTypes.put(theCorePackage.getEBigInteger(), true);
  // dataTypes.put(theCorePackage.getEBoolean(), true);
  // dataTypes.put(theCorePackage.getEBooleanObject(), true);
  // dataTypes.put(theCorePackage.getEByte(), true);
  // // //dataTypes.put(theCorePackage.getEByteArray(), true);
  // dataTypes.put(theCorePackage.getEByteObject(), true);
  // dataTypes.put(theCorePackage.getEChar(), true);
  // // dataTypes.put(theCorePackage.getECharacterObject(), false);
  // dataTypes.put(theCorePackage.getEDate(), true);
  // // // dataTypes.put(theCorePackage.getEDiagnosticChain(), true);
  // dataTypes.put(theCorePackage.getEDouble(), true);
  // // dataTypes.put(theCorePackage.getEDoubleObject(), false);
  // dataTypes.put(theCorePackage.getEFloat(), true);
  // // dataTypes.put(theCorePackage.getEFloatObject(), false);
  // dataTypes.put(theCorePackage.getEInt(), true);
  // // dataTypes.put(theCorePackage.getEIntegerObject(), false);
  // dataTypes.put(theCorePackage.getEJavaObject(), true);
  // dataTypes.put(theCorePackage.getEJavaClass(), true);
  // dataTypes.put(theCorePackage.getELong(), true);
  // // dataTypes.put(theCorePackage.getELongObject(), false);
  // // dataTypes.put(theCorePackage.getEMap(), false);
  // dataTypes.put(theCorePackage.getEShort(), true);
  // dataTypes.put(theCorePackage.getEShortObject(), true);
  // dataTypes.put(theCorePackage.getEString(), true);
  //
  // System.out.println("Start");
  //
  // int i = 0;
  // for (EDataType keyDataType : dataTypes.keySet())
  // {
  // String keyName = keyDataType.getName();
  // for (EDataType valueDataType : dataTypes.keySet())
  // {
  //
  // String valueName = valueDataType.getName();
  // String mapName = keyName + "To" + valueName + "Map";
  //
  // EClass mapEClass = theCoreFactory.createEClass();
  // mapEClass.setName(mapName);
  // mapEClass.setInstanceTypeName("java.util.Map$Map.Entry");
  //
  // EStructuralFeature key;
  // if (dataTypes.get(keyDataType))
  // {
  // key = theCoreFactory.createEAttribute();
  // }
  // else
  // {
  // key = theCoreFactory.createEReference();
  // }
  //
  // key.setName("key");
  // key.setEType(keyDataType);
  //
  // // EStructuralFeature value = theCoreFactory.createEAttribute();
  // EStructuralFeature value;
  // if (dataTypes.get(valueDataType))
  // {
  // value = theCoreFactory.createEAttribute();
  // }
  // else
  // {
  // value = theCoreFactory.createEReference();
  // }
  // value.setName("value");
  // value.setEType(valueDataType);
  //
  // mapEClass.getEStructuralFeatures().add(key);
  // mapEClass.getEStructuralFeatures().add(value);
  //
  // EReference containerMapFeature = theCoreFactory.createEReference();
  //
  // containerMapFeature.setName(mapName);
  // containerMapFeature.setEType(mapEClass);
  // containerMapFeature.setResolveProxies(false);
  // containerMapFeature.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
  // containerMapFeature.setContainment(true);
  //
  // dynamicMapEPackage.getEClassifiers().add(mapEClass);
  // mapContainerEClass.getEStructuralFeatures().add(containerMapFeature);
  //
  // System.out.println("Created " + mapName);
  // i++;
  // }
  // }
  //
  // System.out.println("Created " + i + " maps.");
  //
  // initDummyData(theCorePackage);
  // CDOUtil.prepareDynamicEPackage(dynamicMapEPackage);
  // return dynamicMapEPackage;
  //
  // }

  // private static void initDummyData(EcorePackage theCorePackage)
  // {
  // List data = new ArrayList();
  // data.add("A");
  // data.add("B");
  // data.add("C");
  // dummyData.put(theCorePackage.getEString(), data);
  //
  // data = new ArrayList();
  // data.add(new BigInteger("11111111111111"));
  // data.add(new BigInteger("22222222222222"));
  // data.add(new BigInteger("333333333333333"));
  // dummyData.put(theCorePackage.getEBigInteger(), data);
  //
  // data = new ArrayList();
  // data.add(new BigDecimal("44444444444444"));
  // data.add(new BigDecimal("55555555555555"));
  // data.add(new BigDecimal("66666666666666"));
  // dummyData.put(theCorePackage.getEBigDecimal(), data);
  //
  // data = new ArrayList();
  // data.add(true);
  // data.add(false);
  // data.add(true);
  // dummyData.put(theCorePackage.getEBoolean(), data);
  //
  // data = new ArrayList();
  // data.add(Boolean.valueOf(true));
  // data.add(Boolean.valueOf("false"));
  // data.add(Boolean.valueOf(true));
  // dummyData.put(theCorePackage.getEBooleanObject(), data);
  //
  // data = new ArrayList();
  // data.add((byte)1);
  // data.add((byte)2);
  // data.add((byte)3);
  // dummyData.put(theCorePackage.getEByte(), data);
  //
  // // TODO create additional dummy Data
  // // dummyData.put(theCorePackage.getEByteArray(), data);
  //
  // data = new ArrayList();
  // data.add(Byte.valueOf((byte)1));
  // data.add(Byte.valueOf("2"));
  // data.add(Byte.valueOf((byte)3));
  // dummyData.put(theCorePackage.getEByteObject(), data);
  //
  // data = new ArrayList();
  // data.add('a');
  // data.add((char)99);
  // data.add('c');
  // dummyData.put(theCorePackage.getEChar(), data);
  //
  // data = new ArrayList();
  // data.add(Character.valueOf('a'));
  // data.add(Character.valueOf((char)99));
  // data.add(Character.valueOf('c'));
  // dummyData.put(theCorePackage.getECharacterObject(), data);
  //
  // data = new ArrayList();
  // data.add(new Date());
  // data.add(new Date());
  // data.add(new Date());
  // dummyData.put(theCorePackage.getEDate(), data);
  //
  // // TODO create additional dummy Data
  // // dummyData.put(theCorePackage.getEDiagnosticChain(), data);
  //
  // data = new ArrayList();
  // data.add(111.111D);
  // data.add(222.222D);
  // data.add(333.333D);
  // dummyData.put(theCorePackage.getEDouble(), data);
  //
  // data = new ArrayList();
  // data.add(Double.valueOf(444.444));
  // data.add(Double.valueOf("555.555"));
  // data.add(Double.valueOf(666.666));
  // dummyData.put(theCorePackage.getEDoubleObject(), data);
  //
  // data = new ArrayList();
  // data.add(111.0001f);
  // data.add(222.0002f);
  // data.add(333.0003f);
  // dummyData.put(theCorePackage.getEFloat(), data);
  //
  // data = new ArrayList();
  // data.add(Float.valueOf(444.0004f));
  // data.add(Float.valueOf(555.0005d));
  // data.add(Float.valueOf("666.0006"));
  // dummyData.put(theCorePackage.getEFloatObject(), data);
  //
  // data = new ArrayList();
  // data.add(11);
  // data.add(22);
  // data.add(33);
  // dummyData.put(theCorePackage.getEInt(), data);
  //
  // data = new ArrayList();
  // data.add(Integer.valueOf(44));
  // data.add(Integer.valueOf("55"));
  // data.add(Integer.valueOf(66));
  // dummyData.put(theCorePackage.getEIntegerObject(), data);
  //
  // data = new ArrayList();
  // data.add(Long.valueOf(1));
  // data.add(new Date());
  // data.add(Integer.valueOf(2));
  // dummyData.put(theCorePackage.getEJavaObject(), data);
  //
  // data = new ArrayList();
  // data.add(Object.class);
  // data.add(Date.class);
  // data.add(Long.class);
  // dummyData.put(theCorePackage.getEJavaClass(), data);
  //
  // data = new ArrayList();
  // data.add(10101010101L);
  // data.add(20202020202L);
  // data.add(30303030303L);
  // dummyData.put(theCorePackage.getELong(), data);
  //
  // data = new ArrayList();
  // data.add(Long.valueOf(40404040404L));
  // data.add(Long.valueOf("5050505050505"));
  // data.add(Long.valueOf(6060606060606L));
  // dummyData.put(theCorePackage.getELongObject(), data);
  //
  // data = new ArrayList();
  //
  // Map map = new HashMap();
  // map.put(1, 2);
  // map.put(3, 4);
  // data.add(map);
  // map = new HashMap();
  // map.put("A", "B");
  // map.put("C", "D");
  // data.add(map);
  // map = new HashMap();
  // map.put('a', 'b');
  // map.put('c', 'd');
  // data.add(map);
  // dummyData.put(theCorePackage.getEMap(), data);
  //
  // data = new ArrayList();
  // data.add((short)111);
  // data.add((short)222);
  // data.add((short)333);
  // dummyData.put(theCorePackage.getEShort(), data);
  //
  // data = new ArrayList();
  // data.add(Short.valueOf((short)111));
  // data.add(Short.valueOf((short)222));
  // data.add(Short.valueOf((short)333));
  // dummyData.put(theCorePackage.getEShortObject(), data);
  //
  // }
}
