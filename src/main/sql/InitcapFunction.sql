DELIMITER $$

DROP FUNCTION IF EXISTS initcap$$

CREATE FUNCTION initcap(string VARCHAR(255)) RETURNS VARCHAR(255)
  BEGIN
    DECLARE _result VARCHAR(255);
    SET _result = '';
    SET string = lower(string);
    WHILE length(string) > 0 DO
      SET string = concat(upper(substring(string, 1, 1)), substring(string, 2));
      IF instr(string, ' ') > 0
      THEN
        SET _result = concat(_result, ' ', substring(string, 1, instr(string, ' ') - 1));
        SET string = substring(string, instr(string, ' ') + 1);
      ELSE
        SET _result = concat(_result, ' ', string);
        SET string = '';
      END IF;
    END WHILE;
    RETURN ltrim(_result);
  END$$

DELIMITER ;
