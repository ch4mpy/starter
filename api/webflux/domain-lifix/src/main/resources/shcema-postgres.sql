DROP TABLE IF EXISTS fault_attachment;
DROP TABLE IF EXISTS fault;

CREATE TABLE fault (
  id SERIAL PRIMARY KEY,
  desription VARCHAR(8096),
  opened_by VARCHAR(255),
  opened_on BIGINT,
  closed_by VARCHAR(255),
  closed_on BIGINT
);

CREATE TABLE fault_attachment (
  id SERIAL PRIMARY KEY,
  fault_id BIGINT NOT NULL,
  uri VARCHAR(1024) UNIQUE NOT NULL,
  extension VARCHAR(16) NOT NULL,
  FOREIGN KEY (fault_id) REFERENCES fault(id)
);