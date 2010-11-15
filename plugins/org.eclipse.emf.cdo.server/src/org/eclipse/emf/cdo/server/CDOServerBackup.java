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
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
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
import org.eclipse.net4j.util.XMLStack;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDOServerBackup<IN, OUT>
{
  private InternalRepository repository;

  public CDOServerBackup(IRepository repository)
  {
    this.repository = (InternalRepository)repository;
  }

  public final IRepository getRepository()
  {
    return repository;
  }

  public final void export(OutputStream out) throws Exception
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
      exportRepository(output);
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

  protected void exportRepository(final OUT out) throws Exception
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
      Type type = packageUnit.getType();
      long time = packageUnit.getTimeStamp();

      EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
      String data = new String(EMFUtil.getEPackageBytes(ePackage, false, packageRegistry));

      startPackageUnit(out, type, time, data);
      for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        String packageURI = packageInfo.getPackageURI();
        CDOIDMetaRange metaIDRange = packageInfo.getMetaIDRange();
        startPackageInfo(out, packageURI, metaIDRange);
      }

      endPackageUnit(out);
    }
  }

  protected abstract void startPackageUnit(OUT out, Type type, long time, String data) throws Exception;

  protected abstract void endPackageUnit(OUT out) throws Exception;

  protected abstract void startPackageInfo(OUT out, String packageURI, CDOIDMetaRange metaIDRange) throws Exception;

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
  public static class XML extends CDOServerBackup<BufferedReader, XMLStack>
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
    protected final XMLStack createOutput(OutputStream out) throws Exception
    {
      return new XMLStack(out);
    }

    @Override
    protected void exportRepository(XMLStack out) throws Exception
    {
      out.element("repository");
      out.attribute("name", getRepository().getName());
      out.attribute("uuid", getRepository().getUUID());

      out.push();
      super.exportRepository(out);
      out.done();
    }

    @Override
    protected void startPackageUnit(XMLStack out, Type type, long time, String data) throws Exception
    {
      out.element("packageUnit");
      out.attribute("type", type);
      out.attribute("time", time);
      out.attribute("data", data);
      out.push();
    }

    @Override
    protected void endPackageUnit(XMLStack out) throws Exception
    {
      out.pop();
    }

    @Override
    protected void startPackageInfo(XMLStack out, String uri, CDOIDMetaRange metaIDRange) throws Exception
    {
      out.element("packageInfo");
      out.attribute("uri", uri);
      out.attribute("lower", str(metaIDRange.getLowerBound()));
      out.attribute("upper", str(metaIDRange.getUpperBound()));
    }

    @Override
    protected void exportBranch(XMLStack out, CDOBranch branch) throws Exception
    {
      out.element("branch");
      out.attribute("id", branch.getID());
      out.attribute("name", branch.getName());
      out.attribute("local", branch.isLocal());
      out.attribute("time", branch.getBase().getTimeStamp());
      if (!branch.isMainBranch())
      {
        out.attribute("parent", branch.getBase().getBranch().getID());
      }

      out.push();
      super.exportBranch(out, branch);
      out.pop();

    }

    @Override
    protected void exportRevision(XMLStack out, CDORevision revision) throws Exception
    {
      InternalCDORevision rev = (InternalCDORevision)revision;

      out.element("revision");
      out.attribute("id", str(rev.getID()));
      out.attribute("class", new CDOClassifierRef(rev.getEClass()).getURI());
      out.attribute("version", rev.getVersion());
      out.attribute("time", rev.getTimeStamp());

      long revised = rev.getRevised();
      if (revised != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        out.attribute("revised", revised);
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

    protected void exportFeature(XMLStack out, EStructuralFeature feature, Object value) throws Exception
    {
      out.element("feature");
      out.attribute("name", feature.getName());
      if (value instanceof CDOID)
      {
        out.attribute("value", str((CDOID)value));
      }
      else
      {
        out.attributeOrNull("value", value);
      }
    }

    @Override
    protected void exportCommit(XMLStack out, CDOCommitInfo commitInfo) throws Exception
    {
      out.element("commit");
      out.attribute("time", commitInfo.getTimeStamp());
      long previous = commitInfo.getPreviousTimeStamp();
      if (previous != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        out.attribute("previous", previous);
      }

      int branch = commitInfo.getBranch().getID();
      if (branch != CDOBranch.MAIN_BRANCH_ID)
      {
        out.attribute("branch", branch);
      }

      out.attribute("user", commitInfo.getUserID());
      out.attribute("comment", commitInfo.getComment());
    }
  }
}
