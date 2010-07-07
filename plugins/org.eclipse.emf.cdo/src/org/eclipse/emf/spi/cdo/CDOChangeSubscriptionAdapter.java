package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class CDOChangeSubscriptionAdapter extends AdapterImpl
{
  private CDOView view;

  private CDOAdapterPolicy policy = new CDOAdapterPolicy()
  {
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return adapter == CDOChangeSubscriptionAdapter.this;
    }
  };

  private Set<CDOObject> notifiers = new HashSet<CDOObject>();

  public CDOChangeSubscriptionAdapter(CDOView view)
  {
    this.view = view;
    view.options().addChangeSubscriptionPolicy(policy);
  }

  public void dispose()
  {
    reset();
    view.options().removeChangeSubscriptionPolicy(policy);
    view = null;
  }

  public CDOView getView()
  {
    return view;
  }

  public Set<CDOObject> getNotifiers()
  {
    return notifiers;
  }

  public void attach(CDOObject notifier)
  {
    if (notifiers.add(notifier))
    {
      notifier.eAdapters().add(this);
    }
  }

  public void reset()
  {
    for (CDOObject notifier : notifiers)
    {
      notifier.eAdapters().remove(this);
    }

    notifiers.clear();
  }
}
