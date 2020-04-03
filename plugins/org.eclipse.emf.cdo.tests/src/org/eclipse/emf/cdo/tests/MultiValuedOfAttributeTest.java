/*
 * Copyright (c) 2008-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Simon McDuff
 */
@Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
public class MultiValuedOfAttributeTest extends AbstractCDOTest
{
  public void testListOfString() throws Exception
  {
    List<String> list = new ArrayList<>();
    list.add("Ottawa");
    list.add("Toronto");
    list.add("Berlin");
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfString(), getModel5Package().getGenListOfString_Elements());
  }

  public void testListOfDate() throws Exception
  {
    // Mysql TIMESTAMP values do not support milliseconds!
    List<Date> list = new ArrayList<>();
    list.add(new GregorianCalendar(2018, 4, 1, 6, 45, 14).getTime());
    list.add(new GregorianCalendar(2019, 5, 2, 6, 45, 15).getTime());
    list.add(new GregorianCalendar(2020, 6, 3, 6, 45, 16).getTime());
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfDate(), getModel5Package().getGenListOfDate_Elements());
  }

  public void testListOfInt() throws Exception
  {
    List<Integer> list = new ArrayList<>();
    list.add(10);
    list.add(11);
    list.add(20);
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfInt(), getModel5Package().getGenListOfInt_Elements());
  }

  public void testListOfShort() throws Exception
  {
    List<Short> list = new ArrayList<>();
    list.add((short)10);
    list.add((short)11);
    list.add((short)20);
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfShort(), getModel5Package().getGenListOfShort_Elements());
  }

  public void testListOfFloat() throws Exception
  {
    List<Float> list = new ArrayList<>();
    list.add((float)10);
    list.add((float)11);
    list.add((float)20);
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfFloat(), getModel5Package().getGenListOfFloat_Elements());
  }

  public void testListOfChar() throws Exception
  {
    List<Character> list = new ArrayList<>();
    list.add('c');
    list.add('d');
    list.add('z');
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfChar(), getModel5Package().getGenListOfChar_Elements());
  }

  public void testListOfBoolean() throws Exception
  {
    List<Boolean> list = new ArrayList<>();
    list.add(true);
    list.add(false);
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfBoolean(), getModel5Package().getGenListOfBoolean_Elements());
  }

  public void testListOfDouble() throws Exception
  {
    List<Double> list = new ArrayList<>();
    list.add(10.1928);
    list.add(11.12);
    list.add(20.99991);
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfDouble(), getModel5Package().getGenListOfDouble_Elements());
  }

  public void testListOfInteger() throws Exception
  {
    List<Integer> list = new ArrayList<>();
    list.add(10);
    list.add(null);
    list.add(20);

    EAttribute elements = getModel5Package().getGenListOfInteger_Elements();
    EClass containerClass = getModel5Package().getGenListOfInteger();
    testMultiValuedIOfAttribute(list, containerClass, elements);
  }

  protected <T> void testMultiValuedIOfAttribute(List<T> list, EClass containerClass, EStructuralFeature feature) throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));

      EObject eGenObject = EcoreUtil.create(containerClass);

      @SuppressWarnings("unchecked")
      EList<T> elements = (EList<T>)eGenObject.eGet(feature);

      for (int i = 0; i < list.size() - 1; i++)
      {
        elements.add(list.get(i));
      }

      resource.getContents().add(eGenObject);
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));

      EObject eGenObject = resource.getContents().get(0);

      @SuppressWarnings("unchecked")
      EList<T> elements = (EList<T>)eGenObject.eGet(feature);

      for (int i = 0; i < list.size() - 1; i++)
      {
        assertEquals(elements.get(i), list.get(i));
      }

      elements.add(list.get(list.size() - 1));
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 100));
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));

      EObject eGenObject = resource.getContents().get(0);

      @SuppressWarnings("unchecked")
      EList<T> elements = (EList<T>)eGenObject.eGet(feature);

      for (int i = 0; i < list.size() - 1; i++)
      {
        assertEquals(elements.get(i), list.get(i));
      }

      transaction.commit();
    }
  }
}
