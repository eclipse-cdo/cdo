package org.eclipse.internal.net4j.util.container;

import static org.eclipse.net4j.util.container.IContainerDelta.Kind.ADDED;
import static org.eclipse.net4j.util.container.IContainerDelta.Kind.REMOVED;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.IContainerEventVisitor.Filtered;

import org.eclipse.internal.net4j.util.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ContainerEvent<E> extends Event implements IContainerEvent<E>
{
  private static final long serialVersionUID = 1L;

  private List<IContainerDelta<E>> deltas;

  public ContainerEvent(IContainer<E> container)
  {
    super(container);
    deltas = new ArrayList();
  }

  public ContainerEvent(IContainer<E> container, List<IContainerDelta<E>> deltas)
  {
    super(container);
    this.deltas = deltas;
  }

  public IContainer<E> getContainer()
  {
    return (IContainer<E>)getSource();
  }

  public IContainerDelta<E>[] getDeltas()
  {
    return deltas.toArray(new IContainerDelta[deltas.size()]);
  }

  public IContainerDelta<E> getDelta() throws IllegalStateException
  {
    if (deltas.size() != 1)
    {
      throw new IllegalStateException("deltas.size() != 1");
    }

    return deltas.get(0);
  }

  public E getDeltaElement() throws IllegalStateException
  {
    return getDelta().getElement();
  }

  public Kind getDeltaKind() throws IllegalStateException
  {
    return getDelta().getKind();
  }

  public boolean isEmpty()
  {
    return deltas.isEmpty();
  }

  public void addDelta(E element, Kind kind)
  {
    addDelta(new ContainerDelta(element, kind));
  }

  public void addDelta(IContainerDelta<E> delta)
  {
    deltas.add(delta);
  }

  public void accept(IContainerEventVisitor<E> visitor)
  {
    for (IContainerDelta<E> delta : deltas)
    {
      E element = delta.getElement();

      boolean filtered = true;
      if (visitor instanceof Filtered)
      {
        filtered = ((Filtered)visitor).filter(element);
      }

      if (filtered)
      {
        switch (delta.getKind())
        {
        case ADDED:
          visitor.added(element);
          break;
        case REMOVED:
          visitor.removed(element);
          break;
        }
      }
    }
  }
}
