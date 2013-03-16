/**
 * This file contains the Gulo graph-based schema using Thrift. It contains nodes,
 * edges, and properties. For background and rational for why we are going with
 * a graph-based schema, see: 
 * 
 *   http://nathanmarz.com/blog/thrift-graphs-strong-flexible-schemas-on-hadoop.html
 * 
 * The basic nodes in our schema are Record, Resource, Dataset, and Organization.
 *
 *   +----------+    +----------+    +----------+    +--------------+
 *   |          |    |          |    |          |    |              |
 *   | Record   +----+ Dataset  +----+ Resource +----+ Organization |
 *   |          |    |          |    |          |    |              |
 *   +----------+    +----------+    +----------+    +--------------+
 */

namespace java gulo.schema

/**
 * The RecordSource uniquely identifies a Darwin Core Record node. The sourceID 
 * comes from source_id column in a Darwin Core Archive.
 */
struct RecordSource {
  1: required string sourceID; // source_id from DwCA.
  2: required string datasetUUID;
}

/**
 * The RecordID node uniquely identifies a Darwin Core Record by one of 
 * RecordSource or GUID.
 */
union RecordID {
  1: RecordSource recordSource;
  2: string guid;
}

/**
 * The Darwin Core Occurrence class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#occurrenceindex
 */
struct Occurrence {
  1: string associatedMedia;
  2: string associatedOccurrences;
  3: string associatedReferences;
  4: string associatedSequences;
  5: string associatedTaxa;
  6: string behavior;
  7: string catalogNumber;
  8: string disposition;
  9: string establishmentMeans;
  10: string individualCount;
  11: string individualID;
  12: string lifeStage;
  13: string occurrenceID;
  14: string occurrenceRemarks;
  15: string occurrenceStatus;
  16: string otherCatalogNumbers;
  17: string preparations;
  18: string previousIdentifications;
  19: string recordNumber;
  20: string recordedBy;
  21: string reproductiveCondition;
  22: string sex;
}

/**
 * The Darwin Core Event class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#eventindex
 */
struct Event {
  1: string day;
  2: string endDayOfYear;
  3: string eventDate;
  4: string eventID;
  5: string eventRemarks;
  6: string eventTime;
  7: string fieldNotes;
  8: string fieldNumber;
  9: string habitat;
  10: string month;
  11: string samplingEffort;
  12: string samplingProtocol;
  13: string startDayOfYear;
  14: string verbatimEventDate;
  15: string year;
}

/**
 * The Darwin Core Location class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#locationindex
 */
struct Location {
  1: string continent;
  2: string coordinatePrecision;
  3: string coordinateUncertaintyInMeters;
  4: string country;
  5: string countryCode;
  6: string county;
  7: string decimalLatitude;
  8: string decimalLongitude;
  9: string footprintSRS;
  10: string footprintSpatialFit;
  11: string footprintWKT;
  12: string geodeticDatum;
  13: string georeferenceProtocol;
  14: string georeferenceRemarks;
  15: string georeferenceSources;
  16: string georeferenceVerificationStatus;
  17: string georeferencedBy;
  18: string georeferencedDate;
  19: string higherGeography;
  20: string higherGeographyID;
  21: string island;
  22: string islandGroup;
  23: string locality;
  24: string locationAccordingTo;
  25: string locationID;
  26: string locationRemarks;
  27: string maximumDepthInMeters;
  28: string maximumDistanceAboveSurfaceInMeters;
  29: string maximumElevationInMeters;
  30: string minimumDepthInMeters;
  31: string minimumDistanceAboveSurfaceInMeters;
  32: string minimumElevationInMeters;
  33: string municipality;
  34: string pointRadiusSpatialFit;
  35: string stateProvince;
  36: string verbatimCoordinateSystem;
  37: string verbatimCoordinates;
  38: string verbatimDepth;
  39: string verbatimElevation;
  40: string verbatimLatitude;
  41: string verbatimLocality;
  42: string verbatimLongitude;
  43: string verbatimSRS;
  44: string waterBody;
}

/**
 * The Darwin Core GeologicalContext class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#geologicalindex
 */
