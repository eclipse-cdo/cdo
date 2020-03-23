/*
 * Copyright (c) 2010-2012, 2014, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.ExtResourceSet;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.server.ServerDebugUtil;
import org.eclipse.emf.cdo.server.CDOServerExporter.Statistics;
import org.eclipse.emf.cdo.server.IStoreAccessor.Raw2;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.AsyncOutputStream;
import org.eclipse.net4j.util.io.AsyncWriter;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Imports the complete contents of a {@link IRepository repository} from the output created by a
 * {@link CDOServerExporter exporter} into a new repository.
 * <p>
 * Subtypes specify the actual exchange format.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDOServerImporter
{
  private static final boolean DEBUG_WITH_BROWSER = false;

  private InternalRepository repository;

  private String branchPath;

  private long timeStamp = CDOBranchPoint.INVALID_DATE;

  private final Statistics statistics = new Statistics();

  public CDOServerImporter(IRepository repository)
  {
    this.repository = (InternalRepository)repository;
    init();
  }

  /**
   * @since 4.8
   */
  public final String getBranchPath()
  {
    return branchPath;
  }

  /**
   * @since 4.8
   */
  protected final void setBranchPath(String branchPath)
  {
    this.branchPath = branchPath;
  }

  /**
   * @since 4.8
   */
  public final long getTimeStamp()
  {
    return timeStamp;
  }

  /**
   * @since 4.8
   */
  protected final void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  private void init()
  {
    LifecycleUtil.checkInactive(repository);
    repository.setSkipInitialization(true);
    repository.getStore().setDropAllDataOnActivate(true);
    LifecycleUtil.activate(repository);
  }

  protected final InternalRepository getRepository()
  {
    return repository;
  }

  /**
   * @since 4.8
   */
  public Statistics getStatistics()
  {
    return statistics;
  }

  public void importRepository(InputStream in) throws Exception
  {
    try
    {
      FlushHandler handler = new FlushHandler();
      importAll(in, handler);
      handler.flush();
    }
    finally
    {
      if (DEBUG_WITH_BROWSER)
      {
        ServerDebugUtil.removeAccessor(repository);
      }

      StoreThreadLocal.release();
      repository = null;
    }
  }

  protected abstract void importAll(InputStream in, Handler handler) throws Exception;

  /**
   * Persists the data that has been read by a {@link CDOServerImporter importer} into a new {@link IRepository
   * repository}.
   *
   * @author Eike Stepper
   */
  public static interface Handler extends CDORevisionHandler, CDOLobHandler
  {
    public void handleRepository(String name, String uuid, CDOID root, long created, long committed);

    public InternalCDOPackageUnit handlePackageUnit(String id, Type type, long time, String data);

    public InternalCDOPackageInfo handlePackageInfo(String packageURI);

    public InternalCDOPackageRegistry handleModels();

    public InternalCDOBranch handleBranch(int id, String name, long time, int parentID);

    public void handleCommitInfo(long time, long previous, int branch, String user, String comment);

    public void flush();
  }

  /**
   * Persists the data that has been read by a {@link CDOServerImporter importer} into a new {@link IRepository
   * repository}.
   *
   * @author Eike Stepper
   * @since 4.6
   */
  public static interface Handler2 extends Handler
  {
    public void handleCommitInfo(long time, long previous, int branch, String user, String comment, int mergeSourceBranchID, long mergeSourceTime);
  }

  /**
   * @author Eike Stepper
   */
  private final class FlushHandler implements Handler2
  {
    private OMMonitor monitor = new Monitor();

    private IStoreAccessor.Raw accessor;

    private Map<String, String> models = new HashMap<>();

    private LinkedList<InternalCDOPackageUnit> packageUnits = new LinkedList<>();

    private List<InternalCDOPackageInfo> packageInfos;

    private InternalCDOPackageRegistry packageRegistry = getRepository().getPackageRegistry(false);

    public FlushHandler()
    {
    }

    @Override
    public void handleRepository(String name, String uuid, CDOID root, long created, long committed)
    {
      repository.getStore().setCreationTime(created);
      repository.getStore().setLastCommitTime(committed);

      InternalCDOBranchManager branchManager = repository.getBranchManager();
      repository.initMainBranch(branchManager, created);
      LifecycleUtil.activate(branchManager);

      repository.initSystemPackages(true);
      repository.setRootResourceID(root);

      // InternalSession session = repository.getSessionManager().openSession(null);
      // StoreThreadLocal.setSession(session);

      accessor = (IStoreAccessor.Raw)repository.getStore().getWriter(null);
      StoreThreadLocal.setAccessor(accessor);

      if (DEBUG_WITH_BROWSER)
      {
        ServerDebugUtil.addAccessor(accessor);
      }
    }

    @Override
    public InternalCDOPackageUnit handlePackageUnit(String id, Type type, long time, String data)
    {
      ++statistics.packageUnits;
      collectPackageInfos();

      InternalCDOPackageUnit packageUnit = packageRegistry.createPackageUnit();
      packageUnit.setOriginalType(type);
      packageUnit.setTimeStamp(time);

      models.put(id, data);
      packageUnits.add(packageUnit);
      packageInfos = new ArrayList<>();
      return packageUnit;
    }

    @Override
    public InternalCDOPackageInfo handlePackageInfo(String packageURI)
    {
      ++statistics.packageInfos;

      InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
      packageInfo.setPackageURI(packageURI);
      packageInfos.add(packageInfo);
      return packageInfo;
    }

    @Override
    public InternalCDOPackageRegistry handleModels()
    {
      collectPackageInfos();
      InternalCDOPackageUnit[] array = packageUnits.toArray(new InternalCDOPackageUnit[packageUnits.size()]);
      packageUnits = null;

      final ExtResourceSet resourceSet = EMFUtil.createExtResourceSet(packageRegistry, false, false);

      PackageLoader loader = new PackageLoader()
      {
        @Override
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
        packageUnit.load(loader, false);
      }

      // Before we resolve, we configure the resourceSet to start delegating, which means
      // it will consult the packageRegistry for packages it didn't just load -- such as the Ecore
      // package
      resourceSet.setDelegating(true);
      EMFUtil.safeResolveAll(resourceSet);

      accessor.rawStore(array, monitor);

      return packageRegistry;
    }

    @Override
    public InternalCDOBranch handleBranch(int id, String name, long time, int parentID)
    {
      ++statistics.branches;

      InternalCDOBranchManager branchManager = repository.getBranchManager();
      if (id == CDOBranch.MAIN_BRANCH_ID)
      {
        return branchManager.getMainBranch();
      }

      InternalCDOBranch parent = branchManager.getBranch(parentID);
      return branchManager.createBranch(id, name, parent, time);
    }

    @Override
    public boolean handleRevision(CDORevision revision)
    {
      ++statistics.revisions;
      accessor.rawStore((InternalCDORevision)revision, monitor);
      return true;
    }

    @Override
    public OutputStream handleBlob(final byte[] id, final long size) throws IOException
    {
      ++statistics.blobs;
      return new AsyncOutputStream()
      {
        @Override
        protected void asyncWrite(InputStream in) throws IOException
        {
          accessor.rawStore(id, size, in);
        }
      };
    }

    @Override
    public Writer handleClob(final byte[] id, final long size) throws IOException
    {
      ++statistics.clobs;
      return new AsyncWriter()
      {
        @Override
        protected void asyncWrite(Reader in) throws IOException
        {
          accessor.rawStore(id, size, in);
        }
      };
    }

    @Override
    public void handleCommitInfo(long time, long previous, int branchID, String user, String comment)
    {
      handleCommitInfo(time, previous, branchID, user, comment, 0, CDOBranchPoint.UNSPECIFIED_DATE);
    }

    @Override
    public void handleCommitInfo(long time, long previous, int branchID, String user, String comment, int mergeSourceBranchID, long mergeSourceTime)
    {
      ++statistics.commits;

      CDOBranch branch = repository.getBranchManager().getBranch(branchID);
      if (mergeSourceBranchID != 0 && accessor instanceof Raw2)
      {
        CDOBranch mergeSourceBranch = repository.getBranchManager().getBranch(mergeSourceBranchID);
        CDOBranchPoint mergeSource = mergeSourceBranch.getPoint(mergeSourceTime);

        Raw2 raw2 = (Raw2)accessor;
        raw2.rawStore(branch, time, previous, user, comment, mergeSource, monitor);
      }
      else
      {
        accessor.rawStore(branch, time, previous, user, comment, monitor);
      }
    }

    @Override
    public void flush()
    {
      accessor.rawCommit(1.0, monitor);
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

  /**
   * An {@link CDOServerImporter importer} that reads and interprets XML output created by an
   * {@link CDOServerExporter.XML XML exporter}.
   *
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
      DefaultHandler xmlHandler = new XMLHandler(this, handler);

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(in, xmlHandler);
    }

    /**
     * @author Eike Stepper
     */
    private static final class XMLHandler extends DefaultHandler
    {
      private final XML xml;

      private final Handler handler;

      private InternalCDOPackageRegistry packageRegistry;

      private InternalCDOBranch branch;

      private InternalCDORevision revision;

      private Character blobChar;

      private OutputStream blob;

      private Writer clob;

      private XMLHandler(XML xml, Handler handler)
      {
        this.xml = xml;
        this.handler = handler;
      }

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

          String value = attributes.getValue(REPOSITORY_EXPORT_BRANCH);
          if (value != null)
          {
            xml.setBranchPath(value);
          }

          value = attributes.getValue(REPOSITORY_EXPORT_TIME);
          if (value != null)
          {
            xml.setTimeStamp(Long.parseLong(value));
          }

          handler.handleRepository(name, uuid, root, created, committed);
        }
        else if (PACKAGE_UNIT.equals(qName))
        {
          String id = attributes.getValue(PACKAGE_UNIT_ID);
          if (!CDOModelUtil.isSystemPackageURI(id))
          {
            Type type = CDOPackageUnit.Type.valueOf(attributes.getValue(PACKAGE_UNIT_TYPE));
            long time = Long.parseLong(attributes.getValue(PACKAGE_UNIT_TIME));
            String data = attributes.getValue(PACKAGE_UNIT_DATA);
            handler.handlePackageUnit(id, type, time, data);
          }
        }
        else if (PACKAGE_INFO.equals(qName))
        {
          String packageURI = attributes.getValue(PACKAGE_INFO_URI);
          if (!CDOModelUtil.isSystemPackageURI(packageURI))
          {
            handler.handlePackageInfo(packageURI);
          }
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

          CDOID id = id(attributes.getValue(REVISION_ID));
          int version = Integer.parseInt(attributes.getValue(REVISION_VERSION));
          long created = timeStamp(attributes.getValue(REVISION_TIME));
          long revised = timeStamp(attributes.getValue(REVISION_REVISED));
          boolean detached = "true".equals(attributes.getValue(REVISION_DETACHED));
          if (detached)
          {
            revision = new DetachedCDORevision(eClass, id, branch, version, created, revised);
          }
          else
          {
            revision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(eClass);
            revision.setID(id);
            revision.setBranchPoint(branch.getPoint(created));
            revision.setVersion(version);
            revision.setRevised(revised);

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
        }
        else if (FEATURE.equals(qName))
        {
          String name = attributes.getValue(FEATURE_NAME);
          EClass eClass = revision.getEClass();
          EStructuralFeature feature = eClass.getEStructuralFeature(name);
          if (feature == null)
          {
            throw new IllegalStateException("Feature " + name + " not found in class " + eClass.getName());
          }

          String isSetString = attributes.getValue(FEATURE_ISSET);
          if (isSetString != null)
          {
            // This must be an empty or an unset list.
            boolean isSet = Boolean.parseBoolean(isSetString);
            if (isSet)
            {
              // Create an empty list.
              revision.getOrCreateList(feature);
            }
            else
            {
              // Leave the list unset.
            }
          }
          else
          {
            Object value = value(attributes);

            if (feature.isMany())
            {
              if (value == CDORevisionData.NIL)
              {
                value = null;
              }

              CDOList list = revision.getOrCreateList(feature);
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
        else if (BLOB.equals(qName))
        {
          try
          {
            byte[] id = HexUtil.hexToBytes(attributes.getValue(LOB_ID));
            long size = Long.parseLong(attributes.getValue(LOB_SIZE));
            blob = handler.handleBlob(id, size);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
        else if (CLOB.equals(qName))
        {
          try
          {
            byte[] id = HexUtil.hexToBytes(attributes.getValue(LOB_ID));
            long size = Long.parseLong(attributes.getValue(LOB_SIZE));
            clob = handler.handleClob(id, size);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
        else if (COMMIT.equals(qName))
        {
          long time = Long.parseLong(attributes.getValue(COMMIT_TIME));

          String value = attributes.getValue(COMMIT_PREVIOUS);
          long previous = value == null ? CDOBranchPoint.UNSPECIFIED_DATE : Long.parseLong(value);

          value = attributes.getValue(COMMIT_BRANCH);
          int branch = value == null ? CDOBranch.MAIN_BRANCH_ID : Integer.parseInt(value);

          String user = attributes.getValue(COMMIT_USER);
          String comment = attributes.getValue(COMMIT_COMMENT);

          if (handler instanceof Handler2)
          {
            Handler2 handler2 = (Handler2)handler;

            value = attributes.getValue(MERGE_SOURCE_BRANCH);
            if (value != null)
            {
              int mergeSourceBranch = Integer.parseInt(value);
              long mergeSourceTime = Long.parseLong(attributes.getValue(MERGE_SOURCE_TIME));

              handler2.handleCommitInfo(time, previous, branch, user, comment, mergeSourceBranch, mergeSourceTime);
              return;
            }
          }

          handler.handleCommitInfo(time, previous, branch, user, comment);
        }
      }

      @Override
      public void characters(char[] ch, int start, int length) throws SAXException
      {
        if (blob != null)
        {
          try
          {
            if (blobChar != null)
            {
              char[] firstChars = { blobChar, ch[start] };
              blobChar = null;

              byte[] firstByte = HexUtil.hexToBytes(new String(firstChars));
              blob.write(firstByte, 0, 1);

              ++start;
              --length;
            }

            if ((length & 1) == 1) // odd length?
            {
              --length;
              blobChar = ch[length];
            }

            if (start != 0 || length != ch.length)
            {
              char[] tmp = new char[length];
              System.arraycopy(ch, start, tmp, 0, length);
              ch = tmp;
            }

            byte[] buf = HexUtil.hexToBytes(new String(ch));
            blob.write(buf);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
        else if (clob != null)
        {
          try
          {
            clob.write(ch, start, length);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
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
        else if (BLOB.equals(qName))
        {
          IOUtil.close(blob);
          blob = null;
        }
        else if (CLOB.equals(qName))
        {
          IOUtil.close(clob);
          clob = null;
        }
      }

      protected final CDOID id(String str)
      {
        return CDOIDUtil.read(str);
      }

      protected final long timeStamp(String str)
      {
        if (str == null)
        {
          return CDOBranchPoint.UNSPECIFIED_DATE;
        }

        return Long.parseLong(str);
      }

      protected Object value(Attributes attributes)
      {
        String type = attributes.getValue(FEATURE_TYPE);

        if (type == null)
        {
          String isNullString = attributes.getValue(FEATURE_ISNULL);
          if (isNullString != null)
          {
            // This must be an explicit single-valued null.
            boolean isNull = Boolean.parseBoolean(isNullString);
            if (isNull)
            {
              return CDORevisionData.NIL;
            }

            throw new IllegalArgumentException("Invalid attribute: isnull=false");
          }
        }

        if (TYPE_BLOB.equals(type))
        {
          byte[] id = HexUtil.hexToBytes(attributes.getValue(FEATURE_ID));
          long size = Long.parseLong(attributes.getValue(FEATURE_SIZE));
          return CDOLobUtil.createBlob(id, size);
        }

        if (TYPE_CLOB.equals(type))
        {
          byte[] id = HexUtil.hexToBytes(attributes.getValue(FEATURE_ID));
          long size = Long.parseLong(attributes.getValue(FEATURE_SIZE));
          return CDOLobUtil.createClob(id, size);
        }

        if (TYPE_FEATURE_MAP.equals(type))
        {
          String innerFeatureName = attributes.getValue(FEATURE_INNER_FEATURE);
          EStructuralFeature innerFeature = revision.getEClass().getEStructuralFeature(innerFeatureName);

          String innerType = attributes.getValue(FEATURE_INNER_TYPE);
          Object innerValue = value(attributes, innerType);

          return CDORevisionUtil.createFeatureMapEntry(innerFeature, innerValue);
        }

        return value(attributes, type);
      }

      protected Object value(Attributes attributes, String type)
      {
        String str = attributes.getValue(FEATURE_VALUE);
        if (str == null)
        {
          return null;
        }

        if (type == null || String.class.getSimpleName().equals(type))
        {
          return str;
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

        if (Date.class.getSimpleName().equals(type))
        {
          return new Date(Long.valueOf(str));
        }

        if (BigDecimal.class.getSimpleName().equals(type))
        {
          return new BigDecimal(str);
        }

        if (BigInteger.class.getSimpleName().equals(type))
        {
          return new BigInteger(str);
        }

        if (TYPE_BYTE_ARRAY.equals(type))
        {
          return HexUtil.hexToBytes(str);
        }

        throw new IllegalArgumentException("Invalid type: " + type);
      }
    }
  }

  /**
   * An {@link CDOServerImporter importer} that reads and interprets XML output created by an
   * {@link CDOServerExporter.XML XML exporter}.
   *
   * @author Eike Stepper
   * @since 4.8
   */
  public static class Binary extends CDOServerImporter implements CDOServerExporter.BinaryConstants
  {
    private InternalCDOPackageRegistry packageRegistry;

    public Binary(IRepository repository)
    {
      super(repository);
    }

    @Override
    protected void importAll(InputStream stream, Handler handler) throws Exception
    {
      CDODataInput in = createDataInput(stream);

      for (;;)
      {
        byte opcode = in.readByte();
        switch (opcode)
        {
        case REPOSITORY:
          handleRepository(in, handler);
          break;

        case PACKAGE_UNIT:
          handlePackageUnit(in, handler);
          break;

        case PACKAGE_INFO:
          handlePackageInfo(in, handler);
          break;

        case BRANCH:
          handleBranch(in, handler);
          break;

        case REVISION:
          handleRevision(in, handler);
          break;

        case BLOB:
          handleBlob(in, handler);
          break;

        case CLOB:
          handleClob(in, handler);
          break;

        case COMMIT:
          handleCommit(in, handler);
          break;

        case EOF:
          return;

        default:
          throw new IOException("Illegal opcode: " + opcode);
        }
      }
    }

    private CDODataInput createDataInput(InputStream stream)
    {
      return new CDODataInputImpl(new ExtendedDataInputStream(new BufferedInputStream(stream)))
      {
        @Override
        public CDOPackageRegistry getPackageRegistry()
        {
          return getRepository().getPackageRegistry(false);
        }

        @Override
        protected CDOBranchManager getBranchManager()
        {
          return getRepository().getBranchManager();
        }

        @Override
        protected CDOCommitInfoManager getCommitInfoManager()
        {
          return getRepository().getCommitInfoManager();
        }

        @Override
        protected CDORevisionFactory getRevisionFactory()
        {
          return getRepository().getRevisionManager().getFactory();
        }

        @Override
        protected CDOListFactory getListFactory()
        {
          return CDOListFactory.DEFAULT;
        }

        @Override
        protected CDOLobStore getLobStore()
        {
          return null; // Not used on server
        }

        @Override
        protected boolean isXCompression()
        {
          return true;
        }
      };
    }

    private void handleRepository(CDODataInput in, Handler handler) throws IOException
    {
      String name = in.readString();
      String uuid = in.readString();
      CDOID root = in.readCDOID();
      long created = in.readLong();
      long committed = in.readLong();

      setBranchPath(in.readString());
      setTimeStamp(in.readXLong());

      handler.handleRepository(name, uuid, root, created, committed);
    }

    private void handlePackageUnit(CDODataInput in, Handler handler) throws IOException
    {
      String id = in.readString();
      CDOPackageUnit.Type type = in.readEnum(CDOPackageUnit.Type.class);
      long time = in.readXLong();
      String data = in.readString();

      if (!CDOModelUtil.isSystemPackageURI(id))
      {
        handler.handlePackageUnit(id, type, time, data);
      }
    }

    private void handlePackageInfo(CDODataInput in, Handler handler) throws IOException
    {
      String packageURI = in.readString();
      if (!CDOModelUtil.isSystemPackageURI(packageURI))
      {
        handler.handlePackageInfo(packageURI);
      }
    }

    private void handleBranch(CDODataInput in, Handler handler) throws IOException
    {
      if (packageRegistry == null)
      {
        packageRegistry = handler.handleModels();
      }

      int id = in.readXInt();
      String name = in.readString();
      long time = in.readXLong();
      int parentID = in.readXInt();
      handler.handleBranch(id, name, time, parentID);
    }

    private void handleRevision(CDODataInput in, Handler handler) throws IOException
    {
      CDORevision revision;
      if (in.readBoolean())
      {
        CDOID id = in.readCDOID();
        CDOClassifierRef classifierRef = in.readCDOClassifierRef();
        EClass eClass = (EClass)classifierRef.resolve(packageRegistry);
        CDOBranch branch = in.readCDOBranch();
        int version = in.readXInt();
        long created = in.readXLong();
        long revised = in.readXLong();
        revision = new DetachedCDORevision(eClass, id, branch, version, created, revised);
      }
      else
      {
        revision = in.readCDORevision();
      }

      handler.handleRevision(revision);
    }

    private void handleBlob(CDODataInput in, Handler handler) throws IOException
    {
      byte[] id = in.readByteArray();
      long size = in.readXLong();
      OutputStream blob = handler.handleBlob(id, size);

      // TODO Use IOUtil.copyBinary(in, blob, size).
      try
      {
        while (--size >= 0L)
        {
          byte b = in.readByte();
          blob.write(b);
        }
      }
      finally
      {
        IOUtil.close(blob);
      }
    }

    private void handleClob(CDODataInput in, Handler handler) throws IOException
    {
      byte[] id = in.readByteArray();
      long size = in.readXLong();
      Writer clob = handler.handleClob(id, size);

      // TODO Use IOUtil.copyCharacter(in, clob, size).
      try
      {
        while (--size >= 0L)
        {
          char c = in.readChar();
          clob.write(c);
        }
      }
      finally
      {
        IOUtil.close(clob);
      }
    }

    private void handleCommit(CDODataInput in, Handler handler) throws IOException
    {
      long time = in.readXLong();
      long previous = in.readXLong();
      int branch = in.readXInt();
      String user = in.readString();
      String comment = in.readString();

      if (handler instanceof Handler2)
      {
        Handler2 handler2 = (Handler2)handler;

        if (in.readBoolean())
        {
          int mergeSourceBranch = in.readXInt();
          long mergeSourceTime = in.readXLong();

          handler2.handleCommitInfo(time, previous, branch, user, comment, mergeSourceBranch, mergeSourceTime);
          return;
        }
      }

      handler.handleCommitInfo(time, previous, branch, user, comment);
    }
  }
}
