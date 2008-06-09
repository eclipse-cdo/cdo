/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
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
  private static final ContextTracer MODEL = new ContextTracer(OM.DEBUG_MODEL, CDOPackageImpl.class);

  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, CDOPackageImpl.class);

  private CDOPackageManager packageManager;

  private String packageURI;

  private List<CDOClass> classes;

  private List<CDOClass> index;

  private String ecore;

  private boolean dynamic;

  private CDOIDMetaRange metaIDRange;

  private String parentURI;

  /**
   * TODO If this is only needed by the client then put it into server info
   */
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
    this.ecore = ecore;
    this.dynamic = dynamic;
    this.metaIDRange = metaIDRange;
    this.parentURI = parentURI;
    if (MODEL.isEnabled())
    {
      MODEL.format("Created {0}", this);
    }

    createLists();
  }

  public CDOPackageImpl(CDOPackageManager packageManager, ExtendedDataInput in) throws IOException
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
    if (MODEL.isEnabled())
    {
      MODEL.format("Created proxy package {0}, dynamic={1}, metaIDRange={2}, parentURI={3}", packageURI, dynamic,
          metaIDRange, packageURI);
    }
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    super.read(in);
    packageURI = in.readString();
    dynamic = in.readBoolean();
    ecore = in.readString();
    metaIDRange = CDOIDUtil.readMetaRange(in);
    parentURI = in.readString();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read package: URI={0}, name={1}, dynamic={2}, metaIDRange={3}, parentURI={4}", packageURI,
          getName(), dynamic, metaIDRange, parentURI);
    }

    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} classes", size);
    }

    for (int i = 0; i < size; i++)
    {
      CDOClass cdoClass = CDOModelUtil.readClass(this, in);
      addClass(cdoClass);
    }
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    resolve();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing package: URI={0}, name={1}, dynamic={2}, metaIDRange={3}, parentURI={4}", packageURI,
          getName(), dynamic, metaIDRange, parentURI);
    }

    super.write(out);
    out.writeString(packageURI);
    out.writeBoolean(dynamic);
    out.writeString(ecore);
    CDOIDUtil.writeMetaRange(out, metaIDRange);
    out.writeString(parentURI);

    int size = classes.size();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} classes", size);
    }

    out.writeInt(size);
    for (CDOClass cdoClass : classes)
    {
      CDOModelUtil.writeClass(out, cdoClass);
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

  public int getClassCount()
  {
    resolve();
    return classes.size();
  }

  public CDOClass[] getClasses()
  {
    resolve();
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
    resolve();
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
    resolve();
    return index.get(classifierID);
  }

  public String getEcore()
  {
    if (ecore == null && packageManager instanceof CDOPackageManagerImpl && parentURI == null)
    {
      // TODO Can ecore be null?
      ecore = ((CDOPackageManagerImpl)packageManager).provideEcore(this);
    }

    return ecore;
  }

  /**
   * TODO Add IStore API to demand read dynamic ecore string
   */
  public void setEcore(String ecore)
  {
    this.ecore = ecore;
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
    if (MODEL.isEnabled())
    {
      MODEL.format("Adding class: {0}", cdoClass);
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

  @Override
  protected void onInitialize()
  {
    for (CDOClass cdoClass : classes)
    {
      ((InternalCDOClass)cdoClass).initialize();
    }
  }

  private void setIndex(int classifierID, CDOClass cdoClass)
  {
    while (classifierID >= index.size())
    {
      index.add(null);
    }

    index.set(classifierID, cdoClass);
  }

  private void resolve()
  {
    if (classes == null && packageManager instanceof CDOPackageManagerImpl)
    {
      createLists();
      ((CDOPackageManagerImpl)packageManager).resolve(this);
    }
  }

  private void createLists()
  {
    classes = new ArrayList<CDOClass>(0);
    index = new ArrayList<CDOClass>(0);
  }
}
