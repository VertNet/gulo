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
  private static final org.apache.thrift.protocol.TField LINK_FIELD_DESC = new org.apache.thrift.protocol.TField("link", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField DESCRIPTION_FIELD_DESC = new org.apache.thrift.protocol.TField("description", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField AUTHOR_FIELD_DESC = new org.apache.thrift.protocol.TField("author", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField EML_FIELD_DESC = new org.apache.thrift.protocol.TField("eml", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField PUBLISHER_FIELD_DESC = new org.apache.thrift.protocol.TField("publisher", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField CREATOR_FIELD_DESC = new org.apache.thrift.protocol.TField("creator", org.apache.thrift.protocol.TType.STRING, (short)7);
  private static final org.apache.thrift.protocol.TField DWCA_FIELD_DESC = new org.apache.thrift.protocol.TField("dwca", org.apache.thrift.protocol.TType.STRING, (short)8);
  private static final org.apache.thrift.protocol.TField PUBDATE_FIELD_DESC = new org.apache.thrift.protocol.TField("pubdate", org.apache.thrift.protocol.TType.STRING, (short)9);
  private static final org.apache.thrift.protocol.TField ORGURL_FIELD_DESC = new org.apache.thrift.protocol.TField("orgurl", org.apache.thrift.protocol.TType.STRING, (short)10);
  private static final org.apache.thrift.protocol.TField ORGCONTACT_FIELD_DESC = new org.apache.thrift.protocol.TField("orgcontact", org.apache.thrift.protocol.TType.STRING, (short)11);
  private static final org.apache.thrift.protocol.TField ORGNAME_FIELD_DESC = new org.apache.thrift.protocol.TField("orgname", org.apache.thrift.protocol.TType.STRING, (short)12);

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TITLE((short)1, "title"),
    LINK((short)2, "link"),
    DESCRIPTION((short)3, "description"),
    AUTHOR((short)4, "author"),
    EML((short)5, "eml"),
    PUBLISHER((short)6, "publisher"),
    CREATOR((short)7, "creator"),
    DWCA((short)8, "dwca"),
    PUBDATE((short)9, "pubdate"),
    ORGURL((short)10, "orgurl"),
    ORGCONTACT((short)11, "orgcontact"),
    ORGNAME((short)12, "orgname");

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
        case 2: // LINK
          return LINK;
        case 3: // DESCRIPTION
          return DESCRIPTION;
        case 4: // AUTHOR
          return AUTHOR;
        case 5: // EML
          return EML;
        case 6: // PUBLISHER
          return PUBLISHER;
        case 7: // CREATOR
          return CREATOR;
        case 8: // DWCA
          return DWCA;
        case 9: // PUBDATE
          return PUBDATE;
        case 10: // ORGURL
          return ORGURL;
        case 11: // ORGCONTACT
          return ORGCONTACT;
        case 12: // ORGNAME
          return ORGNAME;
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
    tmpMap.put(_Fields.LINK, new org.apache.thrift.meta_data.FieldMetaData("link", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DESCRIPTION, new org.apache.thrift.meta_data.FieldMetaData("description", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.AUTHOR, new org.apache.thrift.meta_data.FieldMetaData("author", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.EML, new org.apache.thrift.meta_data.FieldMetaData("eml", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PUBLISHER, new org.apache.thrift.meta_data.FieldMetaData("publisher", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CREATOR, new org.apache.thrift.meta_data.FieldMetaData("creator", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DWCA, new org.apache.thrift.meta_data.FieldMetaData("dwca", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PUBDATE, new org.apache.thrift.meta_data.FieldMetaData("pubdate", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ORGURL, new org.apache.thrift.meta_data.FieldMetaData("orgurl", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ORGCONTACT, new org.apache.thrift.meta_data.FieldMetaData("orgcontact", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ORGNAME, new org.apache.thrift.meta_data.FieldMetaData("orgname", org.apache.thrift.TFieldRequirementType.DEFAULT, 
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

  public static ResourcePropertyValue link(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setLink(value);
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

  public static ResourcePropertyValue eml(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setEml(value);
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

  public static ResourcePropertyValue dwca(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setDwca(value);
    return x;
  }

  public static ResourcePropertyValue pubdate(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setPubdate(value);
    return x;
  }

  public static ResourcePropertyValue orgurl(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setOrgurl(value);
    return x;
  }

  public static ResourcePropertyValue orgcontact(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setOrgcontact(value);
    return x;
  }

  public static ResourcePropertyValue orgname(String value) {
    ResourcePropertyValue x = new ResourcePropertyValue();
    x.setOrgname(value);
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
      case LINK:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'link', but got " + value.getClass().getSimpleName());
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
      case EML:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'eml', but got " + value.getClass().getSimpleName());
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
      case DWCA:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'dwca', but got " + value.getClass().getSimpleName());
      case PUBDATE:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'pubdate', but got " + value.getClass().getSimpleName());
      case ORGURL:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'orgurl', but got " + value.getClass().getSimpleName());
      case ORGCONTACT:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'orgcontact', but got " + value.getClass().getSimpleName());
      case ORGNAME:
        if (value instanceof String) {
          break;
        }
        throw new ClassCastException("Was expecting value of type String for field 'orgname', but got " + value.getClass().getSimpleName());
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
        case LINK:
          if (field.type == LINK_FIELD_DESC.type) {
            String link;
            link = iprot.readString();
            return link;
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
        case EML:
          if (field.type == EML_FIELD_DESC.type) {
            String eml;
            eml = iprot.readString();
            return eml;
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
        case DWCA:
          if (field.type == DWCA_FIELD_DESC.type) {
            String dwca;
            dwca = iprot.readString();
            return dwca;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case PUBDATE:
          if (field.type == PUBDATE_FIELD_DESC.type) {
            String pubdate;
            pubdate = iprot.readString();
            return pubdate;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case ORGURL:
          if (field.type == ORGURL_FIELD_DESC.type) {
            String orgurl;
            orgurl = iprot.readString();
            return orgurl;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case ORGCONTACT:
          if (field.type == ORGCONTACT_FIELD_DESC.type) {
            String orgcontact;
            orgcontact = iprot.readString();
            return orgcontact;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case ORGNAME:
          if (field.type == ORGNAME_FIELD_DESC.type) {
            String orgname;
            orgname = iprot.readString();
            return orgname;
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
      case LINK:
        String link = (String)value_;
        oprot.writeString(link);
        return;
      case DESCRIPTION:
        String description = (String)value_;
        oprot.writeString(description);
        return;
      case AUTHOR:
        String author = (String)value_;
        oprot.writeString(author);
        return;
      case EML:
        String eml = (String)value_;
        oprot.writeString(eml);
        return;
      case PUBLISHER:
        String publisher = (String)value_;
        oprot.writeString(publisher);
        return;
      case CREATOR:
        String creator = (String)value_;
        oprot.writeString(creator);
        return;
      case DWCA:
        String dwca = (String)value_;
        oprot.writeString(dwca);
        return;
      case PUBDATE:
        String pubdate = (String)value_;
        oprot.writeString(pubdate);
        return;
      case ORGURL:
        String orgurl = (String)value_;
        oprot.writeString(orgurl);
        return;
      case ORGCONTACT:
        String orgcontact = (String)value_;
        oprot.writeString(orgcontact);
        return;
      case ORGNAME:
        String orgname = (String)value_;
        oprot.writeString(orgname);
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
        case LINK:
          String link;
          link = iprot.readString();
          return link;
        case DESCRIPTION:
          String description;
          description = iprot.readString();
          return description;
        case AUTHOR:
          String author;
          author = iprot.readString();
          return author;
        case EML:
          String eml;
          eml = iprot.readString();
          return eml;
        case PUBLISHER:
          String publisher;
          publisher = iprot.readString();
          return publisher;
        case CREATOR:
          String creator;
          creator = iprot.readString();
          return creator;
        case DWCA:
          String dwca;
          dwca = iprot.readString();
          return dwca;
        case PUBDATE:
          String pubdate;
          pubdate = iprot.readString();
          return pubdate;
        case ORGURL:
          String orgurl;
          orgurl = iprot.readString();
          return orgurl;
        case ORGCONTACT:
          String orgcontact;
          orgcontact = iprot.readString();
          return orgcontact;
        case ORGNAME:
          String orgname;
          orgname = iprot.readString();
          return orgname;
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
      case LINK:
        String link = (String)value_;
        oprot.writeString(link);
        return;
      case DESCRIPTION:
        String description = (String)value_;
        oprot.writeString(description);
        return;
      case AUTHOR:
        String author = (String)value_;
        oprot.writeString(author);
        return;
      case EML:
        String eml = (String)value_;
        oprot.writeString(eml);
        return;
      case PUBLISHER:
        String publisher = (String)value_;
        oprot.writeString(publisher);
        return;
      case CREATOR:
        String creator = (String)value_;
        oprot.writeString(creator);
        return;
      case DWCA:
        String dwca = (String)value_;
        oprot.writeString(dwca);
        return;
      case PUBDATE:
        String pubdate = (String)value_;
        oprot.writeString(pubdate);
        return;
      case ORGURL:
        String orgurl = (String)value_;
        oprot.writeString(orgurl);
        return;
      case ORGCONTACT:
        String orgcontact = (String)value_;
        oprot.writeString(orgcontact);
        return;
      case ORGNAME:
        String orgname = (String)value_;
        oprot.writeString(orgname);
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
      case LINK:
        return LINK_FIELD_DESC;
      case DESCRIPTION:
        return DESCRIPTION_FIELD_DESC;
      case AUTHOR:
        return AUTHOR_FIELD_DESC;
      case EML:
        return EML_FIELD_DESC;
      case PUBLISHER:
        return PUBLISHER_FIELD_DESC;
      case CREATOR:
        return CREATOR_FIELD_DESC;
      case DWCA:
        return DWCA_FIELD_DESC;
      case PUBDATE:
        return PUBDATE_FIELD_DESC;
      case ORGURL:
        return ORGURL_FIELD_DESC;
      case ORGCONTACT:
        return ORGCONTACT_FIELD_DESC;
      case ORGNAME:
        return ORGNAME_FIELD_DESC;
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

  public String getLink() {
    if (getSetField() == _Fields.LINK) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'link' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setLink(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.LINK;
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

  public String getEml() {
    if (getSetField() == _Fields.EML) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'eml' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setEml(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.EML;
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

  public String getDwca() {
    if (getSetField() == _Fields.DWCA) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'dwca' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setDwca(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.DWCA;
    value_ = value;
  }

  public String getPubdate() {
    if (getSetField() == _Fields.PUBDATE) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'pubdate' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setPubdate(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.PUBDATE;
    value_ = value;
  }

  public String getOrgurl() {
    if (getSetField() == _Fields.ORGURL) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'orgurl' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setOrgurl(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.ORGURL;
    value_ = value;
  }

  public String getOrgcontact() {
    if (getSetField() == _Fields.ORGCONTACT) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'orgcontact' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setOrgcontact(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.ORGCONTACT;
    value_ = value;
  }

  public String getOrgname() {
    if (getSetField() == _Fields.ORGNAME) {
      return (String)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'orgname' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setOrgname(String value) {
    if (value == null) throw new NullPointerException();
    setField_ = _Fields.ORGNAME;
    value_ = value;
  }

  public boolean isSetTitle() {
    return setField_ == _Fields.TITLE;
  }


  public boolean isSetLink() {
    return setField_ == _Fields.LINK;
  }


  public boolean isSetDescription() {
    return setField_ == _Fields.DESCRIPTION;
  }


  public boolean isSetAuthor() {
    return setField_ == _Fields.AUTHOR;
  }


  public boolean isSetEml() {
    return setField_ == _Fields.EML;
  }


  public boolean isSetPublisher() {
    return setField_ == _Fields.PUBLISHER;
  }


  public boolean isSetCreator() {
    return setField_ == _Fields.CREATOR;
  }


  public boolean isSetDwca() {
    return setField_ == _Fields.DWCA;
  }


  public boolean isSetPubdate() {
    return setField_ == _Fields.PUBDATE;
  }


  public boolean isSetOrgurl() {
    return setField_ == _Fields.ORGURL;
  }


  public boolean isSetOrgcontact() {
    return setField_ == _Fields.ORGCONTACT;
  }


  public boolean isSetOrgname() {
    return setField_ == _Fields.ORGNAME;
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
