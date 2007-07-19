/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

/**
 * @author Eike Stepper
 */
public class CDOObjectImpl extends EStoreEObjectImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOObjectImpl.class);

  private CDOID id;

  private CDOState state;

  private CDOResourceImpl resource;

  private CDORevisionImpl revision;

  public CDOObjectImpl()
  {
    super(CDOStore.INSTANCE);
    state = CDOState.TRANSIENT;
    eContainer = null;
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public CDORevisionImpl cdoRevision()
  {
    return revision;
  }

  public CDOClassImpl cdoClass()
  {
    return getCDOClass(this);
  }

  public CDOViewImpl cdoView()
  {
    return getCDOView(this);
  }

  public CDOResourceImpl cdoResource()
  {
    if (this instanceof CDOResourceImpl)
    {
      resource = (CDOResourceImpl)this;
    }

    return resource;
  }

  public boolean cdoTransient()
  {
    return isCDOTransient(this);
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("id == null");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  public void cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      this.state = state;
    }
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (CDORevisionImpl)revision;
  }

  public void cdoInternalSetView(CDOView view)
  {
    if (this instanceof CDOResourceImpl)
    {
      ((CDOResourceImpl)this).cdoSetView((CDOViewImpl)view);
    }
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    if (this instanceof CDOResourceImpl)
    {
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resource: {0}", resource);
    }

    this.resource = (CDOResourceImpl)resource;
  }

  public void cdoInternalFinalizeRevision()
  {
    finalizeCDORevision(this, eContainer, eContainerFeatureID, eSettings);
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    return eDynamicFeature(dynamicFeatureID);
  }

  @Deprecated
  void initializeContainer(InternalEObject container, EStructuralFeature eContainingFeature)
  {
    eContainer = container;
    CDORevisionImpl revision = cdoRevision();
    if (eContainer != null)
    {
      if (revision != null)
      {
        if (eContainer instanceof CDOObject && !(eContainer instanceof CDOResource))
        {
          revision.setContainerID(((CDOObject)eContainer).cdoID());
        }
        else
        {
          revision.setContainerID(CDOID.NULL);
        }
      }

      if (eContainingFeature instanceof EReference)
      {
        EReference eContainingReference = (EReference)eContainingFeature;
        EReference eOpposite = eContainingReference.getEOpposite();
        if (eOpposite != null)
        {
          eContainerFeatureID = eClass().getFeatureID(eOpposite);
          return;
        }
      }

      eContainerFeatureID = EOPPOSITE_FEATURE_BASE - eContainer.eClass().getFeatureID(eContainingFeature);
    }
    else
    {
      if (revision != null)
      {
        revision.setContainerID(CDOID.NULL);
      }
    }
  }

  @Override
  protected void eInitializeContainer()
  {
    throw new ImplementationError();
  }

  @Override
  protected void eSetDirectResource(Internal resource)
  {
    super.eSetDirectResource(resource);
    if (resource instanceof CDOResourceImpl)
    {
      this.resource = (CDOResourceImpl)resource;
    }
  }

  /**
   * Don't cache non-transient features in this CDOObject's {@link #eSettings()}.
   */
  @Override
  protected boolean eIsCaching()
  {
    return false;
  }

  @Override
  public Object dynamicGet(int dynamicFeatureID)
  {
    if (cdoTransient())
    {
      if (eSettings == null)
      {
        return null;
      }

      return eSettings[dynamicFeatureID];
    }

    return super.dynamicGet(dynamicFeatureID);
  }

  @Override
  public boolean eIsSet(EStructuralFeature feature)
  {
    if (cdoTransient())
    {
      // TODO What about defaultValues != null?
      if (eSettings == null)
      {
        return false;
      }

      return eSettings[eDynamicFeatureID(feature)] != null;
    }

    return super.eIsSet(feature);
  }

  @Override
  public void dynamicSet(int dynamicFeatureID, Object value)
  {
    if (cdoTransient())
    {
      eSettings(); // Important to create eSettings array if necessary
      eSettings[dynamicFeatureID] = value;
    }
    else
    {
      super.dynamicSet(dynamicFeatureID, value);
    }
  }

  @Override
  public void dynamicUnset(int dynamicFeatureID)
  {
    if (cdoTransient())
    {
      if (eSettings != null)
      {
        eSettings[dynamicFeatureID] = null;
      }
    }
    else
    {
      super.dynamicUnset(dynamicFeatureID);
    }
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    InternalEObject container;
    if (cdoTransient())
    {
      container = eContainer;
    }
    else
    {
      container = CDOStore.INSTANCE.getContainer(this);
    }

    if (container instanceof CDOResource)
    {
      return null;
    }

    return container;
  }

  @Override
  public int eContainerFeatureID()
  {
    if (cdoTransient())
    {
      return eContainerFeatureID;
    }

    return CDOStore.INSTANCE.getContainingFeatureID(this);
  }

  @Override
  protected void eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting container: {0}, featureID={1}", newContainer, newContainerFeatureID);
    }

    if (cdoTransient())
    {
      eContainer = newContainer;
      eContainerFeatureID = newContainerFeatureID;
    }
    else
    {
      CDOStore.INSTANCE.setContainer(this, newContainer, newContainerFeatureID);
    }
  }

  @Override
  public Resource eResource()
  {
    return cdoResource();
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj == this;
  }

  @Override
  public String toString()
  {
    if (id == null)
    {
      return eClass().getName() + "?";
    }

    return eClass().getName() + "@" + id;
  }

  static CDOClassImpl getCDOClass(InternalCDOObject cdoObject)
  {
    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    CDOSessionPackageManager packageManager = view.getSession().getPackageManager();
    return ModelUtil.getCDOClass(cdoObject.eClass(), packageManager);
  }

  static CDOViewImpl getCDOView(InternalCDOObject cdoObject)
  {
    CDOResource resource = cdoObject.cdoResource();
    return resource != null ? (CDOViewImpl)cdoObject.cdoResource().cdoView() : null;
  }

  static boolean isCDOTransient(InternalCDOObject cdoObject)
  {
    final CDOState cdoState = cdoObject.cdoState();
    return cdoState == CDOState.TRANSIENT || cdoState == CDOState.PREPARED_ATTACH;
  }

  static void finalizeCDORevision(InternalCDOObject cdoObject, EObject eContainer, int eContainerFeatureID,
      Object[] eSettings)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Finalizing revision for {0}", cdoObject);
    }

    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    CDORevisionImpl revision = (CDORevisionImpl)cdoObject.cdoRevision();
    revision.setVersion(1);
    revision.setContainerID(eContainer == null ? CDOID.NULL : ((CDOObjectImpl)eContainer).cdoID());
    revision.setContainingFeature(eContainerFeatureID);

    if (eSettings != null)
    {
      EClass eClass = cdoObject.eClass();
      for (int i = 0; i < eClass.getFeatureCount(); i++)
      {
        Object setting = eSettings[i];
        if (setting != null)
        {
          EStructuralFeature eFeature = cdoObject.cdoInternalDynamicFeature(i);
          if (!eFeature.isTransient())
          {
            finalizeRevisionFeature(view, revision, i, setting, eFeature, eSettings);
          }
        }
      }
    }
  }

  private static void finalizeRevisionFeature(CDOViewImpl view, CDORevisionImpl revision, int i, Object setting,
      EStructuralFeature eFeature, Object[] eSettings)
  {
    CDOFeatureImpl cdoFeature = ModelUtil.getCDOFeature(eFeature, view.getSession().getPackageManager());
    if (TRACER.isEnabled())
    {
      TRACER.format("Finalizing feature {0}", cdoFeature);
    }

    boolean isReference = cdoFeature.isReference();
    if (cdoFeature.isMany())
    {
      int index = 0;
      EList<Object> list = (EList<Object>)setting;
      for (Object value : list)
      {
        if (isReference)
        {
          value = CDOStore.convertToID(view, value);
        }

        revision.add(cdoFeature, index++, value);
      }
    }
    else
    {
      if (isReference)
      {
        setting = CDOStore.convertToID(view, setting);
      }

      revision.set(cdoFeature, 0, setting);
    }

    if (eSettings != null)
    {
      eSettings[i] = null;
    }
  }
}
