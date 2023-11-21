/*
 * Copyright (c) 2010-2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.XMLOutput;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.xml.sax.SAXException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Exports the complete contents of a {@link IRepository repository} in a format suitable for {@link CDOServerImporter
 * imports} into new repositories.
 * <p>
 * Subtypes specify the actual exchange format.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDOServerExporter<OUT>
{
  InternalRepository repository;

  private boolean exportSystemPackages;

  private String branchPath;

  private long timeStamp = CDOBranchPoint.INVALID_DATE;

  private final Statistics statistics = new Statistics();

  public CDOServerExporter(IRepository repository)
  {
    this.repository = (InternalRepository)repository;
  }

  public final IRepository getRepository()
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

  /**
   * @since 4.7
   */
  public boolean isExportSystemPackages()
  {
    return exportSystemPackages;
  }

  /**
   * @since 4.7
   */
  public void setExportSystemPackages(boolean exportSystemPackages)
  {
    this.exportSystemPackages = exportSystemPackages;
  }

  /**
   * @since 4.8
   */
  public String getBranchPath()
  {
    return branchPath;
  }

  /**
   * @since 4.8
   */
  public void setBranchPath(String branchPath)
  {
    this.branchPath = branchPath;
  }

  /**
   * @since 4.8
   */
  public long getTimeStamp()
  {
    return timeStamp;
  }

  /**
   * @since 4.8
   */
  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public final void exportRepository(OutputStream out) throws Exception
  {
    boolean wasActive = LifecycleUtil.isActive(repository);
    if (!wasActive)
    {
      LifecycleUtil.activate(repository);
    }

    InternalSession session = repository.getSessionManager().openSession(null);
    StoreThreadLocal.setSession(session);

    try
    {
      if (!(out instanceof BufferedOutputStream))
      {
        out = new BufferedOutputStream(out);
      }

      OUT output = createOutput(out);
      exportAll(output);
      out.flush();
    }
    finally
    {
      StoreThreadLocal.release();

      if (!wasActive)
      {
        LifecycleUtil.deactivate(repository);
      }
      else
      {
        LifecycleUtil.deactivate(session);
      }

      repository = null;
    }
  }

  protected abstract OUT createOutput(OutputStream out) throws Exception;

  protected void exportAll(final OUT out) throws Exception
  {
    try
    {
      exportPackages(out);
      exportBranches(out);
      exportLobs(out);
      exportCommits(out);
    }
    catch (WrappedException ex)
    {
      throw WrappedException.unwrap(ex);
    }
  }

  protected void exportPackages(OUT out) throws Exception
  {
    InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
    InternalCDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits(true);
    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      if (packageUnit.isSystem() && !exportSystemPackages)
      {
        continue;
      }

      String id = packageUnit.getID();
      CDOPackageUnit.Type type = packageUnit.getOriginalType();
      long time = packageUnit.getTimeStamp();

      EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
      String data = new String(EMFUtil.getEPackageBytes(ePackage, false, packageRegistry));

      startPackageUnit(out, id, type, time, data);
      for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        String packageURI = packageInfo.getPackageURI();
        exportPackageInfo(out, packageURI);
        ++statistics.packageInfos;
      }

      endPackageUnit(out);
      ++statistics.packageUnits;
    }
  }

  protected abstract void startPackageUnit(OUT out, String id, CDOPackageUnit.Type type, long time, String data) throws Exception;

  protected abstract void endPackageUnit(OUT out) throws Exception;

  protected abstract void exportPackageInfo(OUT out, String packageURI) throws Exception;

  protected void exportBranches(final OUT out) throws Exception
  {
    InternalCDOBranchManager branchManager = repository.getBranchManager();

    if (branchPath != null)
    {
      InternalCDOBranch branch = branchManager.getBranch(branchPath);
      if (branch == null)
      {
        throw new IllegalStateException("Branch '" + branchPath + "' does not exist");
      }

      exportBranch(out, branch);
    }
    else
    {
      exportBranch(out, branchManager.getMainBranch());

      if (repository.isSupportingBranches())
      {
        branchManager.getBranches(0, 0, new CDOBranchHandler()
        {
          @Override
          public void handleBranch(CDOBranch branch)
          {
            try
            {
              exportBranch(out, branch);
            }
            catch (Exception ex)
            {
              throw WrappedException.wrap(ex);
            }
          }
        });
      }
    }
  }

  protected void exportBranch(OUT out, CDOBranch branch) throws Exception
  {
    exportRevisions(out, branch);
    ++statistics.branches;
  }

  protected void exportRevisions(final OUT out, CDOBranch branch) throws Exception
  {
    repository.handleRevisions(null, branch, true, timeStamp, false, new CDORevisionHandler()
    {
      @Override
      public boolean handleRevision(CDORevision revision)
      {
        try
        {
          exportRevision(out, revision);
          ++statistics.revisions;
          return true;
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });
  }

  protected abstract void exportRevision(OUT out, CDORevision revision) throws Exception;

  protected void exportLobs(final OUT out) throws Exception
  {
    repository.handleLobs(0, 0, new CDOLobHandler()
    {
      @Override
      public OutputStream handleBlob(byte[] id, long size)
      {
        try
        {
          ++statistics.blobs;
          return startBlob(out, id, size);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      @Override
      public Writer handleClob(byte[] id, long size)
      {
        try
        {
          ++statistics.clobs;
          return startClob(out, id, size);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });
  }

  protected abstract OutputStream startBlob(OUT out, byte[] id, long size) throws Exception;

  protected abstract Writer startClob(OUT out, byte[] id, long size) throws Exception;

  protected void exportCommits(final OUT out) throws Exception
  {
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    commitInfoManager.getCommitInfos(null, 0L, 0L, new CDOCommitInfoHandler()
    {
      @Override
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        try
        {
          exportCommit(out, commitInfo);
          ++statistics.commits;
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });
  }

  protected abstract void exportCommit(OUT out, CDOCommitInfo commitInfo) throws Exception;

  /**
   * @author Eike Stepper
   * @since 4.8
   */
  public static final class Statistics
  {
    public long packageUnits;

    public long packageInfos;

    public long branches;

    public long revisions;

    public long blobs;

    public long clobs;

    public long commits;

    /**
     * @deprecated As of 4.9 use {@link #dumpStrings(Consumer)}.
     */
    @Deprecated
    public void dump(org.eclipse.net4j.util.Handler<String> handler)
    {
      dumpStrings((Consumer<String>)string -> handler.handle(string));
    }

    /**
     * @since 4.9
     */
    public void dumpStrings(Consumer<String> consumer)
    {
      consumer.accept("Package Units: " + packageUnits);
      consumer.accept("Package Infos: " + packageInfos);
      consumer.accept("Branches:      " + branches);
      consumer.accept("Revisions:     " + revisions);
      consumer.accept("Blobs:         " + blobs);
      consumer.accept("Clobs:         " + clobs);
      consumer.accept("Commits:       " + commits);
    }
  }

  /**
   * XML constants being used by both {@link CDOServerExporter exporters} and {@link CDOServerImporter importers}.
   *
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @author Eike Stepper
   */
  public static interface XMLConstants
  {
    public static final String REPOSITORY = "repository";

    public static final String REPOSITORY_NAME = "name";

    public static final String REPOSITORY_UUID = "uuid";

    public static final String REPOSITORY_ROOT = "root";

    public static final String REPOSITORY_CREATED = "created";

    public static final String REPOSITORY_COMMITTED = "committed";

    /**
     * @since 4.8
     */
    public static final String REPOSITORY_EXPORT_BRANCH = "exportBranch";

    /**
     * @since 4.8
     */
    public static final String REPOSITORY_EXPORT_TIME = "exportTime";

    public static final String MODELS = "models";

    public static final String PACKAGE_UNIT = "packageUnit";

    public static final String PACKAGE_UNIT_ID = "id";

    public static final String PACKAGE_UNIT_TYPE = "type";

    public static final String PACKAGE_UNIT_TIME = "time";

    public static final String PACKAGE_UNIT_DATA = "data";

    public static final String PACKAGE_INFO = "packageInfo";

    public static final String PACKAGE_INFO_URI = "uri";

    public static final String INSTANCES = "instances";

    public static final String BRANCH = "branch";

    public static final String BRANCH_ID = "id";

    public static final String BRANCH_NAME = "name";

    public static final String BRANCH_TIME = "time";

    public static final String BRANCH_PARENT = "parent";

    public static final String REVISION = "revision";

    public static final String REVISION_ID = "id";

    public static final String REVISION_CLASS = "class";

    public static final String REVISION_VERSION = "version";

    public static final String REVISION_TIME = "time";

    public static final String REVISION_REVISED = "revised";

    /**
     * @since 4.7
     */
    public static final String REVISION_DETACHED = "detached";

    public static final String REVISION_RESOURCE = "resource";

    public static final String REVISION_CONTAINER = "container";

    public static final String REVISION_FEATURE = "feature";

    public static final String FEATURE = "feature";

    public static final String FEATURE_NAME = "name";

    public static final String FEATURE_TYPE = "type";

    public static final String FEATURE_INNER_FEATURE = "innerFeature";

    public static final String FEATURE_INNER_TYPE = "innerType";

    public static final String FEATURE_VALUE = "value";

    /**
     * @since 4.7
     */
    public static final String FEATURE_ISSET = "isset";

    /**
     * @since 4.7
     */
    public static final String FEATURE_ISNULL = "isnull";

    public static final String FEATURE_ID = "id";

    public static final String FEATURE_SIZE = "size";

    public static final String TYPE_BLOB = "Blob";

    public static final String TYPE_CLOB = "Clob";

    /**
     * @since 4.7
     */
    public static final String TYPE_BYTE_ARRAY = "ByteArray";

    /**
     * @deprecated As of 4.5 {@link org.eclipse.emf.ecore.util.FeatureMap feature maps} are no longer supported.
     */
    @Deprecated
    public static final String TYPE_FEATURE_MAP = null;

    public static final String LOBS = "lobs";

    public static final String LOB_ID = "id";

    public static final String LOB_SIZE = "size";

    public static final String BLOB = "blob";

    public static final String CLOB = "clob";

    public static final String COMMITS = "commits";

    public static final String COMMIT = "commit";

    public static final String COMMIT_TIME = "time";

    public static final String COMMIT_PREVIOUS = "previous";

    public static final String COMMIT_BRANCH = "branch";

    public static final String COMMIT_USER = "user";

    public static final String COMMIT_COMMENT = "comment";

    /**
     * @since 4.6
     */
    public static final String MERGE_SOURCE_BRANCH = "mergeSourceBranch";

    /**
     * @since 4.6
     */
    public static final String MERGE_SOURCE_TIME = "mergeSourceTime";
  }

  /**
   * An {@link CDOServerExporter exporter} that creates XML output suitable to be interpreted by an
   * {@link CDOServerImporter.XML XML importer}.
   *
   * @author Eike Stepper
   */
  public static class XML extends CDOServerExporter<XMLOutput> implements XMLConstants
  {
    public XML(IRepository repository)
    {
      super(repository);
    }

    @Override
    protected final XMLOutput createOutput(OutputStream out) throws Exception
    {
      return new XMLOutput(out);
    }

    @Override
    protected void exportAll(XMLOutput out) throws Exception
    {
      out.element(REPOSITORY);
      out.attribute(REPOSITORY_NAME, getRepository().getName());
      out.attribute(REPOSITORY_UUID, getRepository().getUUID());
      out.attribute(REPOSITORY_ROOT, str(getRepository().getRootResourceID()));
      out.attribute(REPOSITORY_CREATED, getRepository().getStore().getCreationTime());
      out.attribute(REPOSITORY_COMMITTED, getRepository().getLastCommitTimeStamp());

      String branchPath = getBranchPath();
      if (branchPath != null)
      {
        out.attribute(REPOSITORY_EXPORT_BRANCH, branchPath);
      }

      long timeStamp = getTimeStamp();
      if (timeStamp != CDOBranchPoint.INVALID_DATE)
      {
        out.attribute(REPOSITORY_EXPORT_TIME, timeStamp);
      }

      out.push();
      super.exportAll(out);
      out.done();
    }

    @Override
    protected void exportPackages(XMLOutput out) throws Exception
    {
      out.element(MODELS);

      out.push();
      super.exportPackages(out);
      out.pop();
    }

    @Override
    protected void startPackageUnit(XMLOutput out, String id, CDOPackageUnit.Type type, long time, String data) throws Exception
    {
      out.element(PACKAGE_UNIT);
      out.attribute(PACKAGE_UNIT_ID, id);
      out.attribute(PACKAGE_UNIT_TYPE, type);
      out.attribute(PACKAGE_UNIT_TIME, time);
      out.attribute(PACKAGE_UNIT_DATA, data);
      out.push();
    }

    @Override
    protected void endPackageUnit(XMLOutput out) throws Exception
    {
      out.pop();
    }

    @Override
    protected void exportPackageInfo(XMLOutput out, String uri) throws Exception
    {
      out.element(PACKAGE_INFO);
      out.attribute(PACKAGE_INFO_URI, uri);
    }

    @Override
    protected void exportBranches(XMLOutput out) throws Exception
    {
      out.element(INSTANCES);

      out.push();
      super.exportBranches(out);
      out.pop();
    }

    @Override
    protected void exportBranch(XMLOutput out, CDOBranch branch) throws Exception
    {
      out.element(BRANCH);
      out.attribute(BRANCH_ID, branch.getID());
      out.attribute(BRANCH_NAME, branch.getName());
      out.attribute(BRANCH_TIME, branch.getBase().getTimeStamp());
      if (!branch.isMainBranch())
      {
        out.attribute(BRANCH_PARENT, branch.getBase().getBranch().getID());
      }

      out.push();
      super.exportBranch(out, branch);
      out.pop();
    }

    @Override
    protected void exportRevision(XMLOutput out, CDORevision revision) throws Exception
    {
      InternalCDORevision rev = (InternalCDORevision)revision;
      boolean detached = rev instanceof DetachedCDORevision;

      out.element(REVISION);
      out.attribute(REVISION_ID, str(rev.getID()));
      out.attribute(REVISION_CLASS, new CDOClassifierRef(rev.getEClass()).getURI());
      out.attribute(REVISION_VERSION, rev.getVersion());
      out.attribute(REVISION_TIME, rev.getTimeStamp());

      long revised = rev.getRevised();
      if (revised != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        out.attribute(REVISION_REVISED, revised);
      }

      if (detached)
      {
        out.attribute(REVISION_DETACHED, true);
        return;
      }

      CDOID resourceID = rev.getResourceID();
      if (!CDOIDUtil.isNull(resourceID))
      {
        out.attribute(REVISION_RESOURCE, str(resourceID));
      }

      CDOID containerID = (CDOID)rev.getContainerID();
      if (!CDOIDUtil.isNull(containerID))
      {
        out.attribute(REVISION_CONTAINER, str(containerID));
      }

      int containingFeatureID = rev.getContainingFeatureID();
      if (containingFeatureID != 0)
      {
        out.attribute(REVISION_FEATURE, containingFeatureID);
      }

      out.push();

      InternalRepository repository = (InternalRepository)getRepository();
      repository.ensureChunks(rev, CDORevision.UNCHUNKED);

      CDOClassInfo classInfo = rev.getClassInfo();
      for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
      {
        if (feature.isMany())
        {
          @SuppressWarnings("unchecked")
          List<Object> list = (List<Object>)rev.getValue(feature);
          if (list != null && !list.isEmpty())
          {
            for (Object value : list)
            {
              exportFeature(out, feature, value);
            }
          }
          else if (feature.isUnsettable())
          {
            out.element(FEATURE);
            out.attribute(FEATURE_NAME, feature.getName());
            out.attribute(FEATURE_ISSET, list != null);
          }
        }
        else
        {
          Object value = rev.getValue(feature);
          if (value != null)
          {
            exportFeature(out, feature, value);
          }
        }
      }

      out.pop();
    }

    protected void exportFeature(XMLOutput out, EStructuralFeature feature, Object value) throws Exception
    {
      out.element(FEATURE);
      out.attribute(FEATURE_NAME, feature.getName());
      exportFeature(out, feature, FEATURE_TYPE, value);
    }

    protected void exportFeature(XMLOutput out, EStructuralFeature feature, String featureType, Object value) throws SAXException
    {
      if (value instanceof CDOID)
      {
        out.attribute(featureType, Object.class.getSimpleName());
        out.attribute(FEATURE_VALUE, str((CDOID)value));
      }
      else if (value instanceof CDOBlob)
      {
        CDOBlob blob = (CDOBlob)value;
        out.attribute(featureType, TYPE_BLOB);
        out.attribute(FEATURE_ID, HexUtil.bytesToHex(blob.getID()));
        out.attribute(FEATURE_SIZE, blob.getSize());
      }
      else if (value instanceof CDOClob)
      {
        CDOClob clob = (CDOClob)value;
        out.attribute(featureType, TYPE_CLOB);
        out.attribute(FEATURE_ID, HexUtil.bytesToHex(clob.getID()));
        out.attribute(FEATURE_SIZE, clob.getSize());
      }
      else if (value instanceof Date)
      {
        Date date = (Date)value;
        out.attribute(featureType, Date.class.getSimpleName());
        out.attribute(FEATURE_VALUE, date.getTime());
      }
      else if (value instanceof byte[])
      {
        byte[] array = (byte[])value;
        out.attribute(featureType, TYPE_BYTE_ARRAY);
        out.attribute(FEATURE_VALUE, HexUtil.bytesToHex(array));
      }
      else if (value == null || value == CDORevisionData.NIL)
      {
        out.attribute(FEATURE_ISNULL, true);
      }
      else
      {
        if (!(value instanceof String))
        {
          out.attribute(featureType, type(value));
        }

        out.attributeOrNull(FEATURE_VALUE, value);
      }
    }

    @Override
    protected void exportLobs(XMLOutput out) throws Exception
    {
      out.element(LOBS);

      out.push();
      super.exportLobs(out);
      out.pop();
    }

    @Override
    protected OutputStream startBlob(XMLOutput out, byte[] id, long size) throws Exception
    {
      out.element(BLOB);
      out.attribute(LOB_ID, HexUtil.bytesToHex(id));
      out.attribute(LOB_SIZE, size);
      return out.bytes();
    }

    @Override
    protected Writer startClob(XMLOutput out, byte[] id, long size) throws Exception
    {
      out.element(CLOB);
      out.attribute(LOB_ID, HexUtil.bytesToHex(id));
      out.attribute(LOB_SIZE, size);
      return out.characters();
    }

    @Override
    protected void exportCommits(XMLOutput out) throws Exception
    {
      out.element(COMMITS);

      out.push();
      super.exportCommits(out);
      out.pop();
    }

    @Override
    protected void exportCommit(XMLOutput out, CDOCommitInfo commitInfo) throws Exception
    {
      out.element(COMMIT);
      out.attribute(COMMIT_TIME, commitInfo.getTimeStamp());
      long previous = commitInfo.getPreviousTimeStamp();
      if (previous != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        out.attribute(COMMIT_PREVIOUS, previous);
      }

      int branch = commitInfo.getBranch().getID();
      if (branch != CDOBranch.MAIN_BRANCH_ID)
      {
        out.attribute(COMMIT_BRANCH, branch);
      }

      out.attribute(COMMIT_USER, commitInfo.getUserID());
      out.attribute(COMMIT_COMMENT, commitInfo.getComment());

      CDOBranchPoint mergeSource = commitInfo.getMergeSource();
      if (mergeSource != null)
      {
        out.attribute(MERGE_SOURCE_BRANCH, mergeSource.getBranch().getID());
        out.attribute(MERGE_SOURCE_TIME, mergeSource.getTimeStamp());
      }
    }

    protected final String str(CDOID id)
    {
      StringBuilder builder = new StringBuilder();
      CDOIDUtil.write(builder, id);
      return builder.toString();
    }

    protected String type(Object value)
    {
      if (value instanceof Boolean)
      {
        return Boolean.class.getSimpleName();
      }

      if (value instanceof Character)
      {
        return Character.class.getSimpleName();
      }

      if (value instanceof Byte)
      {
        return Byte.class.getSimpleName();
      }

      if (value instanceof Short)
      {
        return Short.class.getSimpleName();
      }

      if (value instanceof Integer)
      {
        return Integer.class.getSimpleName();
      }

      if (value instanceof Long)
      {
        return Long.class.getSimpleName();
      }

      if (value instanceof Float)
      {
        return Float.class.getSimpleName();
      }

      if (value instanceof Double)
      {
        return Double.class.getSimpleName();
      }

      if (value instanceof String)
      {
        return String.class.getSimpleName();
      }

      if (value instanceof BigDecimal)
      {
        return BigDecimal.class.getSimpleName();
      }

      if (value instanceof BigInteger)
      {
        return BigInteger.class.getSimpleName();
      }

      throw new IllegalArgumentException("Invalid type: " + value.getClass().getName());
    }
  }

  /**
   * Binary constants being used by both {@link CDOServerExporter exporters} and {@link CDOServerImporter importers}.
   *
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @author Eike Stepper
   * @since 4.8
   */
  public static interface BinaryConstants
  {
    public static final byte REPOSITORY = 0;

    public static final byte PACKAGE_UNIT = 1;

    public static final byte PACKAGE_INFO = 2;

    public static final byte BRANCH = 3;

    public static final byte REVISION = 4;

    public static final byte BLOB = 5;

    public static final byte CLOB = 6;

    public static final byte COMMIT = 7;

    public static final byte EOF = -1;
  }

  /**
   * @author Eike Stepper
   * @since 4.8
   */
  public static class Binary extends CDOServerExporter<CDODataOutput> implements BinaryConstants
  {
    public Binary(IRepository repository)
    {
      super(repository);
    }

    @Override
    protected CDODataOutput createOutput(OutputStream out) throws Exception
    {
      return new CDODataOutputImpl(new ExtendedDataOutputStream(out))
      {
        @Override
        public CDOPackageRegistry getPackageRegistry()
        {
          return ((InternalRepository)getRepository()).getPackageRegistry(false);
        }

        @Override
        public CDOIDProvider getIDProvider()
        {
          return CDOIDProvider.NOOP;
        }

        @Override
        public CDOPermissionProvider getPermissionProvider()
        {
          return CDOPermissionProvider.WRITE;
        }

        @Override
        protected boolean isXCompression()
        {
          return true;
        }
      };
    }

    @Override
    protected void exportAll(CDODataOutput out) throws Exception
    {
      out.writeByte(REPOSITORY);
      out.writeString(getRepository().getName());
      out.writeString(getRepository().getUUID());
      out.writeCDOID(getRepository().getRootResourceID());
      out.writeLong(getRepository().getStore().getCreationTime());
      out.writeLong(getRepository().getLastCommitTimeStamp());
      out.writeString(getBranchPath());
      out.writeXLong(getTimeStamp());

      super.exportAll(out);

      out.writeByte(EOF);
    }

    @Override
    protected void startPackageUnit(CDODataOutput out, String id, Type type, long time, String data) throws Exception
    {
      out.writeByte(PACKAGE_UNIT);
      out.writeString(id);
      out.writeEnum(type);
      out.writeXLong(time);
      out.writeString(data);
    }

    @Override
    protected void endPackageUnit(CDODataOutput out) throws Exception
    {
      // Do nothing.
    }

    @Override
    protected void exportPackageInfo(CDODataOutput out, String packageURI) throws Exception
    {
      out.writeByte(PACKAGE_INFO);
      out.writeString(packageURI);
    }

    @Override
    protected void exportBranch(CDODataOutput out, CDOBranch branch) throws Exception
    {
      out.writeByte(BRANCH);
      out.writeXInt(branch.getID());
      out.writeString(branch.getName());
      out.writeXLong(branch.getBase().getTimeStamp());
      if (branch.isMainBranch())
      {
        out.writeXInt(CDOBranch.MAIN_BRANCH_ID);
      }
      else
      {
        out.writeXInt(branch.getBase().getBranch().getID());
      }

      super.exportBranch(out, branch);
    }

    @Override
    protected void exportRevision(CDODataOutput out, CDORevision revision) throws Exception
    {
      out.writeByte(REVISION);
      if (revision instanceof DetachedCDORevision)
      {
        out.writeBoolean(true);
        out.writeCDOID(revision.getID());
        out.writeCDOClassifierRef(revision.getEClass());
        out.writeCDOBranch(revision.getBranch());
        out.writeXInt(revision.getVersion());
        out.writeXLong(revision.getTimeStamp());
        out.writeXLong(revision.getRevised());
      }
      else
      {
        out.writeBoolean(false);
        out.writeCDORevision(revision, CDORevision.UNCHUNKED);
      }
    }

    @Override
    protected OutputStream startBlob(final CDODataOutput out, byte[] id, long size) throws Exception
    {
      out.writeByte(BLOB);
      out.writeByteArray(id);
      out.writeXLong(size);

      return new OutputStream()
      {
        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
          out.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException
        {
          out.write(b);
        }

        @Override
        public void close() throws IOException
        {
          // Do nothing.
        }
      };
    }

    @Override
    protected Writer startClob(final CDODataOutput out, byte[] id, long size) throws Exception
    {
      out.writeByte(CLOB);
      out.writeByteArray(id);
      out.writeXLong(size);

      return new Writer()
      {
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
          for (int i = off; i < len; i++)
          {
            char c = cbuf[i];
            out.writeChar(c);
          }
        }

        @Override
        public void close() throws IOException
        {
          // Do nothing.
        }

        @Override
        public void flush() throws IOException
        {
          // Do nothing.
        }
      };
    }

    @Override
    protected void exportCommit(CDODataOutput out, CDOCommitInfo commitInfo) throws Exception
    {
      out.writeByte(COMMIT);
      out.writeXLong(commitInfo.getTimeStamp());
      out.writeXLong(commitInfo.getPreviousTimeStamp());
      out.writeXInt(commitInfo.getBranch().getID());
      out.writeString(commitInfo.getUserID());
      out.writeString(commitInfo.getComment());

      CDOBranchPoint mergeSource = commitInfo.getMergeSource();
      if (mergeSource != null)
      {
        out.writeBoolean(true);
        out.writeXInt(mergeSource.getBranch().getID());
        out.writeXLong(mergeSource.getTimeStamp());
      }
      else
      {
        out.writeBoolean(false);
      }
    }
  }
}
