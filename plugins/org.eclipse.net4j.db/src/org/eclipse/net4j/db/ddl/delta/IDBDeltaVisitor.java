/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.ddl.delta;

import org.eclipse.net4j.db.ddl.delta.IDBDelta.ChangeKind;

/**
 * @since 4.2
 * @author Eike Stepper
 */
public interface IDBDeltaVisitor
{
  public void visit(IDBSchemaDelta delta);

  public void visit(IDBTableDelta delta);

  public void visit(IDBFieldDelta delta);

  public void visit(IDBIndexDelta delta);

  public void visit(IDBIndexFieldDelta delta);

  public void visit(IDBPropertyDelta<?> delta);

  /**
   * @author Eike Stepper
   */
  public static class Default implements IDBDeltaVisitor
  {
    protected void visitDefault(IDBDelta delta)
    {
    }

    public void visit(IDBSchemaDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBSchemaDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBSchemaDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBSchemaDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBTableDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBTableDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBTableDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBTableDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBFieldDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBFieldDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBIndexDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBIndexDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBIndexDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBIndexDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBIndexFieldDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBIndexFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBIndexFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBIndexFieldDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBPropertyDelta<?> delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBPropertyDelta<?> delta)
    {
      visitDefault(delta);

    }

    protected void removed(IDBPropertyDelta<?> delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBPropertyDelta<?> delta)
    {
      visitDefault(delta);
    }
  }
}
