
CREATE TABLE "author" (
  "author_id" varchar (100)  PRIMARY KEY,
  "fore_name" varchar(50),
  "last_name" varchar(50)
);


CREATE TABLE "goterm" (
  "go_id" varchar(20)  PRIMARY KEY,
  "definition" text,
  "xrefs" varchar(200),
  "label" varchar(200)
);

CREATE TABLE "gene" (
  "gene_id" varchar (20)  PRIMARY KEY,
  "symbol" varchar(1024),
  "type" varchar(1024),
  "description" text,
  "publication_count" Integer,
  "synonym" varchar(1024),
  "tax_id" Integer,
  "modification_date" Date,
  "count_modification_time" timestamp
);

CREATE TABLE "role" (
  "role_id" varchar(20)  PRIMARY KEY,
  "role_name" varchar(40)
);

CREATE TABLE "team" (
  "team_id" varchar(20)  PRIMARY KEY,
  "team_lead_id" varchar(20),
  "description" varchar(100)
);


CREATE TABLE "user" (
  "user_id" SERIAL PRIMARY KEY,
  "user_email" varchar(254),
  "user_password" varchar(60),
  "first_name" varchar(40),
  "last_name" varchar(40),
  "created_at" timestamp,
  "updated_at" timestamp,
  "reset_token" char(36)
);

CREATE TABLE "gene_relationship" (
  "relationship_id" varchar(50)  PRIMARY KEY,
  "name" varchar(50)
);

CREATE TABLE "address" (
  "user_id" INTEGER PRIMARY KEY REFERENCES "user" (user_id),
  "address1" varchar(100),
  "address2" varchar(100),
  "city" varchar(20),
  "zipcode" varchar(10),
  "state" varchar(20),
  "country" varchar(20)
);

CREATE TABLE "search"
(
    "search_id"    BIGSERIAL PRIMARY KEY,
    "created_by"   INTEGER REFERENCES "user" (user_id),
    "created_date" timestamp,
    "search_name"  varchar(20),
    "search_query" jsonb,
    "updated_at"   timestamp,
    "team_id"      varchar(20) REFERENCES team (team_id)
);

CREATE TABLE "publication"
(
    "publication_id" varchar(20) PRIMARY KEY,
    "completed_date" Date,
    "date_revised"   Date,
    "title"          text
);

CREATE TABLE "meshterm" (
  "mesh_id" varchar (20)  PRIMARY KEY,
  "publication_count" Integer,
  "date_created" Date,
  "date_revised" Date,
  "note" text,
  "supplemental_id" varchar(20),
  "name" varchar(200)
);

CREATE TABLE "meshterm_category" (
  "category_id" varchar(20)  PRIMARY KEY,
  "name" varchar(128)
);

CREATE TABLE "meshterm_tree" (
  "mesh_id" varchar (20) REFERENCES meshterm (mesh_id),
  "tree_parent_id" varchar(120),
  "tree_node_id" varchar(6),
  PRIMARY KEY ("mesh_id", "tree_parent_id", "tree_node_id")
);

CREATE TABLE "gene_gene" (
  "tax_id" Integer,
  "gene_id" varchar (20) REFERENCES gene (gene_id),
  "other_gene_id" varchar (20) REFERENCES gene (gene_id),
  "other_tax_id"  Integer,
  "relationship_id" varchar(50) REFERENCES gene_relationship (relationship_id),
  PRIMARY KEY ("tax_id", "gene_id", "other_tax_id", "other_gene_id", "relationship_id")
);

CREATE TABLE "user_search_sharing"
(
    "search_id"    BIGINT REFERENCES "search" (search_id),
    "user_id"      INTEGER REFERENCES "user" (user_id),
    "shared_by"    INTEGER REFERENCES "user" (user_id),
    "shared_date"  timestamp,
    "deleted_date" timestamp,
    PRIMARY KEY ("search_id", "user_id")
);

CREATE TABLE "publication_author" (
  "publication_id" varchar(20) REFERENCES publication (publication_id),
  "author_id" varchar(100) REFERENCES author (author_id),
  "creation_date" date,
  PRIMARY KEY ("publication_id", "author_id")
);

CREATE TABLE "gene_publication" (
  "gene_id" varchar (20) REFERENCES gene (gene_id),
  "publication_id" varchar(20) REFERENCES publication (publication_id),
  "tax_id" Integer,
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("tax_id","gene_id", "publication_id")

);

