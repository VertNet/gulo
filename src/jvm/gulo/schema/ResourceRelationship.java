/**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package gulo.schema;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRelationship implements org.apache.thrift.TBase<ResourceRelationship, ResourceRelationship._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ResourceRelationship");

  private static final org.apache.thrift.protocol.TField RELATED_RESOURCE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("relatedResourceID", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField RELATIONSHIP_ACCORDING_TO_FIELD_DESC = new org.apache.thrift.protocol.TField("relationshipAccordingTo", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField RELATIONSHIP_ESTABLISHED_DATE_FIELD_DESC = new org.apache.thrift.protocol.TField("relationshipEstablishedDate", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField RELATIONSHIP_OF_RESOURCE_FIELD_DESC = new org.apache.thrift.protocol.TField("relationshipOfResource", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField RELATIONSHIP_REMARKS_FIELD_DESC = new org.apache.thrift.protocol.TField("relationshipRemarks", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField RESOURCE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("resourceID", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField RESOURCE_RELATIONSHIP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("resourceRelationshipID", org.apache.thrift.protocol.TType.STRING, (short)7);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ResourceRelationshipStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ResourceRelationshipTupleSchemeFactory());
  }

  public String relatedResourceID; // required
  public String relationshipAccordingTo; // required
  public String relationshipEstablishedDate; // required
  public String relationshipOfResource; // required
  public String relationshipRemarks; // required
  public String resourceID; // required
  public String resourceRelationshipID; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RELATED_RESOURCE_ID((short)1, "relatedResourceID"),
    RELATIONSHIP_ACCORDING_TO((short)2, "relationshipAccordingTo"),
    RELATIONSHIP_ESTABLISHED_DATE((short)3, "relationshipEstablishedDate"),
    RELATIONSHIP_OF_RESOURCE((short)4, "relationshipOfResource"),
    RELATIONSHIP_REMARKS((short)5, "relationshipRemarks"),
    RESOURCE_ID((short)6, "resourceID"),
    RESOURCE_RELATIONSHIP_ID((short)7, "resourceRelationshipID");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // RELATED_RESOURCE_ID
          return RELATED_RESOURCE_ID;
        case 2: // RELATIONSHIP_ACCORDING_TO
          return RELATIONSHIP_ACCORDING_TO;
        case 3: // RELATIONSHIP_ESTABLISHED_DATE
          return RELATIONSHIP_ESTABLISHED_DATE;
        case 4: // RELATIONSHIP_OF_RESOURCE
          return RELATIONSHIP_OF_RESOURCE;
        case 5: // RELATIONSHIP_REMARKS
          return RELATIONSHIP_REMARKS;
        case 6: // RESOURCE_ID
          return RESOURCE_ID;
        case 7: // RESOURCE_RELATIONSHIP_ID
          return RESOURCE_RELATIONSHIP_ID;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RELATED_RESOURCE_ID, new org.apache.thrift.meta_data.FieldMetaData("relatedResourceID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RELATIONSHIP_ACCORDING_TO, new org.apache.thrift.meta_data.FieldMetaData("relationshipAccordingTo", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RELATIONSHIP_ESTABLISHED_DATE, new org.apache.thrift.meta_data.FieldMetaData("relationshipEstablishedDate", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RELATIONSHIP_OF_RESOURCE, new org.apache.thrift.meta_data.FieldMetaData("relationshipOfResource", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RELATIONSHIP_REMARKS, new org.apache.thrift.meta_data.FieldMetaData("relationshipRemarks", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RESOURCE_ID, new org.apache.thrift.meta_data.FieldMetaData("resourceID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RESOURCE_RELATIONSHIP_ID, new org.apache.thrift.meta_data.FieldMetaData("resourceRelationshipID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ResourceRelationship.class, metaDataMap);
  }

  public ResourceRelationship() {
  }

  public ResourceRelationship(
    String relatedResourceID,
    String relationshipAccordingTo,
    String relationshipEstablishedDate,
    String relationshipOfResource,
    String relationshipRemarks,
    String resourceID,
    String resourceRelationshipID)
  {
    this();
    this.relatedResourceID = relatedResourceID;
    this.relationshipAccordingTo = relationshipAccordingTo;
    this.relationshipEstablishedDate = relationshipEstablishedDate;
    this.relationshipOfResource = relationshipOfResource;
    this.relationshipRemarks = relationshipRemarks;
    this.resourceID = resourceID;
    this.resourceRelationshipID = resourceRelationshipID;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ResourceRelationship(ResourceRelationship other) {
    if (other.isSetRelatedResourceID()) {
      this.relatedResourceID = other.relatedResourceID;
    }
    if (other.isSetRelationshipAccordingTo()) {
      this.relationshipAccordingTo = other.relationshipAccordingTo;
    }
    if (other.isSetRelationshipEstablishedDate()) {
      this.relationshipEstablishedDate = other.relationshipEstablishedDate;
    }
    if (other.isSetRelationshipOfResource()) {
      this.relationshipOfResource = other.relationshipOfResource;
    }
    if (other.isSetRelationshipRemarks()) {
      this.relationshipRemarks = other.relationshipRemarks;
    }
    if (other.isSetResourceID()) {
      this.resourceID = other.resourceID;
    }
    if (other.isSetResourceRelationshipID()) {
      this.resourceRelationshipID = other.resourceRelationshipID;
    }
  }

  public ResourceRelationship deepCopy() {
    return new ResourceRelationship(this);
  }

  @Override
  public void clear() {
    this.relatedResourceID = null;
    this.relationshipAccordingTo = null;
    this.relationshipEstablishedDate = null;
    this.relationshipOfResource = null;
    this.relationshipRemarks = null;
    this.resourceID = null;
    this.resourceRelationshipID = null;
  }

  public String getRelatedResourceID() {
    return this.relatedResourceID;
  }

  public ResourceRelationship setRelatedResourceID(String relatedResourceID) {
    this.relatedResourceID = relatedResourceID;
    return this;
  }

  public void unsetRelatedResourceID() {
    this.relatedResourceID = null;
  }

  /** Returns true if field relatedResourceID is set (has been assigned a value) and false otherwise */
  public boolean isSetRelatedResourceID() {
    return this.relatedResourceID != null;
  }

  public void setRelatedResourceIDIsSet(boolean value) {
    if (!value) {
      this.relatedResourceID = null;
    }
  }

  public String getRelationshipAccordingTo() {
    return this.relationshipAccordingTo;
  }

  public ResourceRelationship setRelationshipAccordingTo(String relationshipAccordingTo) {
    this.relationshipAccordingTo = relationshipAccordingTo;
    return this;
  }

  public void unsetRelationshipAccordingTo() {
    this.relationshipAccordingTo = null;
  }

  /** Returns true if field relationshipAccordingTo is set (has been assigned a value) and false otherwise */
  public boolean isSetRelationshipAccordingTo() {
    return this.relationshipAccordingTo != null;
  }

  public void setRelationshipAccordingToIsSet(boolean value) {
    if (!value) {
      this.relationshipAccordingTo = null;
    }
  }

  public String getRelationshipEstablishedDate() {
    return this.relationshipEstablishedDate;
  }

  public ResourceRelationship setRelationshipEstablishedDate(String relationshipEstablishedDate) {
    this.relationshipEstablishedDate = relationshipEstablishedDate;
    return this;
  }

  public void unsetRelationshipEstablishedDate() {
    this.relationshipEstablishedDate = null;
  }

  /** Returns true if field relationshipEstablishedDate is set (has been assigned a value) and false otherwise */
  public boolean isSetRelationshipEstablishedDate() {
    return this.relationshipEstablishedDate != null;
  }

  public void setRelationshipEstablishedDateIsSet(boolean value) {
    if (!value) {
      this.relationshipEstablishedDate = null;
    }
  }

  public String getRelationshipOfResource() {
    return this.relationshipOfResource;
  }

  public ResourceRelationship setRelationshipOfResource(String relationshipOfResource) {
    this.relationshipOfResource = relationshipOfResource;
    return this;
  }

  public void unsetRelationshipOfResource() {
    this.relationshipOfResource = null;
  }

  /** Returns true if field relationshipOfResource is set (has been assigned a value) and false otherwise */
  public boolean isSetRelationshipOfResource() {
    return this.relationshipOfResource != null;
  }

  public void setRelationshipOfResourceIsSet(boolean value) {
    if (!value) {
      this.relationshipOfResource = null;
    }
  }

  public String getRelationshipRemarks() {
    return this.relationshipRemarks;
  }

  public ResourceRelationship setRelationshipRemarks(String relationshipRemarks) {
    this.relationshipRemarks = relationshipRemarks;
    return this;
  }

  public void unsetRelationshipRemarks() {
    this.relationshipRemarks = null;
  }

  /** Returns true if field relationshipRemarks is set (has been assigned a value) and false otherwise */
  public boolean isSetRelationshipRemarks() {
    return this.relationshipRemarks != null;
  }

  public void setRelationshipRemarksIsSet(boolean value) {
    if (!value) {
      this.relationshipRemarks = null;
    }
  }

  public String getResourceID() {
    return this.resourceID;
  }

  public ResourceRelationship setResourceID(String resourceID) {
    this.resourceID = resourceID;
    return this;
  }

  public void unsetResourceID() {
    this.resourceID = null;
  }

  /** Returns true if field resourceID is set (has been assigned a value) and false otherwise */
  public boolean isSetResourceID() {
    return this.resourceID != null;
  }

  public void setResourceIDIsSet(boolean value) {
    if (!value) {
      this.resourceID = null;
    }
  }

  public String getResourceRelationshipID() {
    return this.resourceRelationshipID;
  }

  public ResourceRelationship setResourceRelationshipID(String resourceRelationshipID) {
    this.resourceRelationshipID = resourceRelationshipID;
    return this;
  }

  public void unsetResourceRelationshipID() {
    this.resourceRelationshipID = null;
  }

  /** Returns true if field resourceRelationshipID is set (has been assigned a value) and false otherwise */
  public boolean isSetResourceRelationshipID() {
    return this.resourceRelationshipID != null;
  }

  public void setResourceRelationshipIDIsSet(boolean value) {
    if (!value) {
      this.resourceRelationshipID = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case RELATED_RESOURCE_ID:
      if (value == null) {
        unsetRelatedResourceID();
      } else {
        setRelatedResourceID((String)value);
      }
      break;

    case RELATIONSHIP_ACCORDING_TO:
      if (value == null) {
        unsetRelationshipAccordingTo();
      } else {
        setRelationshipAccordingTo((String)value);
      }
      break;

    case RELATIONSHIP_ESTABLISHED_DATE:
      if (value == null) {
        unsetRelationshipEstablishedDate();
      } else {
        setRelationshipEstablishedDate((String)value);
      }
      break;

    case RELATIONSHIP_OF_RESOURCE:
      if (value == null) {
        unsetRelationshipOfResource();
      } else {
        setRelationshipOfResource((String)value);
      }
      break;

    case RELATIONSHIP_REMARKS:
      if (value == null) {
        unsetRelationshipRemarks();
      } else {
        setRelationshipRemarks((String)value);
      }
      break;

    case RESOURCE_ID:
      if (value == null) {
        unsetResourceID();
      } else {
        setResourceID((String)value);
      }
      break;

    case RESOURCE_RELATIONSHIP_ID:
      if (value == null) {
        unsetResourceRelationshipID();
      } else {
        setResourceRelationshipID((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case RELATED_RESOURCE_ID:
      return getRelatedResourceID();

    case RELATIONSHIP_ACCORDING_TO:
      return getRelationshipAccordingTo();

    case RELATIONSHIP_ESTABLISHED_DATE:
      return getRelationshipEstablishedDate();

    case RELATIONSHIP_OF_RESOURCE:
      return getRelationshipOfResource();

    case RELATIONSHIP_REMARKS:
      return getRelationshipRemarks();

    case RESOURCE_ID:
      return getResourceID();

    case RESOURCE_RELATIONSHIP_ID:
      return getResourceRelationshipID();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case RELATED_RESOURCE_ID:
      return isSetRelatedResourceID();
    case RELATIONSHIP_ACCORDING_TO:
      return isSetRelationshipAccordingTo();
    case RELATIONSHIP_ESTABLISHED_DATE:
      return isSetRelationshipEstablishedDate();
    case RELATIONSHIP_OF_RESOURCE:
      return isSetRelationshipOfResource();
    case RELATIONSHIP_REMARKS:
      return isSetRelationshipRemarks();
    case RESOURCE_ID:
      return isSetResourceID();
    case RESOURCE_RELATIONSHIP_ID:
      return isSetResourceRelationshipID();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ResourceRelationship)
      return this.equals((ResourceRelationship)that);
    return false;
  }

  public boolean equals(ResourceRelationship that) {
    if (that == null)
      return false;

    boolean this_present_relatedResourceID = true && this.isSetRelatedResourceID();
    boolean that_present_relatedResourceID = true && that.isSetRelatedResourceID();
    if (this_present_relatedResourceID || that_present_relatedResourceID) {
      if (!(this_present_relatedResourceID && that_present_relatedResourceID))
        return false;
      if (!this.relatedResourceID.equals(that.relatedResourceID))
        return false;
    }

    boolean this_present_relationshipAccordingTo = true && this.isSetRelationshipAccordingTo();
    boolean that_present_relationshipAccordingTo = true && that.isSetRelationshipAccordingTo();
    if (this_present_relationshipAccordingTo || that_present_relationshipAccordingTo) {
      if (!(this_present_relationshipAccordingTo && that_present_relationshipAccordingTo))
        return false;
      if (!this.relationshipAccordingTo.equals(that.relationshipAccordingTo))
        return false;
    }

    boolean this_present_relationshipEstablishedDate = true && this.isSetRelationshipEstablishedDate();
    boolean that_present_relationshipEstablishedDate = true && that.isSetRelationshipEstablishedDate();
    if (this_present_relationshipEstablishedDate || that_present_relationshipEstablishedDate) {
      if (!(this_present_relationshipEstablishedDate && that_present_relationshipEstablishedDate))
        return false;
      if (!this.relationshipEstablishedDate.equals(that.relationshipEstablishedDate))
        return false;
    }

    boolean this_present_relationshipOfResource = true && this.isSetRelationshipOfResource();
    boolean that_present_relationshipOfResource = true && that.isSetRelationshipOfResource();
    if (this_present_relationshipOfResource || that_present_relationshipOfResource) {
      if (!(this_present_relationshipOfResource && that_present_relationshipOfResource))
        return false;
      if (!this.relationshipOfResource.equals(that.relationshipOfResource))
        return false;
    }

    boolean this_present_relationshipRemarks = true && this.isSetRelationshipRemarks();
    boolean that_present_relationshipRemarks = true && that.isSetRelationshipRemarks();
    if (this_present_relationshipRemarks || that_present_relationshipRemarks) {
      if (!(this_present_relationshipRemarks && that_present_relationshipRemarks))
        return false;
      if (!this.relationshipRemarks.equals(that.relationshipRemarks))
        return false;
    }

    boolean this_present_resourceID = true && this.isSetResourceID();
    boolean that_present_resourceID = true && that.isSetResourceID();
    if (this_present_resourceID || that_present_resourceID) {
      if (!(this_present_resourceID && that_present_resourceID))
        return false;
      if (!this.resourceID.equals(that.resourceID))
        return false;
    }

    boolean this_present_resourceRelationshipID = true && this.isSetResourceRelationshipID();
    boolean that_present_resourceRelationshipID = true && that.isSetResourceRelationshipID();
    if (this_present_resourceRelationshipID || that_present_resourceRelationshipID) {
      if (!(this_present_resourceRelationshipID && that_present_resourceRelationshipID))
        return false;
      if (!this.resourceRelationshipID.equals(that.resourceRelationshipID))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_relatedResourceID = true && (isSetRelatedResourceID());
    builder.append(present_relatedResourceID);
    if (present_relatedResourceID)
      builder.append(relatedResourceID);

    boolean present_relationshipAccordingTo = true && (isSetRelationshipAccordingTo());
    builder.append(present_relationshipAccordingTo);
    if (present_relationshipAccordingTo)
      builder.append(relationshipAccordingTo);

    boolean present_relationshipEstablishedDate = true && (isSetRelationshipEstablishedDate());
    builder.append(present_relationshipEstablishedDate);
    if (present_relationshipEstablishedDate)
      builder.append(relationshipEstablishedDate);

    boolean present_relationshipOfResource = true && (isSetRelationshipOfResource());
    builder.append(present_relationshipOfResource);
    if (present_relationshipOfResource)
      builder.append(relationshipOfResource);

    boolean present_relationshipRemarks = true && (isSetRelationshipRemarks());
    builder.append(present_relationshipRemarks);
    if (present_relationshipRemarks)
      builder.append(relationshipRemarks);

    boolean present_resourceID = true && (isSetResourceID());
    builder.append(present_resourceID);
    if (present_resourceID)
      builder.append(resourceID);

    boolean present_resourceRelationshipID = true && (isSetResourceRelationshipID());
    builder.append(present_resourceRelationshipID);
    if (present_resourceRelationshipID)
      builder.append(resourceRelationshipID);

    return builder.toHashCode();
  }

  public int compareTo(ResourceRelationship other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    ResourceRelationship typedOther = (ResourceRelationship)other;

    lastComparison = Boolean.valueOf(isSetRelatedResourceID()).compareTo(typedOther.isSetRelatedResourceID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRelatedResourceID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.relatedResourceID, typedOther.relatedResourceID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRelationshipAccordingTo()).compareTo(typedOther.isSetRelationshipAccordingTo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRelationshipAccordingTo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.relationshipAccordingTo, typedOther.relationshipAccordingTo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRelationshipEstablishedDate()).compareTo(typedOther.isSetRelationshipEstablishedDate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRelationshipEstablishedDate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.relationshipEstablishedDate, typedOther.relationshipEstablishedDate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRelationshipOfResource()).compareTo(typedOther.isSetRelationshipOfResource());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRelationshipOfResource()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.relationshipOfResource, typedOther.relationshipOfResource);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRelationshipRemarks()).compareTo(typedOther.isSetRelationshipRemarks());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRelationshipRemarks()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.relationshipRemarks, typedOther.relationshipRemarks);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetResourceID()).compareTo(typedOther.isSetResourceID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResourceID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resourceID, typedOther.resourceID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetResourceRelationshipID()).compareTo(typedOther.isSetResourceRelationshipID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResourceRelationshipID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resourceRelationshipID, typedOther.resourceRelationshipID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ResourceRelationship(");
    boolean first = true;

    sb.append("relatedResourceID:");
    if (this.relatedResourceID == null) {
      sb.append("null");
    } else {
      sb.append(this.relatedResourceID);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("relationshipAccordingTo:");
    if (this.relationshipAccordingTo == null) {
      sb.append("null");
    } else {
      sb.append(this.relationshipAccordingTo);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("relationshipEstablishedDate:");
    if (this.relationshipEstablishedDate == null) {
      sb.append("null");
    } else {
      sb.append(this.relationshipEstablishedDate);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("relationshipOfResource:");
    if (this.relationshipOfResource == null) {
      sb.append("null");
    } else {
      sb.append(this.relationshipOfResource);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("relationshipRemarks:");
    if (this.relationshipRemarks == null) {
      sb.append("null");
    } else {
      sb.append(this.relationshipRemarks);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("resourceID:");
    if (this.resourceID == null) {
      sb.append("null");
    } else {
      sb.append(this.resourceID);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("resourceRelationshipID:");
    if (this.resourceRelationshipID == null) {
      sb.append("null");
    } else {
      sb.append(this.resourceRelationshipID);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ResourceRelationshipStandardSchemeFactory implements SchemeFactory {
    public ResourceRelationshipStandardScheme getScheme() {
      return new ResourceRelationshipStandardScheme();
    }
  }

  private static class ResourceRelationshipStandardScheme extends StandardScheme<ResourceRelationship> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ResourceRelationship struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RELATED_RESOURCE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.relatedResourceID = iprot.readString();
              struct.setRelatedResourceIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // RELATIONSHIP_ACCORDING_TO
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.relationshipAccordingTo = iprot.readString();
              struct.setRelationshipAccordingToIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // RELATIONSHIP_ESTABLISHED_DATE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.relationshipEstablishedDate = iprot.readString();
              struct.setRelationshipEstablishedDateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // RELATIONSHIP_OF_RESOURCE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.relationshipOfResource = iprot.readString();
              struct.setRelationshipOfResourceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // RELATIONSHIP_REMARKS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.relationshipRemarks = iprot.readString();
              struct.setRelationshipRemarksIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // RESOURCE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.resourceID = iprot.readString();
              struct.setResourceIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // RESOURCE_RELATIONSHIP_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.resourceRelationshipID = iprot.readString();
              struct.setResourceRelationshipIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ResourceRelationship struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.relatedResourceID != null) {
        oprot.writeFieldBegin(RELATED_RESOURCE_ID_FIELD_DESC);
        oprot.writeString(struct.relatedResourceID);
        oprot.writeFieldEnd();
      }
      if (struct.relationshipAccordingTo != null) {
        oprot.writeFieldBegin(RELATIONSHIP_ACCORDING_TO_FIELD_DESC);
        oprot.writeString(struct.relationshipAccordingTo);
        oprot.writeFieldEnd();
      }
      if (struct.relationshipEstablishedDate != null) {
        oprot.writeFieldBegin(RELATIONSHIP_ESTABLISHED_DATE_FIELD_DESC);
        oprot.writeString(struct.relationshipEstablishedDate);
        oprot.writeFieldEnd();
      }
      if (struct.relationshipOfResource != null) {
        oprot.writeFieldBegin(RELATIONSHIP_OF_RESOURCE_FIELD_DESC);
        oprot.writeString(struct.relationshipOfResource);
        oprot.writeFieldEnd();
      }
      if (struct.relationshipRemarks != null) {
        oprot.writeFieldBegin(RELATIONSHIP_REMARKS_FIELD_DESC);
        oprot.writeString(struct.relationshipRemarks);
        oprot.writeFieldEnd();
      }
      if (struct.resourceID != null) {
        oprot.writeFieldBegin(RESOURCE_ID_FIELD_DESC);
        oprot.writeString(struct.resourceID);
        oprot.writeFieldEnd();
      }
      if (struct.resourceRelationshipID != null) {
        oprot.writeFieldBegin(RESOURCE_RELATIONSHIP_ID_FIELD_DESC);
        oprot.writeString(struct.resourceRelationshipID);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ResourceRelationshipTupleSchemeFactory implements SchemeFactory {
    public ResourceRelationshipTupleScheme getScheme() {
      return new ResourceRelationshipTupleScheme();
    }
  }

  private static class ResourceRelationshipTupleScheme extends TupleScheme<ResourceRelationship> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ResourceRelationship struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetRelatedResourceID()) {
        optionals.set(0);
      }
      if (struct.isSetRelationshipAccordingTo()) {
        optionals.set(1);
      }
      if (struct.isSetRelationshipEstablishedDate()) {
        optionals.set(2);
      }
      if (struct.isSetRelationshipOfResource()) {
        optionals.set(3);
      }
      if (struct.isSetRelationshipRemarks()) {
        optionals.set(4);
      }
      if (struct.isSetResourceID()) {
        optionals.set(5);
      }
      if (struct.isSetResourceRelationshipID()) {
        optionals.set(6);
      }
      oprot.writeBitSet(optionals, 7);
      if (struct.isSetRelatedResourceID()) {
        oprot.writeString(struct.relatedResourceID);
      }
      if (struct.isSetRelationshipAccordingTo()) {
        oprot.writeString(struct.relationshipAccordingTo);
      }
      if (struct.isSetRelationshipEstablishedDate()) {
        oprot.writeString(struct.relationshipEstablishedDate);
      }
      if (struct.isSetRelationshipOfResource()) {
        oprot.writeString(struct.relationshipOfResource);
      }
      if (struct.isSetRelationshipRemarks()) {
        oprot.writeString(struct.relationshipRemarks);
      }
      if (struct.isSetResourceID()) {
        oprot.writeString(struct.resourceID);
      }
      if (struct.isSetResourceRelationshipID()) {
        oprot.writeString(struct.resourceRelationshipID);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ResourceRelationship struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(7);
      if (incoming.get(0)) {
        struct.relatedResourceID = iprot.readString();
        struct.setRelatedResourceIDIsSet(true);
      }
      if (incoming.get(1)) {
        struct.relationshipAccordingTo = iprot.readString();
        struct.setRelationshipAccordingToIsSet(true);
      }
      if (incoming.get(2)) {
        struct.relationshipEstablishedDate = iprot.readString();
        struct.setRelationshipEstablishedDateIsSet(true);
      }
      if (incoming.get(3)) {
        struct.relationshipOfResource = iprot.readString();
        struct.setRelationshipOfResourceIsSet(true);
      }
      if (incoming.get(4)) {
        struct.relationshipRemarks = iprot.readString();
        struct.setRelationshipRemarksIsSet(true);
      }
      if (incoming.get(5)) {
        struct.resourceID = iprot.readString();
        struct.setResourceIDIsSet(true);
      }
      if (incoming.get(6)) {
        struct.resourceRelationshipID = iprot.readString();
        struct.setResourceRelationshipIDIsSet(true);
      }
    }
  }

}

