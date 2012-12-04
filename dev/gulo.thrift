namespace java gulo.schema

union RecordID {
  1: string id;
}

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

struct ResourceRelationship {
  1: string relatedResourceID;
  2: string relationshipAccordingTo;
  3: string relationshipEstablishedDate;
  4: string relationshipOfResource;
  5: string relationshipRemarks;
  6: string resourceID;
  7: string resourceRelationshipID;
}

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

union RecordPropertyValue {
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
  20: Occurrence occurrence;
  21: Event event;
  22: Location location;
  23: GeologicalContext geologicalContext;
  24: Identification identification;
  25: Taxon taxon;
  26: ResourceRelationship resourceRelationship;
  27: MeasurementOrFact measurementOrFact;
}

struct RecordProperty {
  1: required RecordID id;
  2: required RecordPropertyValue property;
}

union DatasetID {
  1: string uuid;
}

union DatasetPropertyValue {
  1: string abstractSummary;
  2: string datasetUUID;
  3: string intellectualRights;
  4: string language;
  5: string organizationName;
  6: string pubDate;
  7: string title;
}  

struct DatasetProperty {
  1: required DatasetID id;
  2: required DatasetPropertyValue property;
}

struct DatasetRecordEdge {
  1: required DatasetID dataSet;
  2: required RecordID record;
}

union OrganizationID {
  1: string name;
}

union OragnizationPropertyValue {
  1: string url;
}

struct OrganizationProperty {
  1: required OrganizationID id;
  2: required OragnizationPropertyValue property;
}

struct OrganizationDatasetEdge {
  1: required OrganizationID organization;
  2: required DatasetID dataset;
}

struct Pedigree {
  1: required i32 trueAsOfSecs;
}

union DataUnit {
  1: DatasetProperty datasetProperty;
  2: DatasetRecordEdge datasetRecord;
  3: OrganizationDatasetEdge organizationDataset;
  4: OrganizationProperty organizationProperty;
  5: RecordProperty recordProperty;
}

struct Data {
  1: required Pedigree pedigree;
  2: required DataUnit dataunit;
}