struct GeologicalContext {
  1: string bed;
  2: string earliestAgeOrLowestStage;
  3: string earliestEonOrLowestEonothem;
  4: string earliestEpochOrLowestSeries;
  5: string earliestEraOrLowestErathem;
  6: string earliestPeriodOrLowestSystem;
  7: string formation;
  8: string geologicalContextID;
  9: string group;
  10: string highestBiostratigraphicZone;
  11: string latestAgeOrHighestStage;
  12: string latestEonOrHighestEonothem;
  13: string latestEpochOrHighestSeries;
  14: string latestEraOrHighestErathem;
  15: string latestPeriodOrHighestSystem;
  16: string lithostratigraphicTerms;
  17: string lowestBiostratigraphicZone;
  18: string member;
}

/**
 * The Darwin Core Identification class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#Identification
 */
struct Identification {
  1: string identificationID;
  2: string identifiedBy;
  3: string dateIdentified;
  4: string identificationReferences;
  5: string identificationVerificationStatus;
  6: string identificationRemarks;
  7: string identificationQualifier;
  8: string typeStatus;
}

/**
 * The Darwin Core Taxon class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#Taxon
 */
struct Taxon {
  1: string acceptedNameUsage;
  2: string acceptedNameUsageID;
  3: string clazz;
  4: string family;
  5: string genus;
  6: string higherClassification;
  7: string infraspecificEpithet;
  8: string kingdom;
  9: string nameAccordingTo;
  10: string nameAccordingToID;
  11: string namePublishedIn;
  12: string namePublishedInID;
  13: string namePublishedInYear;
  14: string nomenclaturalCode;
  15: string nomenclaturalStatus;
  16: string order;
  17: string originalNameUsage;
  18: string originalNameUsageID;
  19: string parentNameUsage;
  20: string parentNameUsageID;
  21: string phylum;
  22: string scientificName;
  23: string scientificNameAuthorship;
  24: string scientificNameID;
  25: string specificEpithet;
  26: string subgenus;
  27: string taxonConceptID;
  28: string taxonID;
  29: string taxonRank;
  30: string taxonRemarks;
  31: string taxonomicStatus;
  32: string verbatimTaxonRank;
  33: string vernacularName;
}

/**
 * The Darwin Core ResourceRelationship class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#ResourceRelationship
 */
struct ResourceRelationship {
  1: string relatedResourceID;
  2: string relationshipAccordingTo;
  3: string relationshipEstablishedDate;
  4: string relationshipOfResource;
  5: string relationshipRemarks;
  6: string resourceID;
  7: string resourceRelationshipID;
}

/**
 * The Darwin Core MeasurementOrFact class structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#MeasurementOrFact
 */
struct MeasurementOrFact {
  1: string measurementAccuracy;
  2: string measurementDeterminedBy;
  3: string measurementDeterminedDate;
  4: string measurementID;
  5: string measurementMethod;
  6: string measurementRemarks;
  7: string measurementType;
  8: string measurementUnit;
  9: string measurementValue;
}

/**
 * The Darwin Core Record-level structure.
 * 
 * http://rs.tdwg.org/dwc/terms/index.htm#MeasurementOrFact
 */
struct RecordLevel {
  1: string accessRights;
  2: string basisOfRecord;
  3: string bibliographicCitation;
  4: string collectionCode;
  5: string collectionID;
  6: string dataGeneralizations;
  7: string datasetID;
  8: string datasetName;
  9: string dynamicProperties;
  10: string informationWithheld;
  11: string institutionCode;
  12: string institutionID;
  13: string language;
  14: string modified;
  15: string ownerInstitutionCode;
  16: string references;
  17: string rights;
  18: string rightsHolder;
  19: string type;
}

/**
 * The RecordPropertyValue is the polymorphic representation of a Darwin Core 
 * class structure, which is either RecordLevel, Occurrence, Event, Location,
 * GeologicalContext, Identification, Taxon, ResourceRelationship, or 
 * MeasurementOrFact.
 */
union RecordPropertyValue {
  1: RecordLevel recordLevel;
  2: Occurrence occurrence;
  3: Event event;
  4: Location location;
  5: GeologicalContext geologicalContext;
  6: Identification identification;
  7: Taxon taxon;
  8: ResourceRelationship resourceRelationship;
  9: MeasurementOrFact measurementOrFact;
}

