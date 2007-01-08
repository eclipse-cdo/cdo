/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.impl;


import org.eclipse.emf.cdo.client.impl.CDOPersistentImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import testmodel1.Author;
import testmodel1.Book;
import testmodel1.TestModel1Package;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Book</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link testmodel1.impl.BookImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link testmodel1.impl.BookImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BookImpl extends CDOPersistentImpl implements Book
{
  /**
   * The cached value of the '{@link #getAuthor() <em>Author</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAuthor()
   * @generated
   * @ordered
   */
  protected Author author = null;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BookImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return TestModel1Package.Literals.BOOK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Author getAuthor()
  {
    cdoLoad();
    if (author != null && author.eIsProxy())
    {
      InternalEObject oldAuthor = (InternalEObject) author;
      author = (Author) eResolveProxy(oldAuthor);
      if (author != oldAuthor)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, TestModel1Package.BOOK__AUTHOR,
              oldAuthor, author));
      }
    }
    return author;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Author basicGetAuthor()
  {
    cdoLoad();
    return author;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetAuthor(Author newAuthor, NotificationChain msgs)
  {
    cdoLoad();
    Author oldAuthor = author;
    author = newAuthor;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          TestModel1Package.BOOK__AUTHOR, oldAuthor, newAuthor);
      if (msgs == null)
        msgs = notification;
      else
        msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAuthor(Author newAuthor)
  {
    cdoLoad();
    if (newAuthor != author)
    {
      NotificationChain msgs = null;
      if (author != null)
        msgs = ((InternalEObject) author).eInverseRemove(this, TestModel1Package.AUTHOR__BOOKS,
            Author.class, msgs);
      if (newAuthor != null)
        msgs = ((InternalEObject) newAuthor).eInverseAdd(this, TestModel1Package.AUTHOR__BOOKS,
            Author.class, msgs);
      msgs = basicSetAuthor(newAuthor, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, TestModel1Package.BOOK__AUTHOR,
          newAuthor, newAuthor));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    cdoLoad();
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    cdoLoad();
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, TestModel1Package.BOOK__NAME, oldName,
          name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
      NotificationChain msgs)
  {
    switch (featureID)
    {
      case TestModel1Package.BOOK__AUTHOR:
        if (author != null)
          msgs = ((InternalEObject) author).eInverseRemove(this, TestModel1Package.AUTHOR__BOOKS,
              Author.class, msgs);
        return basicSetAuthor((Author) otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
      NotificationChain msgs)
  {
    switch (featureID)
    {
      case TestModel1Package.BOOK__AUTHOR:
        return basicSetAuthor(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case TestModel1Package.BOOK__AUTHOR:
        if (resolve) return getAuthor();
        return basicGetAuthor();
      case TestModel1Package.BOOK__NAME:
        return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case TestModel1Package.BOOK__AUTHOR:
        setAuthor((Author) newValue);
        return;
      case TestModel1Package.BOOK__NAME:
        setName((String) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case TestModel1Package.BOOK__AUTHOR:
        setAuthor((Author) null);
        return;
      case TestModel1Package.BOOK__NAME:
        setName(NAME_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case TestModel1Package.BOOK__AUTHOR:
        return author != null;
      case TestModel1Package.BOOK__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //BookImpl