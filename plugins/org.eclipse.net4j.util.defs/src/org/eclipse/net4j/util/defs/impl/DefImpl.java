/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs.impl;

import org.eclipse.net4j.util.defs.Def;
import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Definition</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public abstract class DefImpl extends EObjectImpl implements Def
{

  /** The internal instance. */
  private Object instance;

  /** the touched (dirty) state */
  private boolean touched;

  /* Lifecycle listener for the internal instance */
  protected IListener instanceListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      if (lifecycle == instance)
      {
        handleDeactivation(instance);
      }
    }
  };

  /** adapter that tracks if this def was touched */
  protected Adapter touchedAdapter = new AdapterImpl()
  {

    @Override
    public void notifyChanged(Notification msg)
    {
      switch (msg.getEventType())
      {
      case Notification.SET:
      case Notification.UNSET:
      case Notification.ADD:
      case Notification.REMOVE:
        touched = true;
      }
    }
  };

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @generated NOT
   */
  protected DefImpl()
  {
    super();
    eAdapters().add(touchedAdapter);
  }

  /**
   * Gets the internal instance.
   * 
   * @return the internal instance
   * @ADDED
   */
  public Object getInternalInstance()
  {
    return instance;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jUtilDefsPackage.Literals.DEF;
  }

  /**
   * returns an instance that is created for the given definition. Instances are stored and reused. The instance is
   * activated before it is returned. A later call to #getInstace
   * 
   * @return the instance that this definition defines
   * @see #createInstance()
   * @see #wireInstance(Object)
   * @see LifecycleUtil#activate(Object)
   * @generated NOT
   */
  public Object getInstance()
  {
    synchronized (this)
    {
      if (!isSetInstance())
      {
        // instance is not created yet
        setInstance();
      }
      else if (isSetInstance() && isTouched())
      {
        // instance is created but def settings were changed afterwards
        unsetInstance();
        setInstance();
      }
      touched = false;
      return instance;
    }
  }

  /**
   * @ADDED
   */
  private void setInstance()
  {
    validateDefinition();
    instance = createInstance();
    wireInstance(instance);
    activateInstance(instance);
  }

  /**
   * Activate a given instance.
   * 
   * @param instance
   *          the instance to activate
   * @ADDED
   */
  protected void activateInstance(Object instance)
  {
    LifecycleUtil.activate(instance);
  }

  /**
   * Deactivate a given instance.
   * 
   * @ADDED
   */
  protected void deactivateInstance(Object instance)
  {
    LifecycleUtil.deactivate(instance);
  }

  /**
   * <!-- begin-user-doc -->Unsets the instance managed by this Definition. The internal reference to it is cleared and
   * internal listeners to it are removed <!-- end-user-doc -->
   * 
   * @ADDED
   */
  public void unsetInstance()
  {
    deactivateInstance(instance);
  }

  /**
   * Checks if the the instance in this <em>definition</em> is set. In other words, if the instance handled by this
   * <em>defintion</em> is present
   * 
   * @return <code>true</code>, if the instance in this definition is set
   * @ADDED
   */
  public boolean isSetInstance()
  {
    return instance != null;
  }

  /**
   * Returns whether this definition (and all its nested, referenced defs) was touched since it created its internal
   * instance.
   * 
   * @return true, if this definition was touched since it created its internal instance
   * @ADDED
   */
  public boolean isTouched()
  {
    if (touched)
    {
      return touched = true;
    }
    else
    {
      touched = areReferencedDefsTouched();
    }
    return touched;
  }

  /**
   * Returns whether any referenced def is touched
   * 
   * @return true, if there's any referenced def that has been touched
   */
  protected boolean areReferencedDefsTouched()
  {
    boolean touched = false;
    for (EStructuralFeature structuralFeature : eClass().getEStructuralFeatures())
    {
      if (structuralFeature.getEType().eClass() == Net4jUtilDefsPackage.eINSTANCE.getDef().eClass())
      {
        Object referencedObject = eGet(structuralFeature, true);
        if (referencedObject != null)
        {
          if (structuralFeature.getUpperBound() != -1)
          {
            if (((Def)referencedObject).isTouched())
            {
              touched = true;
              break;
            }
          }
          else
          {
            List<?> referenceList = (List<?>)referencedObject;
            for (Object reference : referenceList)
            {
              if (((Def)reference).isTouched())
              {
                touched = true;
                break;
              }
            }
          }
        }
      }
    }
    return touched;
  }

  /**
   * @ADDED
   */
  protected void wireInstance(Object instance)
  {
    EventUtil.addListener(instance, instanceListener);
  }

  /**
   * @ADDED
   */
  protected void unwireInstance(Object instance)
  {
    EventUtil.removeListener(instance, instanceListener);
  }

  /**
   * @ADDED
   */
  protected void handleDeactivation(Object instance)
  {
    synchronized (this)
    {
      unwireInstance(instance);
      this.instance = null;
    }
  }

  /**
   * validates the present definition. Subclasses have to provide an implementation
   * 
   * @throws IllegalStateException
   * @ADDED
   */
  protected void validateDefinition()
  {
  }

  /**
   * Creates a new instance. Subclasses have to provide an implementation
   * 
   * @return a new instance
   * @ADDED
   */
  protected abstract Object createInstance();

} // DefinitionImpl
