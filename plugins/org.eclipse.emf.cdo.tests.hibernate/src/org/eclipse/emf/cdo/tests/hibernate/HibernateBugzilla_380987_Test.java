/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Taal
 */
@CleanRepositoriesBefore
public class HibernateBugzilla_380987_Test extends AbstractCDOTest
{
  // in hsqldb several eclasses do not work, they work fine in mysql
  @Override
  protected void doSetUp() throws Exception
  {
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057A());
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057A1());
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057B());
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057B1());
    super.doSetUp();
  }

  private void setOrRemoveTransient(EClass eClass)
  {
    if (eClass.getEAnnotation("teneo.jpa") != null)
    {
      eClass.getEAnnotations().remove(eClass.getEAnnotation("teneo.jpa"));
    }
    else
    {
      final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
      eAnnotation.setSource("teneo.jpa");
      eAnnotation.getDetails().put("value", "@Transient");
      eClass.getEAnnotations().add(eAnnotation);
    }
  }

  @Override
  protected void doTearDown() throws Exception
  {
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057A());
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057A1());
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057B());
    setOrRemoveTransient(HibernateTestPackage.eINSTANCE.getBz398057B1());
    super.doTearDown();
  }

  public void testBugzilla() throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      transaction.createResource(getResourcePath("/res2"));
      transaction.createResource(getResourcePath("/res3"));

      List<Bz380987_Person> persons = new ArrayList<Bz380987_Person>();
      List<Bz380987_Place> places = new ArrayList<Bz380987_Place>();
      List<Bz380987_Group> groups = new ArrayList<Bz380987_Group>();
      for (int i = 0; i < 5; i++)
      {
        Bz380987_Person person = HibernateTestFactory.eINSTANCE.createBz380987_Person();
        person.setName(i + "");
        persons.add(person);
        Bz380987_Place place = HibernateTestFactory.eINSTANCE.createBz380987_Place();
        place.setName(i + "");
        places.add(place);
        Bz380987_Group group = HibernateTestFactory.eINSTANCE.createBz380987_Group();
        groups.add(group);
      }
      for (int i = 0; i < 5; i++)
      {
        persons.get(i).getPlaces().addAll(places);
        places.get(i).getPeople().addAll(persons);
        groups.get(i).getPeople().addAll(persons);
      }

      resource.getContents().addAll(persons);
      resource.getContents().addAll(places);
      resource.getContents().addAll(groups);
      transaction.commit();
      session.close();

      CDOServerExporter.XML exporter = new CDOServerExporter.XML(getRepository());
      exporter.exportRepository(baos);
      System.out.println(baos.toString());
    }

    // clear the repo
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      resource.getContents().clear();
      transaction.commit();
      transaction.close();
      session.close();
    }

    // repo is cleared
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      assertEquals(0, resource.getContents().size());
      transaction.commit();
      transaction.close();
      session.close();
    }

    {
      InternalRepository repo3 = getRepository("repo3", false);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      CDOServerImporter.XML importer = new CDOServerImporter.XML(repo3);
      importer.importRepository(bais);
    }

    testRepo("repo3");
  }

  private void testRepo(String repoName) throws CommitException
  {
    CDOSession session = openSession(repoName);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/res1"));

    for (Object o : resource.getContents())
    {
      final CDOObject cdoObject = CDOUtil.getCDOObject((EObject)o);
      System.err.println(cdoObject.cdoID());
      System.err.println(cdoObject.cdoID());
    }

    CDOResource resource2 = transaction.getResource(getResourcePath("/res2"));
    CDOResource resource3 = transaction.getResource(getResourcePath("/res3"));
    assertNotNull(resource2);
    assertNotNull(resource3);

    List<Bz380987_Person> persons = new ArrayList<Bz380987_Person>();
    List<Bz380987_Place> places = new ArrayList<Bz380987_Place>();
    List<Bz380987_Group> groups = new ArrayList<Bz380987_Group>();
    for (int i = 0; i < 15; i++)
    {
      if (i < 5)
      {
        persons.add((Bz380987_Person)resource.getContents().get(i));
      }
      else if (i < 10)
      {
        places.add((Bz380987_Place)resource.getContents().get(i));
      }
      else
      {
        groups.add((Bz380987_Group)resource.getContents().get(i));
      }
    }
    assertEquals(5, persons.size());
    assertEquals(5, places.size());
    assertEquals(5, groups.size());
    for (int i = 0; i < 5; i++)
    {
      assertEquals(5, persons.get(i).getGroup().size());
      assertEquals(5, persons.get(i).getPlaces().size());
      assertEquals(5, places.get(i).getPeople().size());
      assertEquals(5, groups.get(i).getPeople().size());
    }

    for (int i = 0; i < 5; i++)
    {
      for (Object o : persons.get(i).getGroup())
      {
        final Bz380987_Group gr = (Bz380987_Group)o;
        System.err.println(gr.getPeople().size());
      }
      persons.get(i).getGroup().removeAll(groups);
      assertEquals(0, persons.get(i).getGroup().size());
      persons.get(i).getPlaces().removeAll(places);
      assertEquals(0, persons.get(i).getPlaces().size());

      places.get(i).getPeople().removeAll(persons);
      assertEquals(0, places.get(i).getPeople().size());

      groups.get(i).getPeople().removeAll(persons);
      assertEquals(0, groups.get(i).getPeople().size());
    }
    transaction.rollback();
    session.close();
  }
}