/**
 * The RecordProperty represents a RecordPropertyValue and the RecordID to which
 * it belongs.
 */
struct RecordProperty {
  1: RecordID id;
  2: RecordPropertyValue value;
}

/**
 * The ResourceID node uniquely identifies a Resource. The UUID comes from the 
 * <item><guid> element in the IPT /ipt/rss.do RSS feed.
 */
union ResourceID {
  1: string uuid;
}

/**
 * The ResourcePropertyValue is the polymorphic representation of Resource 
 * property values. 
 */
union ResourcePropertyValue {
  1: string title;
  2: string link;
  3: string description;
  4: string author;
  5: string eml;
  6: string publisher;
  7: string creator;
  8: string dwca;
  9: string pubdate;
  10: string orgurl;
  11: string orgcontact;
  12: string orgname;
}

/**
 * The ResourceProperty represents a ResourcePropertyValue and the ResourceID to
 * which it belongs.
 */
struct ResourceProperty {
  1: ResourceID id;
  2: ResourcePropertyValue value;
}

/**
 * The DatasetID node uniquely identifies a Dataset. The GUID is the same as the
 * Resource UUID.
 */
union DatasetID {
  1: string guid;
}

/**
 * The DatasetPropertyValue is the polymorphic representation of Dataset 
 * property values.
 */
union DatasetPropertyValue {
  1: string title;
  2: string creator;
  3: string metadataProvider;
  4: string language;
  5: string associatedParty;
  6: string pubDate;
  7: string contact;
  8: string additionalInfo;
}  

/**
 * The DatasetProperty represents a DatasetPropertyValue and the DatasetID to 
 * which it belongs.
 */
struct DatasetProperty {
  1: required DatasetID id;
  2: required DatasetPropertyValue property;
}

/**
 * The OrganizationID node uniquely identifies an Organization. The UUID comes
 * from the following feed:
 *
 *   http://gbrds.gbif.org/registry/organisation.json
 *
 * To get the UUID you need the organization name.
 */
union OrganizationID {
  1: string uuid;
}

/**
 * The OrganizationPropertyValue is the polymorphic representation of 
 * Organization property values.
 */
union OrganizationPropertyValue {
  1: string description;
  2: string descriptionLanguage;
  3: string homepageURL;
  4: string key;
  5: string name;
  6: string nameLanguage;
  7: string nodeContactEmail;
  8: string nodeKey;
  9: string nodeName;
  10: string primaryContactAddress;
  11: string primaryContactDescription;
  12: string primaryContactEmail;
  13: string primaryContactName;
  14: string primaryContactPhone;
  15: string primaryContactType;
}

/**
 * The OrganizationProperty represents a OrganizationPropertyValue and the 
 * OrganizationID to which it belongs.
 */
struct OrganizationProperty {
  1: required OrganizationID id;
  2: required OrganizationPropertyValue property;
}

/**
 * Edge between a Dataset and one of its Records.
 */
struct DatasetRecordEdge {
  1: required DatasetID dataSet;
  2: required RecordID record;
}

/**
 * Edge between a Resource and its Dataset.
 */
struct ResourceDatasetEdge {
  1: required ResourceID resource;
  2: required DatasetID dataset;
} 

/**
 * Edge between a Resource and its Organization.
 */
struct ResourceOrganizationEdge {
  1: required ResourceID resource;
  2: required OrganizationID organization;
}

/**
 * Pedigree for the DataUnit.
 */
struct Pedigree {
  1: required i32 trueAsOfSecs;
}

/**
 * The DataUnit is a polymorphic representation of properties and edges.
 */
union DataUnit {
  1: RecordProperty recordProperty;
  2: DatasetProperty datasetProperty;
  3: ResourceProperty resourceProperty;
  4: OrganizationProperty organizationProperty;
  5: DatasetRecordEdge datasetRecord;
  6: ResourceDatasetEdge resourceDataset;
  7: ResourceOrganizationEdge resourceOrganization;
}

/**
 * The Data struct is the root object that combines a DataUnit with its Pedigree.
 */
struct Data {
  1: required Pedigree pedigree;
  2: required DataUnit dataUnit;
}
