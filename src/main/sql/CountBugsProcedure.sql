DELIMITER $$

DROP PROCEDURE IF EXISTS countBugs$$

CREATE PROCEDURE countBugs(fromDate DATE, toDate DATE)
  BEGIN
    SELECT COUNT(id) AS number_of_bugs
    FROM bugs
    WHERE
      fromDate BETWEEN open_date AND close_date OR
      toDate BETWEEN open_date AND close_date;
  END$$

DELIMITER ;
