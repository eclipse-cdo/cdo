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
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    LifecycleUtil.checkInactive(repository);
    this.repository.setSkipInitialization(true);

    LifecycleUtil.activate(repository);
    CheckUtil.checkState(repository.getStore().isFirstTime(), "Store has been used before");
  }

  protected final InternalRepository getRepository()
  {
    return repository;
  }

  public void importRepository(InputStream in) throws Exception
  {
    class FlushHandler implements Handler
    {
      private IStoreAccessor accessor = repository.getStore().getWriter(null);

      private OMMonitor monitor = new Monitor();

      private Object context;

      private Map<String, String> models = new HashMap<String, String>();

      private LinkedList<InternalCDOPackageUnit> packageUnits = new LinkedList<InternalCDOPackageUnit>();

      private List<InternalCDOPackageInfo> packageInfos;

      private InternalCDOPackageRegistry packageRegistry = getRepository().getPackageRegistry(false);

      public FlushHandler()
      {
      }

      public void handleRepository(String name, String uuid, CDOID root, long created, long committed)
      {
        // lastCommitTimeStamp = Math.max(store.getCreationTime(), store.getLastCommitTime());
        InternalCDOBranchManager branchManager = repository.getBranchManager();
        repository.initMainBranch(branchManager, created);
        LifecycleUtil.activate(branchManager);

        repository.initSystemPackages();
        repository.setRootResourceID(root);

        InternalSession session = repository.getSessionManager().openSession(null);
        StoreThreadLocal.setSession(session);
      }

      public InternalCDOPackageUnit handlePackageUnit(String id, Type type, long time, String data)
      {
        collectPackageInfos();

        InternalCDOPackageUnit packageUnit = packageRegistry.createPackageUnit();
        packageUnit.setOriginalType(type);
        packageUnit.setTimeStamp(time);

        models.put(id, data);
        packageUnits.add(packageUnit);
        packageInfos = new ArrayList<InternalCDOPackageInfo>();
        return packageUnit;
      }

      public InternalCDOPackageInfo handlePackageInfo(String packageURI, CDOIDMetaRange metaIDRange)
      {
        InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
        packageInfo.setPackageURI(packageURI);
        packageInfo.setMetaIDRange(metaIDRange);
        packageInfos.add(packageInfo);
        return packageInfo;
      }

      public InternalCDOPackageRegistry handleModels()
      {
        collectPackageInfos();
        InternalCDOPackageUnit[] array = packageUnits.toArray(new InternalCDOPackageUnit[packageUnits.size()]);
        packageUnits = null;

        PackageLoader loader = new PackageLoader()
        {
          private ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(packageRegistry);

          public EPackage[] loadPackages(CDOPackageUnit packageUnit)
          {
            String id = packageUnit.getID();
            String data = models.get(id);

            EPackage ePackage = EMFUtil.createEPackage(id, data.getBytes(), false, resourceSet, true);
            return EMFUtil.getAllPackages(ePackage);
          }
        };

        packageRegistry.putPackageUnits(array, CDOPackageUnit.State.PROXY);
        for (InternalCDOPackageUnit packageUnit : array)
        {
          packageUnit.load(loader);
        }

        context = accessor.rawStore(array, context, monitor);

        return packageRegistry;
      }

      public InternalCDOBranch handleBranch(int id, String name, long time, int parentID)
      {
        InternalCDOBranchManager branchManager = repository.getBranchManager();
        if (id == CDOBranch.MAIN_BRANCH_ID)
        {
          return branchManager.getMainBranch();
        }

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

      private void collectPackageInfos()
      {
        if (packageInfos != null)
        {
          InternalCDOPackageUnit packageUnit = packageUnits.getLast();
          packageUnit.setPackageInfos(packageInfos.toArray(new InternalCDOPackageInfo[packageInfos.size()]));
          packageInfos = null;
        }
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
      repository = null;
    }
  }

  protected abstract void importAll(InputStream in, Handler handler) throws Exception;

  /**
   * @author Eike Stepper
   */
  public static interface Handler extends CDORevisionHandler, CDOCommitInfoHandler
  {
    public void handleRepository(String name, String uuid, CDOID root, long created, long committed);

    public InternalCDOPackageUnit handlePackageUnit(String id, Type type, long time, String data);

    public InternalCDOPackageInfo handlePackageInfo(String packageURI, CDOIDMetaRange metaIDRange);

    public InternalCDOPackageRegistry handleModels();

    public InternalCDOBranch handleBranch(int id, String name, long time, int parentID);

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
        private InternalCDOPackageRegistry packageRegistry;

        private InternalCDOBranch branch;

        private InternalCDORevision revision;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
          if (REPOSITORY.equals(qName))
          {
            String name = attributes.getValue(REPOSITORY_NAME);
            String uuid = attributes.getValue(REPOSITORY_UUID);
            CDOID root = id(attributes.getValue(REPOSITORY_ROOT));
            long created = Long.parseLong(attributes.getValue(REPOSITORY_CREATED));
            long committed = Long.parseLong(attributes.getValue(REPOSITORY_COMMITTED));
            handler.handleRepository(name, uuid, root, created, committed);
          }
          else if (PACKAGE_UNIT.equals(qName))
          {
            String id = attributes.getValue(PACKAGE_UNIT_ID);
            Type type = CDOPackageUnit.Type.valueOf(attributes.getValue(PACKAGE_UNIT_TYPE));
            long time = Long.parseLong(attributes.getValue(PACKAGE_UNIT_TIME));
            String data = attributes.getValue(PACKAGE_UNIT_DATA);
            handler.handlePackageUnit(id, type, time, data);
          }
          else if (PACKAGE_INFO.equals(qName))
          {
            String packageURI = attributes.getValue(PACKAGE_INFO_URI);
            CDOID lowerBound = id(attributes.getValue(PACKAGE_INFO_FIRST));
            int count = Integer.parseInt(attributes.getValue(PACKAGE_INFO_COUNT));
            CDOIDMetaRange metaIDRange = CDOIDUtil.createMetaRange(lowerBound, count);
            handler.handlePackageInfo(packageURI, metaIDRange);
          }
          else if (BRANCH.equals(qName))
          {
            int id = Integer.parseInt(attributes.getValue(BRANCH_ID));
            String name = attributes.getValue(BRANCH_NAME);
            long time = Long.parseLong(attributes.getValue(BRANCH_TIME));
            String parent = attributes.getValue(BRANCH_PARENT);
            int parentID = parent == null ? 0 : Integer.parseInt(parent);
            branch = handler.handleBranch(id, name, time, parentID);
          }
          else if (REVISION.equals(qName))
          {
            CDOClassifierRef classifierRef = new CDOClassifierRef(attributes.getValue(REVISION_CLASS));
            EClass eClass = (EClass)classifierRef.resolve(packageRegistry);
            revision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(eClass);
            revision.setID(id(attributes.getValue(REVISION_ID)));
            revision.setBranchPoint(branch.getPoint(Long.parseLong(attributes.getValue(REVISION_TIME))));
            revision.setVersion(Integer.parseInt(attributes.getValue(REVISION_VERSION)));
            String revised = attributes.getValue(REVISION_REVISED);
            if (revised != null)
            {
              revision.setRevised(Long.parseLong(revised));
            }

            String resourceID = attributes.getValue(REVISION_RESOURCE);
            if (resourceID != null)
            {
              revision.setResourceID(id(resourceID));
            }

            String containerID = attributes.getValue(REVISION_CONTAINER);
            if (containerID != null)
            {
              revision.setContainerID(id(containerID));
            }

            String featureID = attributes.getValue(REVISION_FEATURE);
            if (featureID != null)
            {
              revision.setContainingFeatureID(Integer.parseInt(featureID));
            }
          }
          else if (FEATURE.equals(qName))
          {
            String name = attributes.getValue(FEATURE_NAME);
            Object value = value(attributes);

            EClass eClass = revision.getEClass();
            EStructuralFeature feature = eClass.getEStructuralFeature(name);
            if (feature == null)
            {
              throw new IllegalStateException("Feature " + name + " not found in class " + eClass.getName());
            }

            if (feature.isMany())
            {
              CDOList list = revision.getList(feature);
              list.add(value);
            }
            else
            {
              if (value != null)
              {
                revision.setValue(feature, value);
              }
            }
          }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
          if (MODELS.equals(qName))
          {
            packageRegistry = handler.handleModels();
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

    protected Object value(Attributes attributes)
    {
      String type = attributes.getValue(FEATURE_TYPE);

      if ("Blob".equals(type))
      {
        byte[] id = HexUtil.hexToBytes(attributes.getValue(FEATURE_ID));
        long size = Long.parseLong(attributes.getValue(FEATURE_SIZE));
        return CDOLobUtil.createBlob(id, size);
      }

      if ("Clob".equals(type))
      {
        byte[] id = HexUtil.hexToBytes(attributes.getValue(FEATURE_ID));
        long size = Long.parseLong(attributes.getValue(FEATURE_SIZE));
        return CDOLobUtil.createClob(id, size);
      }

      String str = attributes.getValue(FEATURE_VALUE);
      if (str == null)
      {
        return null;
      }

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

      if ("Blob".equals(type))
      {
        return str;
      }

      throw new IllegalArgumentException("Invalid type: " + type);
    }
  }
}
