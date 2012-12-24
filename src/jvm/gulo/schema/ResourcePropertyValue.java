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

/**
 * The ResourcePropertyValue is the polymorphic representation of Resource
 * property values.
 */
public class ResourcePropertyValue extends org.apache.thrift.TUnion<ResourcePropertyValue, ResourcePropertyValue._Fields> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ResourcePropertyValue");
  private static final org.apache.thrift.protocol.TField TITLE_FIELD_DESC = new org.apache.thrift.protocol.TField("title", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField URL_FIELD_DESC = new org.apache.thrift.protocol.TField("url", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField DESCRIPTION_FIELD_DESC = new org.apache.thrift.protocol.TField("description", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField AUTHOR_FIELD_DESC = new org.apache.thrift.protocol.TField("author", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField EML_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("emlUrl", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField PUBLISHER_FIELD_DESC = new org.apache.thrift.protocol.TField("publisher", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField CREATOR_FIELD_DESC = new org.apache.thrift.protocol.TField("creator", org.apache.thrift.protocol.TType.STRING, (short)7);
  private static final org.apache.thrift.protocol.TField DWCA_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("dwcaUrl", org.apache.thrift.protocol.TType.STRING, (short)8);
  private static final org.apache.thrift.protocol.TField PUB_DATE_FIELD_DESC = new org.apache.thrift.protocol.TField("pubDate", org.apache.thrift.protocol.TType.STRING, (short)9);

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TITLE((short)1, "title"),
    URL((short)2, "url"),
    DESCRIPTION((short)3, "description"),
    AUTHOR((short)4, "author"),
    EML_URL((short)5, "emlUrl"),
    PUBLISHER((short)6, "publisher"),
    CREATOR((short)7, "creator"),
    DWCA_URL((short)8, "dwcaUrl"),
    PUB_DATE((short)9, "pubDate");

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
        case 1: // TITLE
          return TITLE;
        case 2: // URL
          return URL;
        case 3: // DESCRIPTION
          return DESCRIPTION;
        case 4: // AUTHOR
          return AUTHOR;
        case 5: // EML_URL
          return EML_URL;
        case 6: // PUBLISHER
          return PUBLISHER;
        case 7: // CREATOR
          return CREATOR;
        case 8: // DWCA_URL
          return DWCA_URL;
        case 9: // PUB_DATE
          return PUB_DATE;
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

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TITLE, new org.apache.thrift.meta_data.FieldMetaData("title", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.URL, new org.apache.thrift.meta_data.FieldMetaData("url", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DESCRIPTION, new org.apache.thrift.meta_data.FieldMetaData("description", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.AUTHOR, new org.apache.thrift.meta_data.FieldMetaData("author", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.EML_URL, new org.apache.thrift.meta_data.FieldMetaData("emlUrl", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PUBLISHER, new org.apache.thrift.meta_data.FieldMetaData("publisher", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CREATOR, new org.apache.thrift.meta_data.FieldMetaData("creator", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DWCA_URL, new org.apache.thrift.meta_data.FieldMetaData("dwcaUrl", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PUB_DATE, new org.apache.thrift.meta_data.FieldMetaData("pubDate", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ResourcePropertyValue.class, metaDataMap);
  }

  public ResourcePropertyValue() {
    super();
  }

  public ResourcePropertyValue(_Fields setField, Object value) {
    super(setField, value);
  }

  public ResourcePropertyValue(ResourcePropertyValue other) {
    super(other);
  }
  public ResourcePropertyValue deepCopy() {
    return new ResourcePropertyValue(this);
  }

  public static ResourcePropertyValue title(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setTitle(value);
    return x;
  }

  public static ResourcePropertyValue url(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setUrl(value);
    return x;
  }

  public static ResourcePropertyValue description(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setDescription(value);
    return x;
  }

  public static ResourcePropertyValue author(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setAuthor(value);
    return x;
  }

  public static ResourcePropertyValue emlUrl(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setEmlUrl(value);
    return x;
  }

  public static ResourcePropertyValue publisher(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setPublisher(value);
    return x;
  }

  public static ResourcePropertyValue creator(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setCreator(value);
    return x;
  }

  public static ResourcePropertyValue dwcaUrl(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setDwcaUrl(value);
    return x;
  }

  public static ResourcePropertyValue pubDate(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setPubDate(value);
    return x;
  }


  @Override
  protected void checkType(_Fields setField, Object value) throws ClassCastException {
    switch (setField) {
      case TITLE:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'title', but got " + value.getClass().getSimpleName());
      case URL:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'url', but got " + value.getClass().getSimpleName());
      case DESCRIPTION:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'description', but got " + value.getClass().getSimpleName());
      case AUTHOR:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'author', but got " + value.getClass().getSimpleName());
      case EML_URL:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'emlUrl', but got " + value.getClass().getSimpleName());
      case PUBLISHER:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'publisher', but got " + value.getClass().getSimpleName());
      case CREATOR:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'creator', but got " + value.getClass().getSimpleName());
      case DWCA_URL:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'dwcaUrl', but got " + value.getClass().getSimpleName());
      case PUB_DATE:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'pubDate', but got " + value.getClass().getSimpleName());
      default:
        throw new IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  protected Object standardSchemeReadValue(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TField field) throws org.apache.thrift.TException {
    _Fields setField = _Fields.findByThriftId(field.id);
    if (setField != null) {
      switch (setField) {
        case TITLE:
          if (field.type == TITLE_FIELD_DESC.type) {
            String title;
            title = iprot.readString();
            return title;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case URL:
          if (field.type == URL_FIELD_DESC.type) {
            String url;
            url = iprot.readString();
            return url;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case DESCRIPTION:
          if (field.type == DESCRIPTION_FIELD_DESC.type) {
            String description;
            description = iprot.readString();
            return description;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case AUTHOR:
          if (field.type == AUTHOR_FIELD_DESC.type) {
            String author;
            author = iprot.readString();
            return author;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case EML_URL:
          if (field.type == EML_URL_FIELD_DESC.type) {
            String emlUrl;
            emlUrl = iprot.readString();
            return emlUrl;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case PUBLISHER:
          if (field.type == PUBLISHER_FIELD_DESC.type) {
            String publisher;
            publisher = iprot.readString();
            return publisher;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case CREATOR:
          if (field.type == CREATOR_FIELD_DESC.type) {
            String creator;
            creator = iprot.readString();
            return creator;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case DWCA_URL:
          if (field.type == DWCA_URL_FIELD_DESC.type) {
            String dwcaUrl;
            dwcaUrl = iprot.readString();
            return dwcaUrl;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case PUB_DATE:
          if (field.type == PUB_DATE_FIELD_DESC.type) {
            String pubDate;
            pubDate = iprot.readString();
            return pubDate;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        default:
          throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
      }
    } else {
      return null;
    }
  }

  @Override
  protected void standardSchemeWriteValue(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    switch (setField_) {
      case TITLE:
        String title = (String)value_;
        oprot.writeString(title);
        return;
      case URL:
        String url = (String)value_;
        oprot.writeString(url);
        return;
      case DESCRIPTION:
        String description = (String)value_;
        oprot.writeString(description);
        return;
      case AUTHOR:
        String author = (String)value_;
        oprot.writeString(author);
        return;
      case EML_URL:
        String emlUrl = (String)value_;
        oprot.writeString(emlUrl);
        return;
      case PUBLISHER:
        String publisher = (String)value_;
        oprot.writeString(publisher);
        return;
      case CREATOR:
        String creator = (String)value_;
        oprot.writeString(creator);
        return;
      case DWCA_URL:
        String dwcaUrl = (String)value_;
        oprot.writeString(dwcaUrl);
        return;
      case PUB_DATE:
        String pubDate = (String)value_;
        oprot.writeString(pubDate);
        return;
      default:
        throw new IllegalStateException("Cannot write union with unknown field " + setField_);
    }
  }

  @Override
  protected Object tupleSchemeReadValue(org.apache.thrift.protocol.TProtocol iprot, short fieldID) throws org.apache.thrift.TException {
    _Fields setField = _Fields.findByThriftId(fieldID);
    if (setField != null) {
      switch (setField) {
        case TITLE:
          String title;
          title = iprot.readString();
          return title;
        case URL:
          String url;
          url = iprot.readString();
          return url;
        case DESCRIPTION:
          String description;
          description = iprot.readString();
          return description;
        case AUTHOR:
          String author;
          author = iprot.readString();
          return author;
        case EML_URL:
          String emlUrl;
          emlUrl = iprot.readString();
          return emlUrl;
        case PUBLISHER:
          String publisher;
          publisher = iprot.readString();
          return publisher;
        case CREATOR:
          String creator;
          creator = iprot.readString();
          return creator;
        case DWCA_URL:
          String dwcaUrl;
          dwcaUrl = iprot.readString();
          return dwcaUrl;
        case PUB_DATE:
          String pubDate;
          pubDate = iprot.readString();
          return pubDate;
        default:
          throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
      }
    } else {
      return null;
    }
  }

  @Override
  protected void tupleSchemeWriteValue(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    switch (setField_) {
      case TITLE:
        String title = (String)value_;
        oprot.writeString(title);
        return;
      case URL:
        String url = (String)value_;
        oprot.writeString(url);
        return;
      case DESCRIPTION:
        String description = (String)value_;
        oprot.writeString(description);
        return;
      case AUTHOR:
        String author = (String)value_;
        oprot.writeString(author);
        return;
      case EML_URL:
        String emlUrl = (String)value_;
        oprot.writeString(emlUrl);
        return;
      case PUBLISHER:
        String publisher = (String)value_;
        oprot.writeString(publisher);
        return;
      case CREATOR:
        String creator = (String)value_;
        oprot.writeString(creator);
        return;
      case DWCA_URL:
        String dwcaUrl = (String)value_;
        oprot.writeString(dwcaUrl);
        return;
      case PUB_DATE:
        String pubDate = (String)value_;
        oprot.writeString(pubDate);
        return;
      default:
        throw new IllegalStateException("Cannot write union with unknown field " + setField_);
    }
  }

  @Override
  protected org.apache.thrift.protocol.TField getFieldDesc(_Fields setField) {
    switch (setField) {
      case TITLE:
        return TITLE_FIELD_DESC;
      case URL:
        return URL_FIELD_DESC;
      case DESCRIPTION:
        return DESCRIPTION_FIELD_DESC;
      case AUTHOR:
        return AUTHOR_FIELD_DESC;
      case EML_URL:
        return EML_URL_FIELD_DESC;
      case PUBLISHER:
        return PUBLISHER_FIELD_DESC;
      case CREATOR:
        return CREATOR_FIELD_DESC;
      case DWCA_URL:
        return DWCA_URL_FIELD_DESC;
      case PUB_DATE:
        return PUB_DATE_FIELD_DESC;
      default:
        throw new IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  protected org.apache.thrift.protocol.TStruct getStructDesc() {
    return STRUCT_DESC;
  }

  @Override
  protected _Fields enumForId(short id) {
    return _Fields.findByThriftIdOrThrow(id);
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }


  public String getTitle() {
    if (getSetField() == _Fields.TITLE) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'title' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setTitle(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.TITLE;
    value_ = value;
  }

  public String getUrl() {
    if (getSetField() == _Fields.URL) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'url' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setUrl(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.URL;
    value_ = value;
  }

  public String getDescription() {
    if (getSetField() == _Fields.DESCRIPTION) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'description' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setDescription(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.DESCRIPTION;
    value_ = value;
  }

  public String getAuthor() {
    if (getSetField() == _Fields.AUTHOR) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'author' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setAuthor(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.AUTHOR;
    value_ = value;
  }

  public String getEmlUrl() {
    if (getSetField() == _Fields.EML_URL) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'emlUrl' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setEmlUrl(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.EML_URL;
    value_ = value;
  }

  public String getPublisher() {
    if (getSetField() == _Fields.PUBLISHER) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'publisher' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setPublisher(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.PUBLISHER;
    value_ = value;
  }

  public String getCreator() {
    if (getSetField() == _Fields.CREATOR) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'creator' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setCreator(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.CREATOR;
    value_ = value;
  }

  public String getDwcaUrl() {
    if (getSetField() == _Fields.DWCA_URL) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'dwcaUrl' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setDwcaUrl(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.DWCA_URL;
    value_ = value;
  }

  public String getPubDate() {
    if (getSetField() == _Fields.PUB_DATE) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'pubDate' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setPubDate(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.PUB_DATE;
    value_ = value;
  }

  public boolean isSetTitle() {
    return setField_ == _Fields.TITLE;
  }


  public boolean isSetUrl() {
    return setField_ == _Fields.URL;
  }


  public boolean isSetDescription() {
    return setField_ == _Fields.DESCRIPTION;
  }


  public boolean isSetAuthor() {
    return setField_ == _Fields.AUTHOR;
  }


  public boolean isSetEmlUrl() {
    return setField_ == _Fields.EML_URL;
  }


  public boolean isSetPublisher() {
    return setField_ == _Fields.PUBLISHER;
  }


  public boolean isSetCreator() {
    return setField_ == _Fields.CREATOR;
  }


  public boolean isSetDwcaUrl() {
    return setField_ == _Fields.DWCA_URL;
  }


  public boolean isSetPubDate() {
    return setField_ == _Fields.PUB_DATE;
  }


  public boolean equals(Object other) {
    if (other instanceof ResourcePropertyValue) {
      return equals((ResourcePropertyValue)other);
    } else {
      return false;
    }
  }

  public boolean equals(ResourcePropertyValue other) {
    return other != null && getSetField() == other.getSetField() && getFieldValue().equals(other.getFieldValue());
  }

  @Override
  public int compareTo(ResourcePropertyValue other) {
    int lastComparison = org.apache.thrift.TBaseHelper.compareTo(getSetField(), other.getSetField());
    if (lastComparison == 0) {
      return org.apache.thrift.TBaseHelper.compareTo(getFieldValue(), other.getFieldValue());
    }
    return lastComparison;
  }


  @Override
  public int hashCode() {
    HashCodeBuilder hcb = new HashCodeBuilder();
    hcb.append(this.getClass().getName());
    org.apache.thrift.TFieldIdEnum setField = getSetField();
    if (setField != null) {
      hcb.append(setField.getThriftFieldId());
      Object value = getFieldValue();
      if (value instanceof org.apache.thrift.TEnum) {
        hcb.append(((org.apache.thrift.TEnum)getFieldValue()).getValue());
      } else {
        hcb.append(value);
      }
    }
    return hcb.toHashCode();
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


}