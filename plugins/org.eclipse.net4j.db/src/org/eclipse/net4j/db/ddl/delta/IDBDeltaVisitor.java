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
import org.eclipse.net4j.db.ddl.delta.IDBDelta.DeltaType;
import org.eclipse.net4j.internal.db.ddl.delta.DBDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBDeltaWithProperties;
import org.eclipse.net4j.internal.db.ddl.delta.DBFieldDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBIndexDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBIndexFieldDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBPropertyDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBSchemaDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBTableDelta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public void visit(IDBSchemaDelta delta)
    {
      if (handle(delta))
      {
        ChangeKind changeKind = delta.getChangeKind();
        switch (changeKind)
        {
        case ADD:
          added(delta);
          break;

        case REMOVE:
          removed(delta);
          break;

        case CHANGE:
          changed(delta);
          break;

        default:
          illegalChangeKind(changeKind);
        }
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
      if (handle(delta))
      {
        ChangeKind changeKind = delta.getChangeKind();
        switch (changeKind)
        {
        case ADD:
          added(delta);
          break;

        case REMOVE:
          removed(delta);
          break;

        case CHANGE:
          changed(delta);
          break;

        default:
          illegalChangeKind(changeKind);
        }
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
      if (handle(delta))
      {
        ChangeKind changeKind = delta.getChangeKind();
        switch (changeKind)
        {
        case ADD:
          added(delta);
          break;

        case REMOVE:
          removed(delta);
          break;

        case CHANGE:
          changed(delta);
          break;

        default:
          illegalChangeKind(changeKind);
        }
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
      if (handle(delta))
      {
        ChangeKind changeKind = delta.getChangeKind();
        switch (changeKind)
        {
        case ADD:
          added(delta);
          break;

        case REMOVE:
          removed(delta);
          break;

        case CHANGE:
          changed(delta);
          break;

        default:
          illegalChangeKind(changeKind);
        }
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
      if (handle(delta))
      {
        ChangeKind changeKind = delta.getChangeKind();
        switch (changeKind)
        {
        case ADD:
          added(delta);
          break;

        case REMOVE:
          removed(delta);
          break;

        case CHANGE:
          changed(delta);
          break;

        default:
          illegalChangeKind(changeKind);
        }
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
      if (handle(delta))
      {
        ChangeKind changeKind = delta.getChangeKind();
        switch (changeKind)
        {
        case ADD:
          added(delta);
          break;

        case REMOVE:
          removed(delta);
          break;

        case CHANGE:
          changed(delta);
          break;

        default:
          illegalChangeKind(changeKind);
        }
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

    protected void visitDefault(IDBDelta delta)
    {
    }

    protected boolean handle(IDBDelta delta)
    {
      return true;
    }

    private void illegalChangeKind(ChangeKind changeKind)
    {
      throw new IllegalStateException("Illegal change kind: " + changeKind);
    }
  }

  /**
     * @author Eike Stepper
     */
  public static class Filter extends Default
  {
    public static final Map<ChangeKind, Boolean> DEFAULT_POLICY = createPolicy(null, null);

    public static final Boolean ALLOWED = Boolean.TRUE;

    public static final Boolean FORBIDDEN = Boolean.FALSE;

    private Map<ChangeKind, Boolean> policy;

    public Filter()
    {
      this(null);
    }

    public Filter(Map<ChangeKind, Boolean> policy)
    {
      this.policy = policy == null ? DEFAULT_POLICY : policy;
    }

    public Filter(Set<ChangeKind> ignoredChanges, Set<ChangeKind> forbiddenChanges)
    {
      this(createPolicy(ignoredChanges, forbiddenChanges));
    }

    public final Map<ChangeKind, Boolean> getPolicy()
    {
      return policy;
    }

    @Override
    protected void added(IDBSchemaDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void added(IDBTableDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void added(IDBFieldDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void added(IDBIndexDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void added(IDBIndexFieldDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void added(IDBPropertyDelta<?> delta)
    {
      doVisit(delta);
    }

    @Override
    protected void removed(IDBSchemaDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void removed(IDBTableDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void removed(IDBFieldDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void removed(IDBIndexDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void removed(IDBIndexFieldDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void removed(IDBPropertyDelta<?> delta)
    {
      doVisit(delta);
    }

    @Override
    protected void changed(IDBSchemaDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void changed(IDBTableDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void changed(IDBFieldDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void changed(IDBIndexDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void changed(IDBIndexFieldDelta delta)
    {
      doVisit(delta);
    }

    @Override
    protected void changed(IDBPropertyDelta<?> delta)
    {
      doVisit(delta);
    }

    protected void doVisit(IDBSchemaDelta delta)
    {
      visitDefault(delta);
    }

    protected void doVisit(IDBTableDelta delta)
    {
      visitDefault(delta);
    }

    protected void doVisit(IDBFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void doVisit(IDBIndexDelta delta)
    {
      visitDefault(delta);
    }

    protected void doVisit(IDBIndexFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void doVisit(IDBPropertyDelta<?> delta)
    {
      visitDefault(delta);
    }

    @Override
    protected boolean handle(IDBDelta delta)
    {
      Boolean deltaPolicy = policy.get(delta.getChangeKind());
      if (deltaPolicy == FORBIDDEN)
      {
        throw new ForbiddenChangeException(delta);
      }

      return deltaPolicy == ALLOWED;
    }

    public static Map<ChangeKind, Boolean> createPolicy(Set<ChangeKind> ignoredChanges, Set<ChangeKind> forbiddenChanges)
    {
      if (ignoredChanges == null)
      {
        ignoredChanges = Collections.emptySet();
      }

      if (forbiddenChanges == null)
      {
        forbiddenChanges = Collections.emptySet();
      }

      Map<ChangeKind, Boolean> policy = new HashMap<IDBDelta.ChangeKind, Boolean>();
      for (ChangeKind changeKind : ChangeKind.values())
      {
        if (!ignoredChanges.contains(changeKind))
        {
          policy.put(changeKind, ALLOWED);
        }
      }

      for (ChangeKind changeKind : forbiddenChanges)
      {
        policy.put(changeKind, FORBIDDEN);
      }

      return policy;
    }

    /**
     * @author Eike Stepper
     */
    public static final class ForbiddenChangeException extends RuntimeException
    {
      private static final long serialVersionUID = 1L;

      private final IDBDelta delta;

      public ForbiddenChangeException(IDBDelta delta)
      {
        super("Forbidden change: " + delta);
        this.delta = delta;
      }

      public IDBDelta getDelta()
      {
        return delta;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Copier extends Filter
  {
    private DBSchemaDelta result;

    public Copier()
    {
    }

    public Copier(Map<ChangeKind, Boolean> policies)
    {
      super(policies);
    }

    public Copier(Set<ChangeKind> ignoredChanges, Set<ChangeKind> forbiddenChanges)
    {
      super(ignoredChanges, forbiddenChanges);
    }

    public final IDBSchemaDelta getResult()
    {
      return result;
    }

    @Override
    protected void doVisit(IDBSchemaDelta delta)
    {
      result = new DBSchemaDelta(delta.getName(), delta.getChangeKind());
    }

    @Override
    protected void doVisit(IDBTableDelta delta)
    {
      DBTableDelta copy = new DBTableDelta(result, delta.getName(), delta.getChangeKind());
      result.addTableDelta(copy);
    }

    @Override
    protected void doVisit(IDBFieldDelta delta)
    {
      DBTableDelta parentCopy = getParentCopy(delta);
      DBFieldDelta copy = new DBFieldDelta(parentCopy, delta.getName(), delta.getChangeKind());
      parentCopy.addFieldDelta(copy);
    }

    @Override
    protected void doVisit(IDBIndexDelta delta)
    {
      DBTableDelta parentCopy = getParentCopy(delta);
      DBIndexDelta copy = new DBIndexDelta(parentCopy, delta.getName(), delta.getChangeKind());
      parentCopy.addIndexDelta(copy);
    }

    @Override
    protected void doVisit(IDBIndexFieldDelta delta)
    {
      DBIndexDelta parentCopy = getParentCopy(delta);
      DBIndexFieldDelta copy = new DBIndexFieldDelta(parentCopy, delta.getName(), delta.getChangeKind());
      parentCopy.addIndexFieldDelta(copy);
    }

    @Override
    protected void doVisit(IDBPropertyDelta<?> delta)
    {
      DBDeltaWithProperties parentCopy = getParentCopy(delta);
      @SuppressWarnings({ "rawtypes", "unchecked" })
      DBPropertyDelta<?> copy = new DBPropertyDelta(parentCopy, delta.getName(), delta.getType(), delta.getValue(),
          delta.getOldValue());
      parentCopy.addPropertyDelta(copy);
    }

    @SuppressWarnings("unchecked")
    private <T extends DBDelta> T getParentCopy(IDBDelta delta)
    {
      if (result == null)
      {
        throw new IllegalStateException("Copier can only be accepted by schema deltas");
      }

      DBDelta parent = (DBDelta)delta.getParent();
      DeltaType deltaType = parent.getDeltaType();
      switch (deltaType)
      {
      case SCHEMA:
        return (T)result;

      case TABLE:
        return (T)result.getTableDelta(parent.getName());

      case FIELD:
        return (T)result.getTableDelta(parent.getParent().getName()).getFieldDelta(parent.getName());

      case INDEX:
        return (T)result.getTableDelta(parent.getParent().getName()).getIndexDelta(parent.getName());

      case INDEX_FIELD:
        return (T)result.getTableDelta(parent.getParent().getParent().getName())
            .getIndexDelta(parent.getParent().getName()).getIndexFieldDelta(parent.getName());

      default:
        throw new IllegalStateException("Illegal delta type: " + deltaType);
      }
    }
  }
}
