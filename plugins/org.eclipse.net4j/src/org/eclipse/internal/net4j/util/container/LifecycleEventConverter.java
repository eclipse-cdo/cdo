package org.eclipse.internal.net4j.util.container;

import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;

import org.eclipse.internal.net4j.util.event.Notifier;

/**
 * Is registered with each {@link IChannel} of this {@link IConnector}.
 * <p>
 * 
 * @author Eike Stepper
 */
public class LifecycleEventConverter<E> implements IListener
{
  private Notifier owner;

  public LifecycleEventConverter(Notifier owner)
  {
    this.owner = owner;
  }

  public INotifier getOwner()
  {
    return owner;
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof ILifecycleEvent)
    {
      ILifecycleEvent e = (ILifecycleEvent)event;
      switch (e.getKind())
      {
      case ACTIVATED:
        added(e);
        fireContainerEvent(e, IContainerDelta.Kind.ADDED);
        return;

      case DEACTIVATED:
        removed(e);
        fireContainerEvent(e, IContainerDelta.Kind.REMOVED);
        return;
      }
    }

    owner.fireEvent(event);
  }

  protected void added(ILifecycleEvent e)
  {
  }

  protected void removed(ILifecycleEvent e)
  {
  }

  private void fireContainerEvent(ILifecycleEvent e, IContainerDelta.Kind kind)
  {
    ContainerEvent<E> containerEvent = new ContainerEvent<E>((IContainer<E>)owner);
    containerEvent.addDelta(new ContainerDelta<E>((E)e.getLifecycle(), kind));
    owner.fireEvent(containerEvent);
  }
}