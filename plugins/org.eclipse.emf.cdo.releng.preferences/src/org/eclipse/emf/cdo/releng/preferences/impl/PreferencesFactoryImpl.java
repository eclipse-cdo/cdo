/**
 */
package org.eclipse.emf.cdo.releng.preferences.impl;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesFactory;
import org.eclipse.emf.cdo.releng.preferences.PreferencesPackage;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PreferencesFactoryImpl extends EFactoryImpl implements PreferencesFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static PreferencesFactory init()
  {
    try
    {
      PreferencesFactory thePreferencesFactory = (PreferencesFactory)EPackage.Registry.INSTANCE.getEFactory(PreferencesPackage.eNS_URI);
      if (thePreferencesFactory != null)
      {
        return thePreferencesFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new PreferencesFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferencesFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case PreferencesPackage.PREFERENCE_NODE: return createPreferenceNode();
      case PreferencesPackage.PROPERTY: return createProperty();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case PreferencesPackage.ESCAPED_STRING:
        return createEscapedStringFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case PreferencesPackage.ESCAPED_STRING:
        return convertEscapedStringToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferenceNode createPreferenceNode()
  {
    PreferenceNodeImpl preferenceNode = new PreferenceNodeImpl();
    return preferenceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Property createProperty()
  {
    PropertyImpl property = new PropertyImpl();
    return property;
  }

  private static final String[] ESCAPES = { "\\000", "\\001", "\\002", "\\003", "\\004", "\\005", "\\006", "\\007",
      "\\010", "\\t", "\\n", "\\013", "\\014", "\\r", "\\016", "\\017", "\\020", "\\021", "\\022", "\\023", "\\024",
      "\\025", "\\026", "\\027", "\\030", "\\031", "\\032", "\\033", "\\034", "\\035", "\\036", "\\037" };

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String createEscapedStringFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    StringBuilder result = new StringBuilder();
    for (int i = 0, length = initialValue.length(); i < length; ++i)
    {
      char c = initialValue.charAt(i);
      if (c == '\\')
      {
        if (++i < length)
        {
          c = initialValue.charAt(i);
          if (c == 't')
          {
            result.append('\t');
            continue;
          }
          else if (c == 'r')
          {
            result.append('\r');
            continue;
          }
          else if (c == 'n')
          {
            result.append('\n');
            continue;
          }
          else if (c == '\\')
          {
            result.append('\\');
            continue;
          }
          else if (i + 2 < length && c >= '0' && c <= '7' && initialValue.charAt(i + 1) >= '0'
              && initialValue.charAt(i + 1) <= '7' && initialValue.charAt(i + 2) >= '0'
              && initialValue.charAt(i + 2) <= '7')
          {
            result.append((char)Integer.parseInt(initialValue.substring(i, i + 3), 8));
            i += 2;
            continue;
          }
        }
      }
      result.append(c);
    }
    return result.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertEscapedStringToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }

    String initialValue = instanceValue.toString();
    StringBuilder result = new StringBuilder();
    for (int i = 0, length = initialValue.length(); i < length; ++i)
    {
      char c = initialValue.charAt(i);
      if (c < ESCAPES.length)
      {
        result.append(ESCAPES[c]);
      }
      else if (c == '\\')
      {
        result.append("\\\\");
      }
      else
      {
        result.append(c);
      }
    }
    return result.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferencesPackage getPreferencesPackage()
  {
    return (PreferencesPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static PreferencesPackage getPackage()
  {
    return PreferencesPackage.eINSTANCE;
  }

} // PreferencesFactoryImpl