CREATE TABLE "publication_meshterm" (
  "publication_id" varchar(20) REFERENCES publication (publication_id),
  "mesh_id" varchar (20) REFERENCES meshterm (mesh_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("publication_id", "mesh_id")
);

CREATE TABLE "user_team" (
  "team_id" varchar(20) REFERENCES team (team_id),
  "user_id" INTEGER REFERENCES "user" (user_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("team_id", "user_id")

);

CREATE TABLE "user_role" (
  "role_id" varchar(20) REFERENCES role (role_id),
  "user_id" INTEGER REFERENCES "user" (user_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("role_id", "user_id")
);

CREATE TABLE "gene_goterm" (
  "gene_id" varchar (20) REFERENCES gene (gene_id),
  "go_id" varchar(20),
  "tax_id" Integer,
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("tax_id", "gene_id", "go_id")
);

CREATE TABLE "gene_meshterm" (
  "gene_id" varchar (20) REFERENCES gene (gene_id),
  "mesh_id" varchar (20) REFERENCES meshterm (mesh_id),
  "p_value" float,
  "publication_count" Integer,
  PRIMARY KEY ("gene_id", "mesh_id")
);

CREATE TABLE "gene_association" (
  "gene_id" varchar (20) REFERENCES gene (gene_id),
  "other_gene_id" varchar(20) REFERENCES gene (gene_id),
  "p_value" float,
  "publication_count" Integer,
  PRIMARY KEY ("gene_id", "other_gene_id")
);

/* Following tables are used by spring boot scheduler */

CREATE TABLE BATCH_JOB_INSTANCE  (
	JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT ,
	JOB_NAME VARCHAR(100) NOT NULL,
	JOB_KEY VARCHAR(32) NOT NULL,
	constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ;

CREATE TABLE BATCH_JOB_EXECUTION  (
	JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT  ,
	JOB_INSTANCE_ID BIGINT NOT NULL,
	CREATE_TIME TIMESTAMP NOT NULL,
	START_TIME TIMESTAMP DEFAULT NULL ,
	END_TIME TIMESTAMP DEFAULT NULL ,
	STATUS VARCHAR(10) ,
	EXIT_CODE VARCHAR(2500) ,
	EXIT_MESSAGE VARCHAR(2500) ,
	LAST_UPDATED TIMESTAMP,
	JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL,
	constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
	references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ;

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS  (
	JOB_EXECUTION_ID BIGINT NOT NULL ,
	TYPE_CD VARCHAR(6) NOT NULL ,
	KEY_NAME VARCHAR(100) NOT NULL ,
	STRING_VAL VARCHAR(250) ,
	DATE_VAL TIMESTAMP DEFAULT NULL ,
	LONG_VAL BIGINT ,
	DOUBLE_VAL DOUBLE PRECISION ,
	IDENTIFYING CHAR(1) NOT NULL ,
	constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE BATCH_STEP_EXECUTION  (
	STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT NOT NULL,
	STEP_NAME VARCHAR(100) NOT NULL,
	JOB_EXECUTION_ID BIGINT NOT NULL,
	START_TIME TIMESTAMP NOT NULL ,
	END_TIME TIMESTAMP DEFAULT NULL ,
	STATUS VARCHAR(10) ,
	COMMIT_COUNT BIGINT ,
	READ_COUNT BIGINT ,
	FILTER_COUNT BIGINT ,
	WRITE_COUNT BIGINT ,
	READ_SKIP_COUNT BIGINT ,
	WRITE_SKIP_COUNT BIGINT ,
	PROCESS_SKIP_COUNT BIGINT ,
	ROLLBACK_COUNT BIGINT ,
	EXIT_CODE VARCHAR(2500) ,
	EXIT_MESSAGE VARCHAR(2500) ,
	LAST_UPDATED TIMESTAMP,
	constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT  (
	STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
	SERIALIZED_CONTEXT TEXT ,
	constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
	references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ;

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT  (
	JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
	SERIALIZED_CONTEXT TEXT ,
	constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;

INSERT INTO meshterm_category (category_id, name) VALUES ('A', 'Anatomy');
INSERT INTO meshterm_category (category_id, name) VALUES ('B', 'Organisms');
INSERT INTO meshterm_category (category_id, name) VALUES ('C', 'Diseases');
INSERT INTO meshterm_category (category_id, name) VALUES ('D', 'Chemicals and Drugs');
INSERT INTO meshterm_category (category_id, name) VALUES ('E', 'Analytical, Diagnostic and Therapeutic Techniques, and Equipment');
INSERT INTO meshterm_category (category_id, name) VALUES ('F', 'Psychiatry and Psychology');
INSERT INTO meshterm_category (category_id, name) VALUES ('G', 'Phenomena and Processes');
INSERT INTO meshterm_category (category_id, name) VALUES ('H', 'Disciplines and Occupations');
INSERT INTO meshterm_category (category_id, name) VALUES ('I', 'Anthropology, Education, Sociology, and Social Phenomena');
INSERT INTO meshterm_category (category_id, name) VALUES ('J', 'Technology, Industry, and Agriculture');
INSERT INTO meshterm_category (category_id, name) VALUES ('K', 'Humanities');
INSERT INTO meshterm_category (category_id, name) VALUES ('L', 'Information Science');
INSERT INTO meshterm_category (category_id, name) VALUES ('M', 'Named Groups');
INSERT INTO meshterm_category (category_id, name) VALUES ('N', 'Health Care');
INSERT INTO meshterm_category (category_id, name) VALUES ('V', 'Publication Characteristics');
INSERT INTO meshterm_category (category_id, name) VALUES ('Z', 'Geographicals');