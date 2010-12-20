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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOIDDangling;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class CDOIDDanglingImpl extends AbstractCDOID implements CDOIDDangling
{
  private static final String NOT_SUPPORTED_MSG = "Not supported for CDOIDDangling"; //$NON-NLS-1$

  private static final long serialVersionUID = 1L;

  private transient InternalEObject target;

  // private List<Reference> references = new ArrayList<Reference>();

  public CDOIDDanglingImpl(InternalEObject target)
  {
    CheckUtil.checkArg(target, "target");
    this.target = target;
  }

  public InternalEObject getTarget()
  {
    return target;
  }

  // public List<Reference> getReferences()
  // {
  // return references;
  // }
  //
  // public void addReference(InternalCDOObject sourceObject, EStructuralFeature sourceFeature)
  // {
  // synchronized (references)
  // {
  // for (Reference reference : references)
  // {
  // if (reference.getSourceObject() == sourceObject && reference.getSourceFeature() == sourceFeature)
  // {
  // return;
  // }
  // }
  //
  // references.add(new ReferenceImpl(sourceObject, sourceFeature));
  // }
  // }
  //
  // public void dispose()
  // {
  // target = null;
  // references.clear();
  // }

  public Type getType()
  {
    return Type.DANGLING_OBJECT;
  }

  public boolean isDangling()
  {
    return true;
  }

  public boolean isExternal()
  {
    return false;
  }

  public boolean isNull()
  {
    return false;
  }

  public boolean isObject()
  {
    return true;
  }

  public boolean isTemporary()
  {
    return false;
  }

  public String toURIFragment()
  {
    return EcoreUtil.getURI(target).fragment();
  }

  @Override
  public void read(String fragmentPart)
  {
    throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOIDDangling)
    {
      CDOIDDangling that = (CDOIDDangling)obj;
      return target == that.getTarget();
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return target.hashCode();
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return toURIFragment().compareTo(((CDOIDDanglingImpl)o).toURIFragment());
  }

  // /**
  // * @author Eike Stepper
  // */
  // public final class ReferenceImpl implements Reference
  // {
  // private InternalCDOObject sourceObject;
  //
  // private EStructuralFeature sourceFeature;
  //
  // public ReferenceImpl(InternalCDOObject sourceObject, EStructuralFeature sourceFeature)
  // {
  // this.sourceObject = sourceObject;
  // this.sourceFeature = sourceFeature;
  // }
  //
  // public InternalCDOObject getSourceObject()
  // {
  // return sourceObject;
  // }
  //
  // public EStructuralFeature getSourceFeature()
  // {
  // return sourceFeature;
  // }
  //
  // public CDOIDDangling getTargetID()
  // {
  // return CDOIDDanglingImpl.this;
  // }
  // }
}
