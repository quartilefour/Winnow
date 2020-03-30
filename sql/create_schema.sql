
CREATE TABLE "author" (
  "author_id" char(50)  PRIMARY KEY,
  "fore_name" char(30),
  "last_name" char(30)
);


CREATE TABLE "goterm" (
  "go_id" char(20)  PRIMARY KEY,
  "definition" text,
  "xrefs" varchar(200),
  "label" varchar(200)
);

CREATE TABLE "gene" (
  "gene_id" char(20)  PRIMARY KEY,
  "symbol" char(40),
  "type" varchar(300),
  "description" text,
  "publication_count" Integer,
  "synonym" varchar(300),
  "modification_date" Date,
  "count_modification_time" timestamp
);

CREATE TABLE "role" (
  "role_id" char(20)  PRIMARY KEY,
  "role_name" varchar(40)
);

CREATE TABLE "team" (
  "team_id" char(20)  PRIMARY KEY,
  "team_lead_id" char(20),
  "description" varchar(100)
);


CREATE TABLE "user" (
  "user_id" SERIAL PRIMARY KEY,
  "user_email" varchar(254),
  "user_password" char(60),
  "first_name" varchar(40),
  "last_name" varchar(40),
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "gene_relationship" (
  "relationship_id" char(50)  PRIMARY KEY,
  "name" varchar(50)
);

CREATE TABLE "address" (
  "user_id" INTEGER PRIMARY KEY REFERENCES "user" (user_id),
  "address1" varchar(100),
  "address2" varchar(100),
  "city" char(20),
  "zipcode" char(10),
  "state" char(20),
  "country" char(20)
);

CREATE TABLE "search" (
  "search_id" char(20)  PRIMARY KEY,
  "created_by" INTEGER REFERENCES "user" (user_id),
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
  "date_revised" Date,
  "title" text,
  "language" char(20)
);

CREATE TABLE "meshterm" (
  "mesh_id" char(20)  PRIMARY KEY,
  "publication_count" Integer,
  "date_created" Date,
  "date_revised" Date,
  "note" text,
  "supplemental_id" char(20),
  "name" char(200)
);

CREATE TABLE "meshterm_category" (
  "category_id" char(20)  PRIMARY KEY,
  "name" char(128)
);

CREATE TABLE "meshterm_tree" (
  "mesh_id" char(20) REFERENCES meshterm (mesh_id),
  "tree_parent_id" varchar(120),
  "tree_node_id" varchar(6),
  PRIMARY KEY ("mesh_id", "tree_parent_id", "tree_node_id")
);

CREATE TABLE "gene_gene" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "other_gene_id" char(20) REFERENCES gene (gene_id),
  "relationship_id" char(50) REFERENCES gene_relationship (relationship_id),
  PRIMARY KEY ("gene_id", "other_gene_id", "relationship_id")
);

CREATE TABLE "user_search_sharing" (
  "search_id" char(20) REFERENCES search (search_id),
  "user_id" INTEGER REFERENCES "user" (user_id),
  "shared_by" INTEGER REFERENCES "user" (user_id),
  "shared_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("search_id", "user_id")

);

CREATE TABLE "publication_author" (
  "publication_id" char(20) REFERENCES publication (publication_id),
  "author_id" char(50) REFERENCES author (author_id),
  "creation_date" date,
  PRIMARY KEY ("publication_id", "author_id")
);

CREATE TABLE "gene_publication" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "publication_id" char(20) REFERENCES publication (publication_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("gene_id", "publication_id")

);

CREATE TABLE "publication_meshterm" (
  "publication_id" char(20) REFERENCES publication (publication_id),
  "mesh_id" char(20) REFERENCES meshterm (mesh_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("publication_id", "mesh_id")
);

CREATE TABLE "user_team" (
  "team_id" char(20) REFERENCES team (team_id),
  "user_id" INTEGER REFERENCES "user" (user_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("team_id", "user_id")

);

CREATE TABLE "user_role" (
  "role_id" char(20) REFERENCES role (role_id),
  "user_id" INTEGER REFERENCES "user" (user_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("role_id", "user_id")
);

CREATE TABLE "gene_goterm" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "go_id" char(20)  REFERENCES goterm (go_id),
  "created_date" timestamp,
  "deleted_date" timestamp,
  PRIMARY KEY ("gene_id", "go_id")  
);

CREATE TABLE "gene_meshterm" (
  "gene_id" char(20) REFERENCES gene (gene_id),
  "mesh_id" char(20) REFERENCES meshterm (mesh_id),
  "p_value" float,
  "publication_count" Integer,
  PRIMARY KEY ("gene_id", "mesh_id")
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
