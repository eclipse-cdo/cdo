/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.embedded.EmbeddedClientSessionConfiguration;
import org.eclipse.emf.cdo.internal.server.offline.ClonedRepository;
import org.eclipse.emf.cdo.internal.server.offline.MasterInterface;
import org.eclipse.emf.cdo.server.embedded.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOServerUtil
{
  private CDOServerUtil()
  {
  }

  /**
   * @since 3.0
   */
  public static CDOSessionConfiguration createSessionConfiguration()
  {
    return new EmbeddedClientSessionConfiguration();
  }

  public static IRepository createRepository(String name, IStore store, Map<String, String> props)
  {
    Repository repository = new Repository.Default();
    repository.setName(name);
    repository.setStore(store);
    repository.setProperties(props);
    return repository;
  }

  /**
   * @since 3.0
   */
  public static IRepository createClonedRepository(String name, IStore store, Map<String, String> props,
      MasterInterface masterInterface)
  {
    ClonedRepository repository = new ClonedRepository();
    repository.setName(name);
    repository.setStore(store);
    repository.setProperties(props);
    repository.setMasterInterface(masterInterface);
    return repository;
  }

  public static void addRepository(IManagedContainer container, IRepository repository)
  {
    container.putElement(RepositoryFactory.PRODUCT_GROUP, RepositoryFactory.TYPE, repository.getName(), repository);
    LifecycleUtil.activate(repository);
  }

  public static IRepository getRepository(IManagedContainer container, String name)
  {
    return RepositoryFactory.get(container, name);
  }

  public static Element getRepositoryConfig(String repositoryName) throws ParserConfigurationException, SAXException,
      IOException
  {
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo.server.xml"); //$NON-NLS-1$

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(configFile);
    NodeList elements = document.getElementsByTagName("repository"); //$NON-NLS-1$
    for (int i = 0; i < elements.getLength(); i++)
    {
      Node node = elements.item(i);
      if (node instanceof Element)
      {
        Element element = (Element)node;
        String name = element.getAttribute("name"); //$NON-NLS-1$
        if (ObjectUtil.equals(name, repositoryName))
        {
          return element;
        }
      }
    }

    throw new IllegalStateException("CDORepositoryInfo config not found: " + repositoryName); //$NON-NLS-1$
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static abstract class RepositoryReadAccessValidator implements IRepository.ReadAccessHandler
  {
    public RepositoryReadAccessValidator()
    {
    }

    public void handleRevisionsBeforeSending(ISession session, CDORevision[] revisions,
        List<CDORevision> additionalRevisions) throws RuntimeException
    {
      List<String> violations = new ArrayList<String>();
      for (CDORevision revision : revisions)
      {
        String violation = validate(session, revision);
        if (violation != null)
        {
          violations.add(violation);
        }
      }

      if (!violations.isEmpty())
      {
        throwException(session, violations);
      }

      for (Iterator<CDORevision> it = additionalRevisions.iterator(); it.hasNext();)
      {
        CDORevision revision = it.next();
        String violation = validate(session, revision);
        if (violation != null)
        {
          OM.LOG.info("Revision can not be delivered to " + session + ": " + violation); //$NON-NLS-1$ //$NON-NLS-2$
          it.remove();
        }
      }
    }

    protected void throwException(ISession session, List<String> violations) throws RuntimeException
    {
      StringBuilder builder = new StringBuilder();
      builder.append("Revisions can not be delivered to "); //$NON-NLS-1$
      builder.append(session);
      builder.append(":"); //$NON-NLS-1$
      for (String violation : violations)
      {
        builder.append("\n- "); //$NON-NLS-1$
        builder.append(violation);
      }

      throwException(builder.toString());
    }

    protected void throwException(String message) throws RuntimeException
    {
      throw new IllegalStateException(message);
    }

    protected abstract String validate(ISession session, CDORevision revision);
  }
}
