namespace java gulo.schema

struct RecordID {
  1: required string sourceID;
  2: required string datasetUUID;
}

struct Occurrence {
  1: optional string associatedMedia;
  2: optional string associatedOccurrences;
  3: optional string associatedReferences;
  4: optional string associatedSequences;
  5: optional string associatedTaxa;
  6: optional string behavior;
  7: optional string catalogNumber;
  8: optional string disposition;
  9: optional string establishmentMeans;
  10: optional string individualCount;
  11: optional string individualID;
  12: optional string lifeStage;
  13: optional string occurrenceID;
  14: optional string occurrenceRemarks;
  15: optional string occurrenceStatus;
  16: optional string otherCatalogNumbers;
  17: optional string preparations;
  18: optional string previousIdentifications;
  19: optional string recordNumber;
  20: optional string recordedBy;
  21: optional string reproductiveCondition;
  22: optional string sex;
}

struct Event {
  1: optional string day;
  2: optional string endDayOfYear;
  3: optional string eventDate;
  4: optional string eventID;
  5: optional string eventRemarks;
  6: optional string eventTime;
  7: optional string fieldNotes;
  8: optional string fieldNumber;
  9: optional string habitat;
  10: optional string month;
  11: optional string samplingEffort;
  12: optional string samplingProtocol;
  13: optional string startDayOfYear;
  14: optional string verbatimEventDate;
  15: optional string year;
}

struct Location {
  1: optional string continent;
  2: optional string coordinatePrecision;
  3: optional string coordinateUncertaintyInMeters;
  4: optional string country;
  5: optional string countryCode;
  6: optional string county;
  7: optional string decimalLatitude;
  8: optional string decimalLongitude;
  9: optional string footprintSRS;
  10: optional string footprintSpatialFit;
  11: optional string footprintWKT;
  12: optional string geodeticDatum;
  13: optional string georeferenceProtocol;
  14: optional string georeferenceRemarks;
  15: optional string georeferenceSources;
  16: optional string georeferenceVerificationStatus;
  17: optional string georeferencedBy;
  18: optional string georeferencedDate;
  19: optional string higherGeography;
  20: optional string higherGeographyID;
  21: optional string island;
  22: optional string islandGroup;
  23: optional string locality;
  24: optional string locationAccordingTo;
  25: optional string locationID;
  26: optional string locationRemarks;
  27: optional string maximumDepthInMeters;
  28: optional string maximumDistanceAboveSurfaceInMeters;
  29: optional string maximumElevationInMeters;
  30: optional string minimumDepthInMeters;
  31: optional string minimumDistanceAboveSurfaceInMeters;
  32: optional string minimumElevationInMeters;
  33: optional string municipality;
  34: optional string pointRadiusSpatialFit;
  35: optional string stateProvince;
  36: optional string verbatimCoordinateSystem;
  37: optional string verbatimCoordinates;
  38: optional string verbatimDepth;
  39: optional string verbatimElevation;
  40: optional string verbatimLatitude;
  41: optional string verbatimLocality;
  42: optional string verbatimLongitude;
  43: optional string verbatimSRS;
  44: optional string waterBody;
}

struct GeologicalContext {
  1: optional string bed;
  2: optional string earliestAgeOrLowestStage;
  3: optional string earliestEonOrLowestEonothem;
  4: optional string earliestEpochOrLowestSeries;
  5: optional string earliestEraOrLowestErathem;
  6: optional string earliestPeriodOrLowestSystem;
  7: optional string formation;
  8: optional string geologicalContextID;
  9: optional string group;
  10: optional string highestBiostratigraphicZone;
  11: optional string latestAgeOrHighestStage;
  12: optional string latestEonOrHighestEonothem;
  13: optional string latestEpochOrHighestSeries;
  14: optional string latestEraOrHighestErathem;
  15: optional string latestPeriodOrHighestSystem;
  16: optional string lithostratigraphicTerms;
  17: optional string lowestBiostratigraphicZone;
  18: optional string member;
}

struct Identification {
  1: optional string bed;
  2: optional string earliestAgeOrLowestStage;
  3: optional string earliestEonOrLowestEonothem;
  4: optional string earliestEpochOrLowestSeries;
  5: optional string earliestEraOrLowestErathem;
  6: optional string earliestPeriodOrLowestSystem;
  7: optional string formation;
  8: optional string geologicalContextID;
  9: optional string group;
  10: optional string highestBiostratigraphicZone;
  11: optional string latestAgeOrHighestStage;
  12: optional string latestEonOrHighestEonothem;
  13: optional string latestEpochOrHighestSeries;
  14: optional string latestEraOrHighestErathem;
  15: optional string latestPeriodOrHighestSystem;
  16: optional string lithostratigraphicTerms;
  17: optional string lowestBiostratigraphicZone;
  18: optional string member;
}

struct Taxon {
  1: optional string acceptedNameUsage;
  2: optional string acceptedNameUsageID;
  3: optional string clazz;
  4: optional string family;
  5: optional string genus;
  6: optional string higherClassification;
  7: optional string infraspecificEpithet;
  8: optional string kingdom;
  9: optional string nameAccordingTo;
  10: optional string nameAccordingToID;
  11: optional string namePublishedIn;
  12: optional string namePublishedInID;
  13: optional string namePublishedInYear;
  14: optional string nomenclaturalCode;
  15: optional string nomenclaturalStatus;
  16: optional string order;
  17: optional string originalNameUsage;
  18: optional string originalNameUsageID;
  19: optional string parentNameUsage;
  20: optional string parentNameUsageID;
  21: optional string phylum;
  22: optional string scientificName;
  23: optional string scientificNameAuthorship;
  24: optional string scientificNameID;
  25: optional string specificEpithet;
  26: optional string subgenus;
  27: optional string taxonConceptID;
  28: optional string taxonID;
  29: optional string taxonRank;
  30: optional string taxonRemarks;
  31: optional string taxonomicStatus;
  32: optional string verbatimTaxonRank;
  33: optional string vernacularName;
}

struct ResourceRelationship {
  1: optional string relatedResourceID;
  2: optional string relationshipAccordingTo;
  3: optional string relationshipEstablishedDate;
  4: optional string relationshipOfResource;
  5: optional string relationshipRemarks;
  6: optional string resourceID;
  7: optional string resourceRelationshipID;
}

struct MeasurementOrFact {
  1: optional string measurementAccuracy;
  2: optional string measurementDeterminedBy;
  3: optional string measurementDeterminedDate;
  4: optional string measurementID;
  5: optional string measurementMethod;
  6: optional string measurementRemarks;
  7: optional string measurementType;
  8: optional string measurementUnit;
  9: optional string measurementValue;
}

union RecordPropertyValue {
  1: optional string accessRights;
  2: optional string basisOfRecord;
  3: optional string bibliographicCitation;
  4: optional string collectionCode;
  5: optional string collectionID;
  6: optional string dataGeneralizations;
  7: optional string datasetID;
  8: optional string datasetName;
  9: optional string dynamicProperties;
  10: optional string informationWithheld;
  11: optional string institutionCode;
  12: optional string institutionID;
  13: optional string language;
  14: optional string modified;
  15: optional string ownerInstitutionCode;
  16: optional string references;
  17: optional string rights;
  18: optional string rightsHolder;
  19: optional string type;
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

struct Pedigree {
  1: required i32 trueAsOfSecs;
}

union DataUnit {
  1: RecordProperty recordProperty;
}

struct Data {
  1: required Pedigree pedigree;
  2: required DataUnit dataunit;
}
