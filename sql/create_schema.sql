
CREATE TABLE "author" (
  "author_id" char(50)  PRIMARY KEY,
  "fore_name" char(30),
  "last_name" char(30)
);


CREATE TABLE "goterm" (
  "go_id" char(20)  PRIMARY KEY,
  "definition" varchar(50),
  "xrefs" varchar(30),
  "type" varchar(30)
);

CREATE TABLE "gene" (
  "gene_id" char(20)  PRIMARY KEY,
  "symbol" char(40),
  "type" char(20),
  "description" varchar(100),
  "publication_count" Integer,
  "synonym" char(20),
  "modification_date" Date,
  "count_modification_time" timestamp
);

CREATE TABLE "team" (
  "team_id" char(20)  PRIMARY KEY,
  "team_lead_id" char(20),
  "description" varchar(100)
);


CREATE TABLE "user_extension" (
  "user_id" char(20) PRIMARY KEY,
  "user_email" varchar(100),
  "first_name" varchar(40),
  "last_name" varchar(40)
);

CREATE TABLE "gene_relationship" (
  "relationship_id" char(20)  PRIMARY KEY,
  "name" varchar(50)
);

CREATE TABLE "address" (
  "user_id" char(50)  PRIMARY KEY REFERENCES user_extension (user_id),
  "address1" varchar(100),
  "address2" varchar(100),
  "city" char(20),
  "zipcode" char(10),
  "state" char(20),
  "country" char(20)
);

CREATE TABLE "search" (
  "search_id" char(20)  PRIMARY KEY,
  "created_by" char(20) REFERENCES user_extension (user_id),
  "created_date" timestamp,
  "search_name" varchar(20),
  "search_query" JSON,
  "deleted_date" timestamp,
  "updated_at" timestamp,
  "team_id" char(20) REFERENCES team (team_id),
  "query_type" char(20)
);

CREATE TABLE "publication" (
  "publication_id" char(20)  PRIMARY KEY,
  "completed_date" Date,
  "date_revised" Date
);

CREATE TABLE "meshterm" (
  "mesh_id" char(20)  PRIMARY KEY,
  "parent_descriptor_id" char(20),
  "publication_count" Integer,
  "date_created" Date,
  "date_revised" Date,
  "note" varchar(100),
  "supplemental_id" char(20),
  "name" char(30)
);

CREATE TABLE "gene_gene" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "other_gene_id" char(20) REFERENCES gene (gene_id),
  "relationship_id" char(20) REFERENCES gene_relationship (relationship_id),
  PRIMARY KEY ("gene_id", "other_gene_id", "relationship_id")
);

CREATE TABLE "user_search_sharing" (
  "search_id" char(20) REFERENCES search (search_id),
  "user_id" char(20) REFERENCES user_extension (user_id),
  "shared_by" char(20) REFERENCES user_extension (user_id),
  "shared_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("search_id", "user_id")

);

CREATE TABLE "publication_author" (
  "publication_id" char(20) REFERENCES publication (publication_id),
  "author_id" char(50) REFERENCES author (author_id),
  PRIMARY KEY ("publication_id", "author_id")
);

CREATE TABLE "gene_publication" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "publication_id" char(20) REFERENCES publication (publication_id),
  PRIMARY KEY ("gene_id", "publication_id")

);

CREATE TABLE "publication_meshterm" (
  "publication_id" char(20) REFERENCES publication (publication_id),
  "mesh_id" char(20) REFERENCES meshterm (mesh_id),
  PRIMARY KEY ("publication_id", "mesh_id")
);

CREATE TABLE "user_team" (
  "team_id" char(20) REFERENCES team (team_id),
  "user_id" char(20) REFERENCES user_extension (user_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("team_id", "user_id")

);

CREATE TABLE "gene_goterm" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "go_id" char(20)  REFERENCES goterm (go_id),
  PRIMARY KEY ("gene_id", "go_id")  
);

CREATE TABLE "gene_meshterm" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "mesh_id" char(20) REFERENCES meshterm (mesh_id),
  "p-value" float,
  "publication_count" Integer,
  PRIMARY KEY ("gene_id", "mesh_id")
);