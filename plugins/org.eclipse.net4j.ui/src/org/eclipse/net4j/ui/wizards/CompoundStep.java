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
package org.eclipse.net4j.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Eike Stepper
 */
public abstract class CompoundStep extends Step implements List<Step>
{
  private List<Step> children = new ArrayList(0);

  public CompoundStep()
  {
  }

  public void add(int index, Step child)
  {
    child.setParent(this);
    children.add(index, child);
  }

  public boolean add(Step child)
  {
    child.setParent(this);
    return children.add(child);
  }

  public boolean addAll(Collection<? extends Step> c)
  {
    for (Step step : c)
    {
      step.setParent(this);
    }

    return children.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends Step> c)
  {
    for (Step step : c)
    {
      step.setParent(this);
    }

    return children.addAll(index, c);
  }

  public void clear()
  {
    for (Step step : this)
    {
      step.setParent(null);
    }

    children.clear();
  }

  public boolean contains(Object o)
  {
    return children.contains(o);
  }

  public boolean containsAll(Collection<?> c)
  {
    return children.containsAll(c);
  }

  public boolean equals(Object o)
  {
    return children.equals(o);
  }

  public Step get(int index)
  {
    return children.get(index);
  }

  public Step getFirst()
  {
    return children.isEmpty() ? null : children.get(0);
  }

  public Step getLast()
  {
    return children.isEmpty() ? null : children.get(children.size() - 1);
  }

  public int hashCode()
  {
    return children.hashCode();
  }

  public int indexOf(Object o)
  {
    return children.indexOf(o);
  }

  public boolean isEmpty()
  {
    return children.isEmpty();
  }

  public Iterator<Step> iterator()
  {
    return new Itr(children.iterator());
  }

  public int lastIndexOf(Object o)
  {
    return children.lastIndexOf(o);
  }

  public ListIterator<Step> listIterator()
  {
    return new ListItr(children.listIterator());
  }

  public ListIterator<Step> listIterator(int index)
  {
    return new ListItr(children.listIterator(index));
  }

  public Step remove(int index)
  {
    return children.remove(index);
  }

  public boolean remove(Object o)
  {
    boolean removed = children.remove(o);
    if (removed)
    {
      ((Step)o).setParent(null);
    }

    return removed;
  }

  public boolean removeAll(Collection<?> c)
  {
    for (Object o : c)
    {
      if (c instanceof Step)
      {
        ((Step)o).setParent(null);
      }
    }

    return children.removeAll(c);
  }

  public boolean retainAll(Collection<?> c)
  {
    for (Step step : this)
    {
      if (!c.contains(step))
      {
        step.setParent(null);
      }
    }

    return children.retainAll(c);
  }

  public Step set(int index, Step element)
  {
    Step old = children.set(index, element);
    if (old != null)
    {
      old.setParent(null);
    }

    return old;
  }

  public int size()
  {
    return children.size();
  }

  public List<Step> subList(int fromIndex, int toIndex)
  {
    return children.subList(fromIndex, toIndex);
  }

  public <T> T[] toArray(T[] a)
  {
    return children.toArray(a);
  }

  public Object[] toArray()
  {
    return children.toArray();
  }

  @Override
  public void accept(IStepVisitor visitor)
  {
    super.accept(visitor);
    for (Step step : this)
    {
      step.accept(visitor);
    }
  }

  @Override
  public boolean isFinished()
  {
    return false;
  }

  @Override
  public boolean isReady()
  {
    return false;
  }

  @Override
  void setWizard(SteppingWizard wizard)
  {
    if (isEmpty())
    {
      throw new IllegalStateException("isEmpty()");
    }

    super.setWizard(wizard);
    for (Step child : children)
    {
      child.setWizard(wizard);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Itr implements Iterator<Step>
  {
    private Iterator<Step> delegate;

    private Step last;

    private Itr(Iterator<Step> delegate)
    {
      this.delegate = delegate;
    }

    public boolean hasNext()
    {
      return delegate.hasNext();
    }

    public Step next()
    {
      return last = delegate.next();
    }

    public void remove()
    {
      delegate.remove();
      last.setParent(null);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ListItr implements ListIterator<Step>
  {
    private final ListIterator<Step> delegate;

    private Step last;

    private ListItr(ListIterator<Step> delegate)
    {
      this.delegate = delegate;
    }

    public boolean hasNext()
    {
      return delegate.hasNext();
    }

    public Step next()
    {
      return last = delegate.next();
    }

    public void remove()
    {
      delegate.remove();
      last.setParent(null);
    }

    public void add(Step child)
    {
      child.setParent(CompoundStep.this);
      delegate.add(child);
    }

    public boolean hasPrevious()
    {
      return delegate.hasPrevious();
    }

    public int nextIndex()
    {
      return delegate.nextIndex();
    }

    public Step previous()
    {
      return last = delegate.previous();
    }

    public int previousIndex()
    {
      return delegate.previousIndex();
    }

    public void set(Step child)
    {
      if (last != null)
      {
        last.setParent(null);
      }

      last = child;
      last.setParent(CompoundStep.this);
    }
  }
}
