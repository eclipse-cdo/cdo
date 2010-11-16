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
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.XMLOutput;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.OutputStream;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDOServerExporter<OUT>
{
  private InternalRepository repository;

  public CDOServerExporter(IRepository repository)
  {
    this.repository = (InternalRepository)repository;
  }

  public final IRepository getRepository()
  {
    return repository;
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
      OUT output = createOutput(out);
      exportAll(output);
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

  protected abstract OUT createOutput(OutputStream out) throws Exception;

  protected void exportAll(final OUT out) throws Exception
  {
    try
    {
      exportPackages(out);
      exportBranches(out);
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
    for (InternalCDOPackageUnit packageUnit : packageRegistry.getPackageUnits(false))
    {
      CDOPackageUnit.Type type = packageUnit.getOriginalType();
      long time = packageUnit.getTimeStamp();

      EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
      String data = new String(EMFUtil.getEPackageBytes(ePackage, false, packageRegistry));

      startPackageUnit(out, type, time, data);
      for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        String packageURI = packageInfo.getPackageURI();
        CDOIDMetaRange metaIDRange = packageInfo.getMetaIDRange();
        exportPackageInfo(out, packageURI, metaIDRange);
      }

      endPackageUnit(out);
    }
  }

  protected abstract void startPackageUnit(OUT out, CDOPackageUnit.Type type, long time, String data) throws Exception;

  protected abstract void endPackageUnit(OUT out) throws Exception;

  protected abstract void exportPackageInfo(OUT out, String packageURI, CDOIDMetaRange metaIDRange) throws Exception;

  protected void exportBranches(final OUT out) throws Exception
  {
    InternalCDOBranchManager branchManager = repository.getBranchManager();
    exportBranch(out, branchManager.getMainBranch());

    if (repository.isSupportingBranches())
    {
      branchManager.getBranches(0, 0, new CDOBranchHandler()
      {
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

  protected void exportBranch(OUT out, CDOBranch branch) throws Exception
  {
    exportRevisions(out, branch);
  }

  protected void exportRevisions(final OUT out, CDOBranch branch) throws Exception
  {
    repository.handleRevisions(null, branch, true, CDOBranchPoint.UNSPECIFIED_DATE, false, new CDORevisionHandler()
    {
      public boolean handleRevision(CDORevision revision)
      {
        try
        {
          exportRevision(out, revision);
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

  protected void exportCommits(final OUT out) throws Exception
  {
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    commitInfoManager.getCommitInfos(0, 0, new CDOCommitInfoHandler()
    {
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        try
        {
          exportCommit(out, commitInfo);
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
   */
  public static interface XMLConstants
  {
    public static final String REPOSITORY = "repository";

    public static final String REPOSITORY_NAME = "name";

    public static final String REPOSITORY_UUID = "uuid";

    public static final String MODELS = "models";

    public static final String PACKAGE_UNIT = "packageUnit";

    public static final String PACKAGE_UNIT_TYPE = "type";

    public static final String PACKAGE_UNIT_TIME = "time";

    public static final String PACKAGE_UNIT_DATA = "data";

    public static final String PACKAGE_INFO = "packageInfo";

    public static final String PACKAGE_INFO_URI = "uri";

    public static final String PACKAGE_INFO_FIRST = "first";

    public static final String PACKAGE_INFO_COUNT = "count";

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

    public static final String FEATURE = "feature";

    public static final String FEATURE_NAME = "name";

    public static final String FEATURE_TYPE = "name";

    public static final String FEATURE_VALUE = "value";

    public static final String COMMITS = "commits";

    public static final String COMMIT = "commit";

    public static final String COMMIT_TIME = "time";

    public static final String COMMIT_PREVIOUS = "previous";

    public static final String COMMIT_BRANCH = "branch";

    public static final String COMMIT_USER = "user";

    public static final String COMMIT_COMMENT = "comment";
  }

  /**
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
    protected void startPackageUnit(XMLOutput out, CDOPackageUnit.Type type, long time, String data) throws Exception
    {
      out.element(PACKAGE_UNIT);
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
    protected void exportPackageInfo(XMLOutput out, String uri, CDOIDMetaRange metaIDRange) throws Exception
    {
      out.element(PACKAGE_INFO);
      out.attribute(PACKAGE_INFO_URI, uri);
      out.attribute(PACKAGE_INFO_FIRST, str(metaIDRange.getLowerBound()));
      out.attribute(PACKAGE_INFO_COUNT, metaIDRange.size());
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

      out.push();
      CDOClassInfo classInfo = rev.getClassInfo();
      for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
      {
        if (feature.isMany())
        {
          @SuppressWarnings("unchecked")
          List<Object> list = (List<Object>)rev.getValue(feature);
          if (list != null)
          {
            for (Object value : list)
            {
              exportFeature(out, feature, value);
            }
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
      if (value instanceof CDOID)
      {
        out.attribute(FEATURE_TYPE, Object.class.getSimpleName());
        out.attribute(FEATURE_VALUE, str((CDOID)value));
      }
      else
      {
        out.attribute(FEATURE_TYPE, type(value));
        out.attributeOrNull(FEATURE_VALUE, value);
      }
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

      throw new IllegalArgumentException("Invalid type: " + value.getClass().getName());
    }
  }
}
