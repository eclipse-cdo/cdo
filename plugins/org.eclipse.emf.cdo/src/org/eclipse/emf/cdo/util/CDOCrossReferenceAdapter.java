/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An {@link ECrossReferenceAdapter} that does instanceof checks of {@link Notifier} instances
 * in the order {@link Resource}, {@link EObject}, and {@link ResourceSet}.
 * <p>
 * <b>Background:</b>
 * For performance reasons (assuming that there are typically more <code>EObject</code> instances than <code>Resource</code> instances)
 * EMF does instanceof checks of <code>Notifier</code> instances in the order <code>EObject</code>, <code>Resource</code>, and <code>ResourceSet</code>.
 * That is problematic with CDOResources because they implement both <code>Resource</code> and <code>EObject</code>.
 *
 * @author Eike Stepper
 * @since 4.6
 */
public final class CDOCrossReferenceAdapter extends ECrossReferenceAdapter
{
  @Override
  public void setTarget(Notifier target)
  {
    if (target instanceof Resource)
    {
      super.setTarget((Resource)target);
    }
    else if (target instanceof EObject)
    {
      super.setTarget((EObject)target);
    }
    else if (target instanceof ResourceSet)
    {
      super.setTarget((ResourceSet)target);
    }
  }

  @Override
  public void unsetTarget(Notifier target)
  {
    if (target instanceof Resource)
    {
      super.unsetTarget((Resource)target);
    }
    else if (target instanceof EObject)
    {
      super.unsetTarget((EObject)target);
    }
    else if (target instanceof ResourceSet)
    {
      super.unsetTarget((ResourceSet)target);
    }
  }

  @Override
  protected void selfAdapt(Notification notification)
  {
    Object notifier = notification.getNotifier();
    if (notifier instanceof Resource)
    {
      switch (notification.getFeatureID(Resource.class))
      {
      case Resource.RESOURCE__CONTENTS:
      {
        if (!unloadedResources.contains(notifier))
        {
          switch (notification.getEventType())
          {
          case Notification.REMOVE:
          {
            Resource resource = (Resource)notifier;
            if (!resource.isLoaded())
            {
              EObject eObject = (EObject)notification.getOldValue();
              unloadedEObjects.put(eObject, resource);
              for (Iterator<EObject> i = EcoreUtil.getAllProperContents(eObject, false); i.hasNext();)
              {
                unloadedEObjects.put(i.next(), resource);
              }
            }
            break;
          }
          case Notification.REMOVE_MANY:
          {
            Resource resource = (Resource)notifier;
            if (!resource.isLoaded())
            {
              @SuppressWarnings("unchecked")
              List<EObject> eObjects = (List<EObject>)notification.getOldValue();
              for (Iterator<EObject> i = EcoreUtil.getAllProperContents(eObjects, false); i.hasNext();)
              {
                unloadedEObjects.put(i.next(), resource);
              }
            }
            break;
          }
          default:
          {
            handleContainment(notification);
            break;
          }
          }
        }
        break;
      }
      case Resource.RESOURCE__IS_LOADED:
      {
        if (notification.getNewBooleanValue())
        {
          unloadedResources.remove(notifier);
          for (Notifier child : ((Resource)notifier).getContents())
          {
            addAdapter(child);
          }
        }
        else
        {
          unloadedResources.add((Resource)notifier);
          for (Iterator<Map.Entry<EObject, Resource>> i = unloadedEObjects.entrySet().iterator(); i.hasNext();)
          {
            Map.Entry<EObject, Resource> entry = i.next();
            if (entry.getValue() == notifier)
            {
              i.remove();
              if (!resolve())
              {
                EObject eObject = entry.getKey();
                Collection<EStructuralFeature.Setting> settings = inverseCrossReferencer.get(eObject);
                if (settings != null)
                {
                  for (EStructuralFeature.Setting setting : settings)
                  {
                    getInverseCrossReferencer().addProxy(eObject, setting.getEObject());
                  }
                }
              }
            }
          }
        }
        break;
      }
      }
    }
    else if (notifier instanceof EObject)
    {
      Object feature = notification.getFeature();
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          handleContainment(notification);
        }
        else if (isIncluded(reference))
        {
          handleCrossReference(reference, notification);
        }
      }
    }
    else if (notifier instanceof ResourceSet)
    {
      if (notification.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES)
      {
        handleContainment(notification);
      }
    }
  }

  @Override
  protected CDOInverseCrossReferencer createInverseCrossReferencer()
  {
    return new CDOInverseCrossReferencer();
  }

  protected CDOInverseCrossReferencer getInverseCrossReferencer()
  {
    return (CDOInverseCrossReferencer)inverseCrossReferencer;
  }

  /**
   * An {@link org.eclipse.emf.ecore.util.ECrossReferenceAdapter.InverseCrossReferencer InverseCrossReferencer} with an
   * {@link #addProxy(EObject, EObject)} method that is visible to {@link CDOCrossReferenceAdapter}.
   *
   * @author Eike Stepper
   */
  protected class CDOInverseCrossReferencer extends InverseCrossReferencer
  {
    private static final long serialVersionUID = 1L;

    @Override
    protected void addProxy(EObject proxy, EObject context)
    {
      super.addProxy(proxy, context);
    }
  }
}
