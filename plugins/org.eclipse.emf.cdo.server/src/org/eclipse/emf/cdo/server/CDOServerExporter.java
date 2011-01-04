/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - https://bugs.eclipse.org/333260
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.XMLOutput;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import org.xml.sax.SAXException;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDOServerExporter<OUT>
{
  private IRepository repository;

  public CDOServerExporter(IRepository repository)
  {
    this.repository = repository;
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

    ISession session = repository.getSessionManager().openSession(null);
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
    CDOPackageRegistry packageRegistry = repository.getPackageRegistry();
    CDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits();
    for (CDOPackageUnit packageUnit : packageUnits)
    {
      String id = packageUnit.getID();

      // Don't export the system packages, as 4.0 does not expect these
      if (CDOModelUtil.CORE_PACKAGE_URI.equals(id) || CDOModelUtil.RESOURCE_PACKAGE_URI.equals(id))
      {
        continue;
      }

      CDOPackageUnit.Type type = packageUnit.getOriginalType();
      long time = packageUnit.getTimeStamp();

      EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
      String data = new String(EMFUtil.getEPackageBytes(ePackage, false, packageRegistry));

      startPackageUnit(out, id, type, time, data);
      for (CDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        String packageURI = packageInfo.getPackageURI();
        exportPackageInfo(out, packageURI);
      }

      endPackageUnit(out);
    }
  }

  protected abstract void startPackageUnit(OUT out, String id, CDOPackageUnit.Type type, long time, String data)
      throws Exception;

  protected abstract void endPackageUnit(OUT out) throws Exception;

  protected abstract void exportPackageInfo(OUT out, String packageURI) throws Exception;

  protected void exportBranches(final OUT out) throws Exception
  {
    exportBranch(out);
  }

  protected void exportBranch(OUT out) throws Exception
  {
    exportRevisions(out);
  }

  protected void exportRevisions(final OUT out) throws Exception
  {
    ((Repository)repository).handleRevisions(new CDORevisionHandler()
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

  protected void exportLobs(final OUT out) throws Exception
  {
  }

  protected void exportCommits(final OUT out) throws Exception
  {
  }

  /**
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

    public static final String REVISION_RESOURCE = "resource";

    public static final String REVISION_CONTAINER = "container";

    public static final String REVISION_FEATURE = "feature";

    public static final String FEATURE = "feature";

    public static final String FEATURE_NAME = "name";

    public static final String FEATURE_TYPE = "type";

    public static final String FEATURE_INNER_FEATURE = "innerFeature";

    public static final String FEATURE_INNER_TYPE = "innerType";

    public static final String FEATURE_VALUE = "value";

    public static final String FEATURE_ID = "id";

    public static final String FEATURE_SIZE = "size";

    public static final String TYPE_BLOB = "Blob";

    public static final String TYPE_CLOB = "Clob";

    public static final String TYPE_FEATURE_MAP = "FeatureMap";

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
      out.attribute(REPOSITORY_ROOT, str(((Repository)getRepository()).getRootResourceID()));
      out.attribute(REPOSITORY_CREATED, getRepository().getStore().getCreationTime());
      out.attribute(REPOSITORY_COMMITTED, ((Repository)getRepository()).getLastCommitTimeStamp());

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
    protected void startPackageUnit(XMLOutput out, String id, CDOPackageUnit.Type type, long time, String data)
        throws Exception
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
    protected void exportBranch(XMLOutput out) throws Exception
    {
      out.element(BRANCH);
      out.attribute(BRANCH_ID, 0);
      out.attribute(BRANCH_NAME, "MAIN");
      out.attribute(BRANCH_TIME, getRepository().getStore().getCreationTime());

      out.push();
      super.exportBranch(out);
      out.pop();
    }

    private String getURI(CDOClassifierRef ref)
    {
      return ref.getPackageURI() + CDOClassifierRef.URI_SEPARATOR + ref.getClassifierName();
    }

    @Override
    protected void exportRevision(XMLOutput out, CDORevision revision) throws Exception
    {
      InternalCDORevision rev = (InternalCDORevision)revision;

      out.element(REVISION);
      out.attribute(REVISION_ID, str(rev.getID()));
      out.attribute(REVISION_CLASS, getURI(new CDOClassifierRef(rev.getEClass())));
      out.attribute(REVISION_VERSION, rev.getVersion());
      out.attribute(REVISION_TIME, rev.getCreated());

      long revised = rev.getRevised();
      if (revised != 0 /* CDOBranchPoint.UNSPECIFIED_DATE */)
      {
        out.attribute(REVISION_REVISED, revised);
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
      CDOClassInfo classInfo = CDOModelUtil.getClassInfo(rev.getEClass());
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
          if (value != null && !(value instanceof CDOID && CDOIDUtil.isNull((CDOID)value)))
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

    protected void exportFeature(XMLOutput out, EStructuralFeature feature, String featureType, Object value)
        throws SAXException
    {
      if (value instanceof CDOID)
      {
        out.attribute(featureType, Object.class.getSimpleName());
        out.attribute(FEATURE_VALUE, str((CDOID)value));
      }
      else if (value instanceof Date)
      {
        Date date = (Date)value;
        out.attribute(featureType, Date.class.getSimpleName());
        out.attribute(FEATURE_VALUE, date.getTime());
      }
      else if (FeatureMapUtil.isFeatureMap(feature))
      {
        FeatureMap.Entry entry = (FeatureMap.Entry)value;
        EStructuralFeature innerFeature = entry.getEStructuralFeature();
        Object innerValue = entry.getValue();

        out.attribute(featureType, TYPE_FEATURE_MAP);
        out.attribute(FEATURE_INNER_FEATURE, innerFeature.getName());
        exportFeature(out, innerFeature, FEATURE_INNER_TYPE, innerValue);
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
    protected void exportCommits(XMLOutput out) throws Exception
    {
      out.element(COMMITS);

      out.push();
      super.exportCommits(out);
      out.pop();
    }

    protected final String str(CDOID id)
    {
      StringBuilder builder = new StringBuilder();
      CDOIDUtil.write40(builder, id);
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
