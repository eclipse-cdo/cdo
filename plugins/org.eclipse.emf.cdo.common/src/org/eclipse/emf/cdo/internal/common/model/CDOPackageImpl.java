/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/246442 
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackageManager;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOPackageImpl extends CDOModelElementImpl implements InternalCDOPackage
{
  private static final ContextTracer MODEL_TRACER = new ContextTracer(OM.DEBUG_MODEL, CDOPackageImpl.class);

  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDOPackageImpl.class);

  private CDOPackageManager packageManager;

  private String packageURI;

  private List<CDOClass> classes;

  private List<CDOClass> index;

  private String ecore;

  private boolean ecoreLoaded;

  private boolean dynamic;

  private CDOIDMetaRange metaIDRange;

  private String parentURI;

  private transient boolean persistent = true;

  public CDOPackageImpl()
  {
  }

  public CDOPackageImpl(CDOPackageManager packageManager, String packageURI, String name, String ecore,
      boolean dynamic, CDOIDMetaRange metaIDRange, String parentURI)
  {
    super(name);
    this.packageManager = packageManager;
    this.packageURI = packageURI;
    this.dynamic = dynamic;
    this.metaIDRange = metaIDRange;
    this.parentURI = parentURI;
    if (MODEL_TRACER.isEnabled())
    {
      MODEL_TRACER.format("Created {0}", this);
    }

    setEcore(ecore);
    createLists();
  }

  public CDOPackageImpl(CDOPackageManager packageManager, CDODataInput in) throws IOException
  {
    this.packageManager = packageManager;
    createLists();
    read(in);
  }

  /**
   * Creates a proxy CDO package
   */
  public CDOPackageImpl(CDOPackageManager packageManager, String packageURI, boolean dynamic,
      CDOIDMetaRange metaIDRange, String parentURI)
  {
    this.packageManager = packageManager;
    this.packageURI = packageURI;
    this.dynamic = dynamic;
    this.metaIDRange = metaIDRange;
    this.parentURI = parentURI;
    if (MODEL_TRACER.isEnabled())
    {
      MODEL_TRACER.format("Created proxy package {0}, dynamic={1}, metaIDRange={2}, parentURI={3}", packageURI,
          dynamic, metaIDRange, packageURI);
    }
  }

  @Override
  public void read(CDODataInput in) throws IOException
  {
    super.read(in);
    packageURI = in.readCDOPackageURI();
    dynamic = in.readBoolean();
    metaIDRange = in.readCDOIDMetaRange();
    parentURI = in.readString();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read package: URI={0}, name={1}, dynamic={2}, metaIDRange={3}, parentURI={4}",
          packageURI, getName(), dynamic, metaIDRange, parentURI);
    }

    int size = in.readInt();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} classes", size);
    }

    for (int i = 0; i < size; i++)
    {
      CDOClass cdoClass = in.readCDOClass(this);
      addClass(cdoClass);
    }
  }

  @Override
  public void write(CDODataOutput out) throws IOException
  {
    load();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing package: URI={0}, name={1}, dynamic={2}, metaIDRange={3}, parentURI={4}",
          packageURI, getName(), dynamic, metaIDRange, parentURI);
    }

    super.write(out);
    out.writeCDOPackageURI(packageURI);
    out.writeBoolean(dynamic);
    out.writeCDOIDMetaRange(metaIDRange);
    out.writeString(parentURI);

    int size = classes.size();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} classes", size);
    }

    out.writeInt(size);
    for (CDOClass cdoClass : classes)
    {
      out.writeCDOClass(cdoClass);
    }
  }

  public void setPackageManager(CDOPackageManager packageManager)
  {
    this.packageManager = packageManager;
  }

  public CDOPackageManager getPackageManager()
  {
    return packageManager;
  }

  public String getParentURI()
  {
    return parentURI;
  }

  public void setParentURI(String parentURI)
  {
    this.parentURI = parentURI;
  }

  public CDOPackage getTopLevelPackage()
  {
    CDOPackage parentPackage = getParentPackage();
    return parentPackage == null ? this : parentPackage.getTopLevelPackage();
  }

  public CDOPackage getParentPackage()
  {
    if (parentURI == null)
    {
      return null;
    }

    return packageManager.lookupPackage(parentURI);
  }

  public CDOPackage[] getSubPackages(boolean recursive)
  {
    List<CDOPackage> result = new ArrayList<CDOPackage>();
    CDOPackage[] allPackages = packageManager.getPackages();
    getSubPackages(this, allPackages, result, recursive);
    return result.toArray(new CDOPackage[result.size()]);
  }

  private void getSubPackages(CDOPackage parentPackage, CDOPackage[] allPackages, List<CDOPackage> result,
      boolean recursive)
  {
    for (CDOPackage cdoPackage : allPackages)
    {
      if (ObjectUtil.equals(cdoPackage.getParentURI(), parentPackage.getPackageURI()))
      {
        result.add(cdoPackage);
        if (recursive)
        {
          getSubPackages(cdoPackage, allPackages, result, true);
        }
      }
    }
  }

  public String getPackageURI()
  {
    return packageURI;
  }

  public void setPackageURI(String packageURI)
  {
    this.packageURI = packageURI;
  }

  public String getQualifiedName()
  {
    CDOPackage parentPackage = getParentPackage();
    if (parentPackage != null)
    {
      return parentPackage.getQualifiedName() + "." + getName();
    }

    return getName();
  }

  public int getClassCount()
  {
    load();
    return classes.size();
  }

  public CDOClass[] getClasses()
  {
    load();
    return classes.toArray(new CDOClass[classes.size()]);
  }

  public void setClasses(List<CDOClass> classes)
  {
    this.classes = classes;
    for (CDOClass cdoClass : classes)
    {
      ((InternalCDOClass)cdoClass).setContainingPackage(this);
      setIndex(cdoClass.getClassifierID(), cdoClass);
    }
  }

  /**
   * @return All classes with <code>isAbstract() == false</code> and <code>isSystem() == false</code>.
   */
  public CDOClass[] getConcreteClasses()
  {
    load();
    List<CDOClass> result = new ArrayList<CDOClass>(0);
    for (CDOClass cdoClass : classes)
    {
      if (!cdoClass.isAbstract())
      {
        result.add(cdoClass);
      }
    }

    return result.toArray(new CDOClass[result.size()]);
  }

  public CDOClass lookupClass(int classifierID)
  {
    load();
    return index.get(classifierID);
  }

  public synchronized String basicGetEcore()
  {
    return ecore;
  }

  public synchronized String getEcore()
  {
    if (!ecoreLoaded)
    {
      if (parentURI == null && !isSystem())
      {
        ((InternalCDOPackageManager)packageManager).loadPackageEcore(this);
      }
    }

    return ecore;
  }

  public synchronized void setEcore(String ecore)
  {
    this.ecore = ecore;
    ecoreLoaded = true;
  }

  public CDOIDMetaRange getMetaIDRange()
  {
    return metaIDRange;
  }

  public void setMetaIDRange(CDOIDMetaRange metaIDRange)
  {
    this.metaIDRange = metaIDRange;
  }

  public boolean isDynamic()
  {
    return dynamic;
  }

  public void setDynamic(boolean dynamic)
  {
    this.dynamic = dynamic;
  }

  public boolean isSystem()
  {
    return false;
  }

  public boolean isProxy()
  {
    return classes == null;
  }

  public boolean isPersistent()
  {
    return persistent;
  }

  public void setPersistent(boolean persistent)
  {
    this.persistent = persistent;
  }

  public void addClass(CDOClass cdoClass)
  {
    int classifierID = cdoClass.getClassifierID();
    if (MODEL_TRACER.isEnabled())
    {
      MODEL_TRACER.format("Adding class: {0}", cdoClass);
    }

    setIndex(classifierID, cdoClass);
    classes.add(cdoClass);
  }

  public int compareTo(CDOPackage that)
  {
    return getPackageURI().compareTo(that.getPackageURI());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOPackage(URI={0}, name={1}, dynamic={2}, metaIDRange={3}, parentURI={4})",
        packageURI, getName(), dynamic, metaIDRange, parentURI);
  }

  private void setIndex(int classifierID, CDOClass cdoClass)
  {
    while (classifierID >= index.size())
    {
      index.add(null);
    }

    index.set(classifierID, cdoClass);
  }

  private void createLists()
  {
    classes = new ArrayList<CDOClass>(0);
    index = new ArrayList<CDOClass>(0);
  }

  private synchronized void load()
  {
    if (classes == null)
    {
      createLists();
      ((InternalCDOPackageManager)packageManager).loadPackage(this);
    }
  }
}
