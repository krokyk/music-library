DROP TABLE albums;
DROP TABLE bands;

CREATE TABLE bands (
    name VARCHAR(500) PRIMARY KEY,
    url VARCHAR(1000)
);

CREATE TABLE albums (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name VARCHAR(500) NOT NULL,
    release_year INTEGER NOT NULL,
    checked SMALLINT NOT NULL,
    band_name VARCHAR(500) NOT NULL,
    CONSTRAINT fk_bands FOREIGN KEY (band_name) REFERENCES bands (name),
    CONSTRAINT albums_listened_bool CHECK (checked = 0 OR checked = 1)
);
