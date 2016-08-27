DELIMITER $$

DROP PROCEDURE IF EXISTS split$$

CREATE PROCEDURE split()
  BEGIN
    DECLARE _done INT DEFAULT FALSE;
    DECLARE _id INT;
    DECLARE _names VARCHAR(50);
    DECLARE _name VARCHAR(50);
    DECLARE _cursor CURSOR FOR SELECT id, name FROM names;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET _done = TRUE;

    OPEN _cursor;

    DROP TABLE IF EXISTS temp_table;
    CREATE TEMPORARY TABLE temp_table (
      ID   INT,
      NAME VARCHAR(50)
    );

    WHILE _done <> TRUE DO
      FETCH _cursor INTO _id, _names;

      WHILE length(_names) > 0 DO

        IF instr(_names, '|') > 0
        THEN
          SET _name = substring(_names, 1, instr(_names, '|') - 1);
          SET _names = substring(_names, instr(_names, '|') + 1);
        ELSE
          SET _name = _names;
          SET _names = '';
        END IF;

        INSERT INTO temp_table VALUES (_id, _name);
      END WHILE;
    END WHILE;

    CLOSE _cursor;

    SELECT * FROM temp_table;
  END$$

DELIMITER ;
