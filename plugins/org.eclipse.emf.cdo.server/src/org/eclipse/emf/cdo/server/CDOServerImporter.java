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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    InternalSession session = repository.getSessionManager().openSession(null);
    StoreThreadLocal.setSession(session);

    class FlushHandler implements Handler
    {
      private IStoreAccessor accessor = repository.getStore().getWriter(null);

      private OMMonitor monitor = new Monitor();

      private Object context;

      public void handlePackageUnits(InternalCDOPackageUnit[] packageUnits)
      {
        InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
        for (InternalCDOPackageUnit packageUnit : packageUnits)
        {
          packageRegistry.putPackageUnit(packageUnit);
        }

        context = accessor.rawStore(packageUnits, context, monitor);
      }

      public CDOBranch handleBranch(int id, String name, long time, int parentID)
      {
        InternalCDOBranchManager branchManager = repository.getBranchManager();
        InternalCDOBranch parent = branchManager.getBranch(parentID);
        return branchManager.createBranch(id, name, parent, time);
      }

      public boolean handleRevision(CDORevision revision)
      {
        context = accessor.rawStore((InternalCDORevision)revision, context, monitor);
        return true;
      }

      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
      }

      public void flush()
      {
        accessor.rawCommit(context, monitor);
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
      StoreThreadLocal.release();
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
  public static interface Handler extends CDORevisionHandler, CDOCommitInfoHandler
  {
    public void handlePackageUnits(InternalCDOPackageUnit[] packageUnits);

    public CDOBranch handleBranch(int id, String name, long time, int parentID);

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

    @Override
    protected void importAll(InputStream in, final Handler handler) throws Exception
    {
      DefaultHandler xmlHandler = new DefaultHandler()
      {
        private LinkedList<InternalCDOPackageUnit> packageUnits = new LinkedList<InternalCDOPackageUnit>();

        private List<InternalCDOPackageInfo> packageInfos;

        private InternalCDOBranch branch;

        private InternalCDORevision revision;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
          if (PACKAGE_UNIT.equals(qName))
          {
            InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
            packageUnit.setOriginalType(CDOPackageUnit.Type.valueOf(attributes.getValue(PACKAGE_UNIT_TYPE)));
            packageUnit.setTimeStamp(Long.parseLong(attributes.getValue(PACKAGE_UNIT_TIME)));
            // packageUnit.s
            packageUnit.setState(CDOPackageUnit.State.LOADED);

            packageUnits.add(packageUnit);
            packageInfos = new ArrayList<InternalCDOPackageInfo>();
          }
          else if (PACKAGE_INFO.equals(qName))
          {
            InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
            packageInfo.setPackageURI(attributes.getValue(PACKAGE_INFO_URI));
            CDOID lowerBound = id(attributes.getValue(PACKAGE_INFO_FIRST));
            int count = Integer.parseInt(attributes.getValue(PACKAGE_INFO_COUNT));
            CDOIDMetaRange metaIDRange = CDOIDUtil.createMetaRange(lowerBound, count);
            packageInfo.setMetaIDRange(metaIDRange);
            packageInfos.add(packageInfo);
          }
          else if (BRANCH.equals(qName))
          {
            int id = Integer.parseInt(attributes.getValue(BRANCH_ID));
            String name = attributes.getValue(BRANCH_NAME);
            long time = Long.parseLong(attributes.getValue(BRANCH_TIME));
            String parent = attributes.getValue(BRANCH_PARENT);
            int parentID = parent == null ? 0 : Integer.parseInt(parent);
            branch = (InternalCDOBranch)handler.handleBranch(id, name, time, parentID);
          }
          else if (REVISION.equals(qName))
          {
            CDOClassifierRef classifierRef = new CDOClassifierRef(attributes.getValue(REVISION_CLASS));
            EClass eClass = (EClass)classifierRef.resolve(getRepository().getPackageRegistry());
            revision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(eClass);
            revision.setID(id(attributes.getValue(REVISION_ID)));
            revision.setBranchPoint(branch.getPoint(Long.parseLong(attributes.getValue(REVISION_TIME))));
            revision.setVersion(Integer.parseInt(attributes.getValue(REVISION_VERSION)));
            String revised = attributes.getValue(REVISION_REVISED);
            if (revised != null)
            {
              revision.setRevised(Long.parseLong(revised));
            }
          }
          else if (FEATURE.equals(qName))
          {
            String name = attributes.getValue(FEATURE_NAME);
            String type = attributes.getValue(FEATURE_TYPE);
            String value = attributes.getValue(FEATURE_VALUE);

            EStructuralFeature feature = revision.getEClass().getEStructuralFeature(name);
            if (feature.isMany())
            {
              CDOList list = revision.getList(feature);
              if (value == null)
              {
                list.add(null);
              }
              else
              {
                list.add(value(type, value));
              }
            }
            else
            {
              if (value != null)
              {
                revision.setValue(feature, value(type, value));
              }
            }
          }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
          if (PACKAGE_UNIT.equals(qName))
          {
            InternalCDOPackageUnit packageUnit = packageUnits.getLast();
            packageUnit.setPackageInfos(packageInfos.toArray(new InternalCDOPackageInfo[packageInfos.size()]));
            packageInfos = null;
          }
          else if (MODELS.equals(qName))
          {
            handler.handlePackageUnits(packageUnits.toArray(new InternalCDOPackageUnit[packageUnits.size()]));
            packageUnits = null;
          }
          else if (BRANCH.equals(qName))
          {
            branch = null;
          }
          else if (REVISION.equals(qName))
          {
            handler.handleRevision(revision);
            revision = null;
          }
        }
      };

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(in, xmlHandler);
    }

    protected final CDOID id(String str)
    {
      return CDOIDUtil.read(str);
    }

    protected Object value(String type, String str)
    {
      if (Object.class.getSimpleName().equals(type))
      {
        return id(str);
      }

      if (Boolean.class.getSimpleName().equals(type))
      {
        return Boolean.valueOf(str);
      }

      if (Character.class.getSimpleName().equals(type))
      {
        return str.charAt(0);
      }

      if (Byte.class.getSimpleName().equals(type))
      {
        return Byte.valueOf(str);
      }

      if (Short.class.getSimpleName().equals(type))
      {
        return Short.valueOf(str);
      }

      if (Integer.class.getSimpleName().equals(type))
      {
        return Integer.valueOf(str);
      }

      if (Long.class.getSimpleName().equals(type))
      {
        return Long.valueOf(str);
      }

      if (Float.class.getSimpleName().equals(type))
      {
        return Float.valueOf(str);
      }

      if (Double.class.getSimpleName().equals(type))
      {
        return Double.valueOf(str);
      }

      if (String.class.getSimpleName().equals(type))
      {
        return str;
      }

      throw new IllegalArgumentException("Invalid type: " + type);
    }
  }
}
