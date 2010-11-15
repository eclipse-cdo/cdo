/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.InputStream;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDOServerImporter
{
  private InternalRepository repository;

  public CDOServerImporter(IRepository repository)
  {
    this.repository = (InternalRepository)repository;
  }

  public final IRepository getRepository()
  {
    return repository;
  }

  public void importRepository(InputStream in) throws Exception
  {
    boolean wasActive = LifecycleUtil.isActive(repository);
    if (!wasActive)
    {
      LifecycleUtil.activate(repository);
    }

    class FlushHandler implements Handler
    {
      private IStoreAccessor accessor = repository.getStore().getWriter(null);

      private OMMonitor monitor = new Monitor();

      private Object context;

      public void importPackageUnit(CDOPackageUnit packageUnit)
      {
        InternalCDOPackageUnit[] packageUnits = { (InternalCDOPackageUnit)packageUnit };
        context = accessor.rawStore(packageUnits, context, monitor);
      }

      public void importBranch(CDOBranch branch)
      {
      }

      public void importRevision(CDORevision revision)
      {
      }

      public void importCommit(CDOCommitInfo commitInfo)
      {
      }

      public void flush()
      {
        accessor.rawCommit(context, new Monitor());
        context = null;
      }
    }

    try
    {
      FlushHandler handler = new FlushHandler();
      importAll(in, handler);
      handler.flush();
    }
    finally
    {
      if (!wasActive)
      {
        LifecycleUtil.deactivate(repository);
      }

      repository = null;
    }
  }

  protected abstract void importAll(InputStream in, Handler handler) throws Exception;

  /**
   * @author Eike Stepper
   */
  public static interface Handler
  {
    public void importPackageUnit(CDOPackageUnit packageUnit);

    public void importBranch(CDOBranch branch);

    public void importRevision(CDORevision revision);

    public void importCommit(CDOCommitInfo commitInfo);

    public void flush();
  }

  /**
   * @author Eike Stepper
   */
  public static class XML extends CDOServerImporter implements CDOServerExporter.XMLConstants
  {
    public XML(IRepository repository)
    {
      super(repository);
    }

    protected final String str(CDOID id)
    {
      StringBuilder builder = new StringBuilder();
      CDOIDUtil.write(builder, id);
      return builder.toString();
    }

    @Override
    protected void importAll(InputStream in, Handler handler) throws Exception
    {
      DefaultHandler xmlHandler = new DefaultHandler()
      {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
          if (PACKAGE_UNIT.equals(qName))
          {
          }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
        }
      };

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(in, xmlHandler);
    }
  }
}
