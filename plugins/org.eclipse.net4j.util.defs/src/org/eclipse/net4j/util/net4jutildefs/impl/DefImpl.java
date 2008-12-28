/**
 * <copyright>
 * </copyright>
 *
 * $Id: DefImpl.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.util.net4jutildefs.impl;

import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.net4jutildefs.Def;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.net4jutildefs.util.Net4jUtilDefsUtil;
import org.eclipse.net4j.util.net4jutildefs.util.Net4jUtilDefsUtil.IVisitor;

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

  /** The instance. */
  private Object instance;

  private boolean touched;

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

  /**
   * this might be changed to a content adapter across non-containment references
   * 
   * @see Recipe: Subclass EContentAdapter to receive notifications across non-containment references
   * @see http://wiki.eclipse.org/EMF/Recipes#Recipe:_Data_Migration
   */
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
   * @generated
   */
  protected DefImpl()
  {
    super();
    eAdapters().add(touchedAdapter);
  }

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

  private void setInstance()
  {
    validateDefinition();
    instance = createInstance();
    wireInstance(instance);
    activateInstance(instance);
  }

  protected void activateInstance(Object instance)
  {
    LifecycleUtil.activate(instance);
  }

  protected void deactivateInstance()
  {
    LifecycleUtil.deactivate(instance);
  }

  /**
   * <!-- begin-user-doc -->Unsets the instance managed by this Definition. The internal reference to it is cleared and
   * internal listeners to it are removed <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public void unsetInstance()
  {
    deactivateInstance();
  }

  /**
   * Checks if the the instance in this <em>definition</em> is set. In other words, if the instance handled by this
   * <em>defintion</em> is present
   * 
   * @return <code>true</code>, if the instance in this definition is set
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
   */
  public boolean isTouched()
  {
    IVisitor<EStructuralFeature> structuralFeaturesVisitor = new Net4jUtilDefsUtil.IVisitor<EStructuralFeature>()
    {
      public void visit(EStructuralFeature structuralFeature)
      {
        if (structuralFeature.getEType().eClass() == Net4jUtilDefsPackage.eINSTANCE.getDef().eClass())
        {
          Object referencedObject = eGet(structuralFeature, true);
          if (referencedObject != null)
          {
            if (structuralFeature.getUpperBound() != -1)
            {
              touched |= ((Def)referencedObject).isTouched();
            }
            else
            {
              List referenceList = (List)referencedObject;
              for (Object reference : referenceList)
              {
                touched |= ((Def)reference).isTouched();
              }
            }
          }
        }
      }
    };

    Net4jUtilDefsUtil.doForAllStructuralFeatures(structuralFeaturesVisitor, eClass().getEStructuralFeatures());
    return touched;
  }

  protected void wireInstance(Object instance)
  {
    EventUtil.addListener(instance, instanceListener);
  }

  protected void unwireInstance(Object instance)
  {
    EventUtil.removeListener(instance, instanceListener);
  }

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
   */
  protected void validateDefinition()
  {
  }

  /**
   * Creates a new instance. Subclasses have to provide an implementation
   * 
   * @return a new instance
   */
  /**
   * Creates a new instance. Subclasses have to provide an implementation
   * 
   * @return a new instance
   */
  protected abstract Object createInstance();

} // DefinitionImpl
